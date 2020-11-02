package com.haha.gcmp.controller;

import com.haha.gcmp.model.dto.TaskDTO;
import com.haha.gcmp.model.params.TaskParam;
import com.haha.gcmp.model.support.TaskSubmitResult;
import com.haha.gcmp.service.TaskService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author SZFHH
 * @date 2020/10/22
 */
@RestController
@RequestMapping("/api/task")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @RequestMapping("/submit")
    public TaskSubmitResult submitTask(TaskParam taskParam) {
        return taskService.submitTask(taskParam);
    }

    @RequestMapping("/list/user")
    List<TaskDTO> listTasksPerUser() {
        return taskService.listTasksPerUser();
    }

    @RequestMapping("/list/admin")
    List<TaskDTO> listAllTasks() {
        return taskService.listAllTasks();
    }

    @DeleteMapping("/{id:\\d+}")
    void cancelTask(@PathVariable("id") int id) {
        taskService.cancelTask(id);
    }

    @GetMapping("/log/{id:\\d+}")
    String getLog(@PathVariable("id") int id) {
        return taskService.getLog(id);
    }

}
