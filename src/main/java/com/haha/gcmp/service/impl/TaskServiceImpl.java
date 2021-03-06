package com.haha.gcmp.service.impl;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.JsonSyntaxException;
import com.haha.gcmp.config.propertites.GcmpProperties;
import com.haha.gcmp.exception.BadRequestException;
import com.haha.gcmp.exception.ServiceException;
import com.haha.gcmp.model.dto.TaskDTO;
import com.haha.gcmp.model.entity.Image;
import com.haha.gcmp.model.entity.Task;
import com.haha.gcmp.model.entity.User;
import com.haha.gcmp.model.enums.TaskStatusType;
import com.haha.gcmp.model.params.TaskParam;
import com.haha.gcmp.repository.TaskMapper;
import com.haha.gcmp.service.*;
import com.haha.gcmp.utils.FileUtils;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;
import io.kubernetes.client.util.Yaml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import static com.haha.gcmp.model.enums.TaskStatusType.*;

/**
 * Task service implementation.
 *
 * @author SZFHH
 * @date 2020/11/1
 */
@Service
public class TaskServiceImpl implements TaskService {
    private ServerStatusService serverStatusService;
    private final GcmpProperties gcmpProperties;
    private DockerService dockerService;
    private UserService userService;
    private DataService dataService;
    private final CoreV1Api k8sApi;
    private final TaskMapper taskMapper;
    private final ScheduledExecutorService executor;

    public TaskServiceImpl(GcmpProperties gcmpProperties, TaskMapper taskMapper) {
        this.gcmpProperties = gcmpProperties;
        this.taskMapper = taskMapper;

        try {
            ApiClient client = ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(new FileReader(gcmpProperties.getK8sConfigFilePath()))).build();
            Configuration.setDefaultApiClient(client);
            k8sApi = new CoreV1Api();
        } catch (IOException e) {
            throw new ServiceException("没找到k8s config配置文件", e);
        }
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("InMemoryCache-pool-%d").build();
        executor = new ScheduledThreadPoolExecutor(1, namedThreadFactory);
        executor.scheduleAtFixedRate(new TaskCleaner(), 0, gcmpProperties.getTaskClearPeriod(), TimeUnit.SECONDS);

    }

    @Autowired
    public void setServerStatusService(ServerStatusService serverStatusService) {
        this.serverStatusService = serverStatusService;
    }

    @Autowired
    public void setDockerService(DockerService dockerService) {
        this.dockerService = dockerService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setDataService(DataService dataService) {
        this.dataService = dataService;
    }

    @Override
    public int submitTask(TaskParam taskParam) {
        int gpus = taskParam.getGpus();
        int availableGpus = serverStatusService.requestForGpus(taskParam.getServerId(), gpus);
        if (availableGpus == -1) {
            String cmd = taskParam.getCmd();
            String[] extraPyPkg = Arrays.stream(taskParam.getExtraPythonPackage().split(" ")).filter(s -> !"".equals(s)).toArray(String[]::new);
            int environmentId = taskParam.getImageId();
            Image image = dockerService.getImage(environmentId);
            String podName = "gcmp-" + userService.getCurrentUser().getId() + System.currentTimeMillis();
            String podFile = generatePodFile(image, gpus, cmd, extraPyPkg, podName, taskParam.getPyVersion(), taskParam.getServerId());
            V1Pod v1pod;
            try {
                v1pod = (V1Pod) Yaml.load(podFile);
            } catch (IOException e) {
                throw new ServiceException("解析podFile异常", e);
            }
            try {
                k8sApi.createNamespacedPod("default", v1pod, true, "true", null);
            } catch (ApiException e) {
                throw new ServiceException("创建pod异常", e);
            }
            Task task = convertFrom(taskParam, podName);
            taskMapper.insert(task);
            return -1;
        }
        return availableGpus;
    }

    @Override
    public List<TaskDTO> listTasksPerUser() {
        User cur = userService.getCurrentUser();
        List<Task> tasks = taskMapper.listByUserId(cur.getId());
        List<TaskDTO> taskDTOs = new ArrayList<>();
        for (Task task : tasks) {
            taskDTOs.add(convertFrom(task));
        }
        return taskDTOs;
    }

    private Task convertFrom(TaskParam taskParam, String podName) {
        Task task = new Task();
        task.setCmd(taskParam.getCmd());
        task.setGpus(taskParam.getGpus());
        task.setImageId(taskParam.getImageId());
        task.setServerId(taskParam.getServerId());
        task.setUserId(userService.getCurrentUser().getId());
        task.setPodName(podName);
        task.setStatus(PENDING);
        task.setStartTime(System.currentTimeMillis());
        return task;
    }

    private TaskDTO convertFrom(Task task) {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(task.getId());
        taskDTO.setGpus(task.getGpus());
        taskDTO.setCmd(task.getCmd());
        taskDTO.setServerId(task.getServerId());
        taskDTO.setGpuSeries(gcmpProperties.getServerProperties().get(task.getServerId()).getGpuSeries());
        Image image = dockerService.getImage(task.getImageId());
        taskDTO.setImage(image.getAlias());
        taskDTO.setStatus(task.getStatus().getDesc());
        taskDTO.setStartTime(task.getStartTime());
        return taskDTO;
    }

    private TaskStatusType getPodStatus(String podName) throws ApiException {

        V1Pod v1Pod = k8sApi.readNamespacedPodStatus(podName, "default", "true");

        return TaskStatusType.valueFrom(v1Pod.getStatus().getPhase());
    }


    @Override
    public List<TaskDTO> listAllTasks() {
        List<Task> tasks = taskMapper.listAll();
        List<TaskDTO> taskDTOs = new ArrayList<>();
        for (Task task : tasks) {
            taskDTOs.add(convertFrom(task));
        }
        return taskDTOs;
    }

    @Override
    public void cancelTask(int taskId) {
        Task task = taskMapper.getById(taskId);
        // 删除状态优先级是最高的，无论当前任务状态是什么都可以覆盖，TaskCleaner看到DELETED状态就不会更新任务状态了。
        task.setStatus(DELETED);
        taskMapper.updateStatus(task);
        // 可能和TaskCleaner处理任务完成或失败的逻辑有并发冲突，即使让TaskCleaner抢先了也没关系。
        // 两者都会删除pod，归还GPU。
        if (taskMapper.casUpdateRemoved(taskId) != 0) {
            String podName = task.getPodName();
            try {
                k8sApi.deleteNamespacedPod(podName, "default", "true", null, null,
                    2, null, null);
            } catch (ApiException e) {
                // RETRY状态见TaskCleaner中的注释
                task.setStatus(RETRY);
                taskMapper.insert(task);
                throw new ServiceException("k8s删除pod异常, pod名：" + podName, e);
            } catch (JsonSyntaxException ignored) {
            }
            serverStatusService.returnGpus(task.getServerId(), task.getGpus());

        }
    }

    @Override
    public String getLog(int taskId) {
        Task task = taskMapper.getById(taskId);
        TaskStatusType status = task.getStatus();
        if (status == DELETED) {
            throw new BadRequestException("不能获取删除了的训练任务的日志");
        } else if (status == SUCCEEDED || status == FAILED) {
            return getLogFromFile(task);
        } else {
            try {
                return getLogFromPod(task.getPodName());
            } catch (ServiceException e) {
                if (e.getCause().getMessage().endsWith("Not Found")) {
                    return getLogFromFile(task);
                } else {
                    throw e;
                }
            }
        }

    }

    @Override
    public String getTaskLogPath(Task task) {

        return FileUtils.joinPaths(gcmpProperties.getTaskLogRoot(), String.valueOf(task.getUserId()), String.valueOf(task.getId()));

    }

    @Override
    public List<Task> listByUserId(int userId) {
        return taskMapper.listByUserId(userId);
    }

    private String getLogFromFile(Task task) {
        try {
            return FileUtils.readFile(Paths.get(getTaskLogPath(task)));
        } catch (IOException ioe) {
            throw new ServiceException("读取训练任务日志文件异常，任务id" + task.getId(), ioe);
        }
    }

    private String getLogFromPod(String podName) {
        try {
            return k8sApi.readNamespacedPodLog(podName, "default", null, false, gcmpProperties.getTaskLogLimitBytes(), "true", false, null, null, false);
        } catch (ApiException e) {
            throw new ServiceException("从pod获取日志异常，podName：" + podName, e);
        }
    }

    private String generatePodFile(Image image, int gpus, String cmd, String[] extraPyPkg, String podName, int pyVersion, int serverId) {
        String pip = "pip3";
        if (pyVersion == 2) {
            pip = "pip";
        }
        StringBuilder sb = new StringBuilder();
        if (extraPyPkg.length > 0) {
            for (String pkg : extraPyPkg) {
                sb.
                    append(pip).
                    append(" install -i ").
                    append(gcmpProperties.getPypiSource()).
                    append(" ").append(pkg).append(" && ");
            }
        }
        sb.append(cmd);
        String withPyPkgCmd = sb.toString();

        String userDataPath = dataService.getUserDataPath("");
        String commonDataRoot = gcmpProperties.getCommonDataRoot();
        String nodeName = gcmpProperties.getServerProperties().get(serverId).getHostName();
        String podFile = String.format(
            "apiVersion: v1\n" +
                "kind: Pod\n" +
                "metadata:\n" +
                "  name: %s\n" +
                "spec:\n" +
                "  restartPolicy: Never\n" +
                "  containers:\n" +
                "    - name: gcmp-container\n" +
                "      image: %s\n" +
                "      workingDir: %s\n" +
                "      command: [ \"/bin/bash\", \"-c\", \"%s\" ]\n" +
                "      volumeMounts:\n" +
                "      - mountPath: %s\n" +
                "        name: user-data\n" +
                "      - mountPath: %s\n" +
                "        name: common-data\n" +
                "      resources:\n" +
                "        limits:\n" +
                "          nvidia.com/gpu: %s # requesting 1 GPUs\n" +
                "  volumes:\n" +
                "  - name: user-data\n" +
                "    hostPath:\n" +
                "      path: %s\n" +
                "      type: DirectoryOrCreate\n" +
                "  - name: common-data\n" +
                "    hostPath:\n" +
                "      path: %s\n" +
                "      type: DirectoryOrCreate\n" +
                "  nodeName: %s",
            podName, image.getTag(), userDataPath, withPyPkgCmd, userDataPath, commonDataRoot, gpus, userDataPath, commonDataRoot, nodeName.toLowerCase());
        return podFile;
    }

    /**
     * 定时从K8S pod获取最新的状态，更新数据库，释放GPU
     * 用removed字段控制删除pod时的并发冲突，removed为1代表已经有代码在进行pod删除了。
     * 用status字段表示任务状态，除了四个pod状态：PENDING,RUNNING,SUCCEEDED,FAILED，还加了两个DELETED和RETRY。
     * DELETED表示用户要删掉的任务，RETRY表示删除pod时出现异常，需要再次尝试的任务。
     * 状态为DELETED的任务记录每隔一段时间一定会被删除，RETRY的任务记录在再次尝试成功后会被删除。
     */
    private class TaskCleaner implements Runnable {
        private final Logger log = LoggerFactory.getLogger(TaskCleaner.class);

        @Override
        public void run() {
            List<Task> tasks = taskMapper.listAll();
            for (Task task : tasks) {
                TaskStatusType status = task.getStatus();
                int taskId = task.getId();

                // DELETED表示用户要删除的任务，pod在用户发起删除指令时删除，这里只需要删除数据库记录和持久化的日志。
                if (status == DELETED) {
                    taskMapper.removeById(taskId);

                    try {
                        FileUtils.deleteFile(Paths.get(getTaskLogPath(task)));
                        FileUtils.deleteDirIfEmpty(Paths.get(gcmpProperties.getTaskLogRoot(), String.valueOf(task.getUserId())));
                    } catch (IOException e) {
                        log.error("删除训练任务日志异常，任务id" + taskId, e);
                    }

                    // RUNNING表示任务正在运行，需要从pod获取最新的状态
                } else if (status == RUNNING) {
                    TaskStatusType latestStatus = null;

                    try {
                        latestStatus = getPodStatus(task.getPodName());
                    } catch (ApiException e) {
                        log.error("k8s获取pod状态异常pod名：" + task.getPodName(), e);
                    }
                    // 如果pod已经完成了任务，就删除pod，持久化日志，释放GPU，更新数据库
                    if (latestStatus == SUCCEEDED || latestStatus == FAILED) {
                        // 尝试删除pod节点，可能有并发冲突的地方是cancelTask，也就是用户发起删除指令。
                        // 如果删除命令抢先了，就不用往下执行持久化日志之类的操作了，反正任务最终会被删掉。
                        if (taskMapper.casUpdateRemoved(taskId) != 0) {

                            String podName = task.getPodName();
                            try {
                                FileUtils.writeFile(Paths.get(getTaskLogPath(task)), getLogFromPod(podName));
                            } catch (IOException e) {
                                log.error("保存训练任务日志异常，任务id" + taskId, e);
                            }
                            task.setStatus(latestStatus);
                            // 如果在上面代码执行过程中，用户发起了删除命令，就不更新状态
                            taskMapper.casUpdateStatus(task);
                            try {
                                try {
                                    k8sApi.deleteNamespacedPod(podName, "default", "true", null, null, 2, null, null);
                                } catch (JsonSyntaxException ignored) {
                                }
                                serverStatusService.returnGpus(task.getServerId(), task.getGpus());
                            } catch (ApiException e) {
                                // 如果删除pod的时候出现网络异常等问题，就把这个任务状态设为RETRY，重新加入数据库中
                                task.setStatus(RETRY);
                                taskMapper.insert(task);
                                log.error("k8s删除pod异常，pod名：" + podName, e);
                            }

                        }
                    }
                    // 正在创建中的任务状态为PENDING
                } else if (status == PENDING) {
                    TaskStatusType latestStatus = null;
                    try {
                        latestStatus = getPodStatus(task.getPodName());
                    } catch (ApiException e) {
                        log.error("k8s获取pod状态异常pod名：" + task.getPodName(), e);
                    }
                    if (latestStatus != PENDING) {
                        // 不管最新的状态是什么，都先设置为RUNNING，之后由RUNNING处理逻辑再去更新
                        task.setStatus(RUNNING);
                        // 不能和删除命令冲突
                        taskMapper.casUpdateStatus(task);
                    }

                    // 再次尝试删除之前删除失败的pod
                } else if (status == RETRY) {
                    String podName = task.getPodName();
                    try {
                        k8sApi.deleteNamespacedPod(podName, "default", "true", null, null,
                            2, null, null);
                        serverStatusService.returnGpus(task.getServerId(), task.getGpus());
                        taskMapper.removeById(task.getId());
                    } catch (ApiException e) {
                        log.error("retry: k8s删除pod异常，pod名：" + podName, e);
                    } catch (JsonSyntaxException ignored) {
                    }
                }
            }
        }
    }

}
