package com.haha.gcmp.service;

import com.haha.gcmp.model.entity.User;
import com.haha.gcmp.model.params.RegisterParam;

import java.util.List;

/**
 * @author SZFHH
 * @date 2020/10/23
 */
public interface UserService {
    User getByIdOfNonNull(int id);

    User getByIdOfNullable(int id);

    User getByNameOfNonNull(String name);


    User getByNameOfNullable(String name);

    int createUser(RegisterParam registerParam);

    User getCurrentUser();

    List<User> getAllUser();
}
