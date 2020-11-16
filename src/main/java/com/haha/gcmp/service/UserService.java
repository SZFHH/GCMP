package com.haha.gcmp.service;

import com.haha.gcmp.model.entity.User;
import com.haha.gcmp.model.params.UserParam;

import java.util.List;

/**
 * @author SZFHH
 * @date 2020/10/23
 */
public interface UserService {
    /**
     * 根据id获取user，结果不能为null
     *
     * @param id user id
     * @return user
     */
    User getByIdOfNonNull(int id);

    /**
     * 根据id获取user，结果可以为null
     *
     * @param id user id
     * @return user
     */
    User getByIdOfNullable(int id);

    /**
     * 根据name获取user，结果不能为null
     *
     * @param name user name
     * @return user
     */
    User getByNameOfNonNull(String name);

    /**
     * 根据name获取user，结果可以为null
     *
     * @param name user name
     * @return user
     */
    User getByNameOfNullable(String name);

    /**
     * 创建新用户
     *
     * @param registerParam register param
     */
    void createUser(UserParam registerParam);

    /**
     * 获取当前用户
     *
     * @return user
     */
    User getCurrentUser();

    /**
     * 获取所有的用户
     *
     * @return list of users
     */
    List<User> getAllUser();

    /**
     * 更新用户密码
     *
     * @param user must not be null
     * @return changed lines
     */
    int updatePassword(User user);

    /**
     * 更新用户docker镜像上限
     *
     * @param user must not be null
     * @return changed lines
     */
    int updateDockerQuota(User user);

    /**
     * 删除用户，删除数据库记录及所有服务器上的文件，docker镜像，任务
     *
     * @param userId user id
     */
    void removeUser(int userId);
}
