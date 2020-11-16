package com.haha.gcmp.service;

import com.haha.gcmp.model.dto.TaskDTO;
import com.haha.gcmp.model.entity.Task;
import com.haha.gcmp.model.params.TaskParam;

import java.util.List;

/**
 * @author SZFHH
 * @date 2020/11/1
 */
public interface TaskService {

    /**
     * 提交任务
     *
     * @param taskParam
     * @return -1 if success, available gpus if gpu not sufficient
     */
    int submitTask(TaskParam taskParam);

    /**
     * 获取一个用户的所有task
     *
     * @return list of taskDTO
     */
    List<TaskDTO> listTasksPerUser();

    /**
     * 获取所有的任务，需要管理员权限
     *
     * @return list of taskDTO
     */
    List<TaskDTO> listAllTasks();

    /**
     * 中断（取消）任务
     *
     * @param taskId task id
     */
    void cancelTask(int taskId);

    /**
     * 获取任务的输出日志
     *
     * @param taskId task id
     * @return log of task
     */
    String getLog(int taskId);

    /**
     * 获取任务日志持久化路径
     *
     * @param task must not be null
     * @return path of log file after task is completed
     */
    String getTaskLogPath(Task task);

    /**
     * 获取用户的所有task
     *
     * @param userId user id
     * @return list of task
     */
    List<Task> listByUserId(int userId);


}
