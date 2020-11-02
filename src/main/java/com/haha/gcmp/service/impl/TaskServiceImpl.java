package com.haha.gcmp.service.impl;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.haha.gcmp.config.propertites.GcmpProperties;
import com.haha.gcmp.exception.BadRequestException;
import com.haha.gcmp.exception.ServiceException;
import com.haha.gcmp.model.dto.TaskDTO;
import com.haha.gcmp.model.entity.Image;
import com.haha.gcmp.model.entity.Task;
import com.haha.gcmp.model.entity.User;
import com.haha.gcmp.model.params.TaskParam;
import com.haha.gcmp.model.support.TaskSubmitResult;
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
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
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
    public TaskSubmitResult submitTask(TaskParam taskParam) {
        int gpus = taskParam.getGpus();
        int availableGpus = serverStatusService.requestForGpus(taskParam.getServerId(), gpus);
        if (availableGpus == -1) {
            String cmd = taskParam.getCmd();
            String[] extraPyPkg = taskParam.getExtraPythonPackage().split(" ");
            int environmentId = taskParam.getEnvironmentId();
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
            task.setStatus("pending");
            taskMapper.insert(task);
            return new TaskSubmitResult(-1);
        }
        return new TaskSubmitResult(availableGpus);
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
        task.setImageId(task.getImageId());
        task.setServerId(taskParam.getServerId());
        task.setUserId(userService.getCurrentUser().getId());
        task.setPodName(podName);
        task.setStatus(getPodStatus(podName));
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
        taskDTO.setEnvironment(image.getAlias());
        return taskDTO;
    }

    private String getPodStatus(String podName) {
        V1Pod v1Pod;
        try {
            v1Pod = k8sApi.readNamespacedPodStatus(podName, "default", "true");
        } catch (ApiException e) {
            throw new ServiceException("无法获取pod信息", e);
        }
        return v1Pod.getStatus().getPhase();
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
    public void cancelTask(int id) {
        Task task = taskMapper.getById(id);
        task.setStatus("delete");
        taskMapper.updateStatus(task);
        if (taskMapper.casUpdateRemoved(id) != 0) {
            String podName = task.getPodName();
            try {
                k8sApi.deleteNamespacedPod(podName, "default", "true", null, null,
                    null, null, null);
            } catch (ApiException e) {
                throw new ServiceException("k8s删除pod异常", e);
            }
            serverStatusService.returnGpus(task.getServerId(), task.getGpus());
        }

    }

    @Override
    public String getLog(int id) {
        Task task = taskMapper.getById(id);
        String status = task.getStatus();
        if ("delete".equals(status)) {
            throw new BadRequestException("不能获取删除了的训练任务的日志");
        } else if ("succeeded".equals(status) || "failed".equals(status)) {
            try {
                return FileUtils.readFile(Paths.get(getTaskLogPath(id)));
            } catch (IOException e) {
                throw new ServiceException("读取训练任务日志文件异常，任务id" + id, e);
            }
        } else {
            try {
                return getLogFromPod(task.getPodName());
            } catch (ServiceException e) {
                if (e.getCause().getMessage().endsWith("不存在")) {
                    task = taskMapper.getById(id);
                    while ("running".equals(task.getStatus())) {
                        Thread.yield();
                        task = taskMapper.getById(id);
                    }
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
        return FileUtils.joinPaths(gcmpProperties.getTaskLogRoot(), user.getUserName(), String.valueOf(taskId));

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
            throw new ServiceException("从pod获取日志异常，podName" + podName, e);
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
                "  restartPolicy: OnFailure\n" +
                "  containers:\n" +
                "    - name: gcmp-container\n" +
                "      image: %s\n" +
                "      command: [ \"%s\" ]\n" +
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
                "  nodeSelector: %s",
            podName, image.getTag(), withPyPkgCmd, mountPath, gpus, mountPath, nodeName);
        return podFile;
    }

    private class TaskCleaner implements Runnable {

        @Override
        public void run() {
            List<Task> tasks = taskMapper.listAll();
            for (Task task : tasks) {
                String status = task.getStatus();
                int taskId = task.getId();

                if ("delete".equals(status)) {
                    if (task.getRemoved() == 1) {
                        taskMapper.removeById(taskId);
                    }
                    try {
                        FileUtils.deleteFile(Paths.get(getTaskLogPath(taskId)));
                    } catch (IOException e) {
                        throw new ServiceException("删除训练任务日志异常，任务id" + taskId, e);
                    }
                } else if ("running".equals(status)) {
                    String latestStatus = getPodStatus(task.getPodName());
                    latestStatus = latestStatus.toLowerCase();
                    if ("succeeded".equals(latestStatus) || "failed".equals(latestStatus)) {
                        if (taskMapper.casUpdateRemoved(taskId) != 0) {
                            serverStatusService.returnGpus(task.getServerId(), task.getGpus());
                            String podName = task.getPodName();
                            try {
                                FileUtils.writeFile(Paths.get(getTaskLogPath(taskId)), getLogFromPod(podName));
                            } catch (IOException e) {
                                throw new ServiceException("保存训练任务日志异常，任务id" + taskId, e);
                            }
                            task.setStatus(latestStatus);
                            taskMapper.casUpdateStatus(task);
                            try {
                                k8sApi.deleteNamespacedPod(podName, "default", "true", null, null,
                                    null, null, null);
                            } catch (ApiException e) {
                                throw new ServiceException("k8s删除pod异常", e);
                            }
                        }
                    }

                }
            }
        }
    }

}
