package com.haha.gcmp.repository;

import com.haha.gcmp.model.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author SZFHH
 * @date 2020/10/23
 */
@Mapper
public interface UserMapper {
    User findById(int id);

    User findByUserName(String userName);

    int insert(User user);
}
