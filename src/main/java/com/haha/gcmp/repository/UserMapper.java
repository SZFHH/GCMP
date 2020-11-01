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
    User getById(int id);

    User getByUserName(String userName);

    int insert(User user);

    List<User> listAll();
}
