package com.haha.gcmp.controller;

import com.haha.gcmp.model.dto.TaskDTO;
import com.haha.gcmp.model.params.TaskParam;
import com.haha.gcmp.model.support.BaseResponse;
import com.haha.gcmp.service.TaskService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Task controller
 *
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

    @PostMapping("")
    public int submitTask(@RequestBody TaskParam taskParam) {
        return taskService.submitTask(taskParam);
    }

    @GetMapping("user")
    List<TaskDTO> listTasksPerUser() {
        return taskService.listTasksPerUser();
    }

    @GetMapping("all")
    List<TaskDTO> listAllTasks() {
        return taskService.listAllTasks();
    }

    @DeleteMapping("/{taskId:\\d+}")
    void cancelTask(@PathVariable("taskId") int taskId) {
        taskService.cancelTask(taskId);
    }

    @GetMapping("/log/{taskId:\\d+}")
    BaseResponse<String> getLog(@PathVariable("taskId") int taskId) {
        return BaseResponse.ok("", taskService.getLog(taskId));
    }

}
