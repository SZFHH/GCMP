package com.haha.gcmp.repository;

import com.haha.gcmp.model.entity.Task;

import java.util.List;

/**
 * @author SZFHH
 * @date 2020/11/1
 */
public interface TaskMapper {
    /**
     * 获取所有task
     *
     * @return list of tasks
     */
    List<Task> listAll();

    /**
     * 根据user id获取用户的所有task
     *
     * @param id user id
     * @return list of tasks
     */
    List<Task> listByUserId(int id);

    /**
     * 添加task
     *
     * @param task must not be null
     * @return changed lines
     */
    int insert(Task task);

    /**
     * 根据task id 删除task
     *
     * @param id id
     * @return changed lines
     */
    int removeById(int id);

    /**
     * 更新task的status
     *
     * @param task must not be null.
     */
    void updateStatus(Task task);


    /**
     * 根据task id 获取task
     *
     * @param id id
     * @return task
     */
    Task getById(int id);

    /**
     * 如果removed是false的话，设置removed为true，不然什么都不做
     *
     * @param id id
     * @return changed lines
     */
    int casUpdateRemoved(int id);

    /**
     * 如果 status==running || status==pending, 设置置status为task中的status，不然什么都不做
     *
     * @param task must not be null
     * @return change lines
     */
    int casUpdateStatus(Task task);
}
