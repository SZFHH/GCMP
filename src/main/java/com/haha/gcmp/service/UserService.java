package com.haha.gcmp.service;

import com.haha.gcmp.model.entity.User;
import com.haha.gcmp.model.params.RegisterParam;

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
    void createUser(RegisterParam registerParam);

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
}
