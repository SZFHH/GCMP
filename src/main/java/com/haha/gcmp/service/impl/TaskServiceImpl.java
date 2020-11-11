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
    private final ServerStatusService serverStatusService;
    private final GcmpProperties gcmpProperties;
    private final DockerService dockerService;
    private final UserService userService;
    private final DataService dataService;
    private final CoreV1Api k8sApi;
    private final TaskMapper taskMapper;
    private final ScheduledExecutorService executor;

    public TaskServiceImpl(ServerStatusService serverStatusService, GcmpProperties gcmpProperties, DockerService dockerService, UserService userService, DataService dataService, TaskMapper taskMapper) {
        this.serverStatusService = serverStatusService;
        this.gcmpProperties = gcmpProperties;
        this.dockerService = dockerService;
        this.userService = userService;
        this.dataService = dataService;
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
            task.setStatus(PENDING);
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
        task.setStatus(getPodStatus(podName));
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

    private TaskStatusType getPodStatus(String podName) {
        V1Pod v1Pod;
        try {
            v1Pod = k8sApi.readNamespacedPodStatus(podName, "default", "true");
        } catch (ApiException e) {
            throw new ServiceException("无法获取pod信息，pod名：" + podName, e);
        }
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
        task.setStatus(DELETED);
        taskMapper.updateStatus(task);
        if (taskMapper.casUpdateRemoved(taskId) != 0) {
            String podName = task.getPodName();
            try {
                k8sApi.deleteNamespacedPod(podName, "default", "true", null, null,
                    2, null, null);
            } catch (ApiException e) {
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
            return getLogFromFile(task.getId());
        } else {
            try {
                return getLogFromPod(task.getPodName());
            } catch (ServiceException e) {
                if (e.getCause().getMessage().endsWith("Not Found")) {
                    return getLogFromFile(task.getId());
                } else {
                    throw e;
                }
            }
        }

    }

    @Override
    public String getTaskLogPath(int taskId) {
        User user = userService.getCurrentUser();
        return FileUtils.joinPaths(gcmpProperties.getTaskLogRoot(), user.getUsername(), String.valueOf(taskId));

    }

    private String getLogFromFile(int taskId) {
        try {
            return FileUtils.readFile(Paths.get(getTaskLogPath(taskId)));
        } catch (IOException ioe) {
            throw new ServiceException("读取训练任务日志文件异常，任务id" + taskId, ioe);
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

        String mountPath = dataService.getUserDataPath("");
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
                "        name: gcmp-volume\n" +
                "      resources:\n" +
                "        limits:\n" +
                "          nvidia.com/gpu: %s # requesting 1 GPUs\n" +
                "  volumes:\n" +
                "  - name: gcmp-volume\n" +
                "    hostPath:\n" +
                "      # directory location on host\n" +
                "      path: %s\n" +
                "      # this field is optional\n" +
                "      type: DirectoryOrCreate\n" +
                "  nodeName: %s",
            podName, image.getTag(), mountPath, withPyPkgCmd, mountPath, gpus, mountPath, nodeName.toLowerCase());
        return podFile;
    }

    private class TaskCleaner implements Runnable {
        private final Logger log = LoggerFactory.getLogger(TaskCleaner.class);

        @Override
        public void run() {
            List<Task> tasks = taskMapper.listAll();
            for (Task task : tasks) {
                TaskStatusType status = task.getStatus();
                int taskId = task.getId();

                if (status == DELETED) {

                    taskMapper.removeById(taskId);

                    try {
                        FileUtils.deleteFile(Paths.get(getTaskLogPath(taskId)));
                    } catch (IOException e) {
                        log.error("删除训练任务日志异常，任务id" + taskId, e);
                    }
                } else if (status == RUNNING) {
                    TaskStatusType latestStatus = getPodStatus(task.getPodName());
                    if (latestStatus == SUCCEEDED || latestStatus == FAILED) {
                        if (taskMapper.casUpdateRemoved(taskId) != 0) {

                            String podName = task.getPodName();
                            try {
                                FileUtils.writeFile(Paths.get(getTaskLogPath(taskId)), getLogFromPod(podName));
                            } catch (IOException e) {
                                log.error("保存训练任务日志异常，任务id" + taskId, e);
                            }
                            task.setStatus(latestStatus);
                            taskMapper.casUpdateStatus(task);
                            try {
                                try {
                                    k8sApi.deleteNamespacedPod(podName, "default", "true", null, null, 2, null, null);
                                } catch (JsonSyntaxException ignored) {
                                }
                                serverStatusService.returnGpus(task.getServerId(), task.getGpus());
                            } catch (ApiException e) {
                                task.setStatus(RETRY);
                                taskMapper.insert(task);
                                log.error("k8s删除pod异常，pod名：" + podName, e);
                            }

                        }
                    }
                } else if (status == PENDING) {
                    TaskStatusType latestStatus = getPodStatus(task.getPodName());
                    if (latestStatus != PENDING) {
                        task.setStatus(RUNNING);
                        taskMapper.casUpdateStatus(task);
                    }
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
