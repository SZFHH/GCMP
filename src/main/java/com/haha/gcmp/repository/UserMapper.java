package com.haha.gcmp.repository;

import com.haha.gcmp.model.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author SZFHH
 * @date 2020/10/23
 */
@Mapper
public interface UserMapper {
    /**
     * 根据id获取user
     *
     * @param id id
     * @return user
     */
    User getById(int id);

    /**
     * 根据name获取user
     *
     * @param userName user name
     * @return user
     */
    User getByUserName(String userName);

    /**
     * 添加user
     *
     * @param user must not be null
     * @return changed lines
     */
    int insert(User user);

    /**
     * 获取所有user
     *
     * @return list of users.
     */
    List<User> listAll();
}