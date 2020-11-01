package com.haha.gcmp.repository;

import com.haha.gcmp.model.entity.Task;

import java.util.List;

/**
 * @author SZFHH
 * @date 2020/11/1
 */
public interface TaskMapper {

    List<Task> listAll();

    List<Task> listByUserId(int id);

    int insert(Task task);

    int removeById(int id);

    void updateStatus(Task task);

    Task getById(int id);
}
