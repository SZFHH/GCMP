package com.haha.gcmp.service;

import com.haha.gcmp.model.dto.TaskDTO;
import com.haha.gcmp.model.params.TaskParam;
import com.haha.gcmp.model.support.TaskSubmitResult;

import java.util.List;

/**
 * @author SZFHH
 * @date 2020/11/1
 */
public interface TaskService {

    TaskSubmitResult submitTask(TaskParam taskParam);

    List<TaskDTO> listTasksPerUser();

    List<TaskDTO> listAllTasks();

    void cancelTask(int id);

    String getLog(int id);

    String getTaskLogPath(int taskId);


}
