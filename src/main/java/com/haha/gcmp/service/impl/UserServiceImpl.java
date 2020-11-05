package com.haha.gcmp.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.haha.gcmp.exception.NotFoundException;
import com.haha.gcmp.model.entity.User;
import com.haha.gcmp.model.params.RegisterParam;
import com.haha.gcmp.repository.UserMapper;
import com.haha.gcmp.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * User service implementation.
 *
 * @author SZFHH
 * @date 2020/10/23
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }


    @Override
    public User getByIdOfNonNull(int id) {
        User user = userMapper.getById(id);
        if (user == null) {
            throw new NotFoundException("The id does not exist").setErrorData(id);
        }
        return user;
    }

    @Override
    public User getByIdOfNullable(int id) {
        return userMapper.getById(id);
    }

    @Override
    public User getByNameOfNonNull(String userName) {
        User user = userMapper.getByUserName(userName);
        if (user == null) {
            throw new NotFoundException("The user name does not exist").setErrorData(userName);
        }
        return user;
    }

    @Override
    public User getByNameOfNullable(String userName) {
        return userMapper.getByUserName(userName);
    }

    @Override
    public void createUser(RegisterParam registerParam) {
        User user = registerParam.toEntity();
        setPassword(user, registerParam.getPassword());
        userMapper.insert(user);
    }

    @Override
    public User getCurrentUser() {
        return userMapper.getByUserName("szf");
//        return SecurityUtils.getCurrentUser();
    }

    @Override
    public List<User> getAllUser() {
        return userMapper.listAll();
    }

    private void setPassword(User user, String plainPassword) {
        user.setPassword(BCrypt.hashpw(plainPassword, BCrypt.gensalt()));
    }
}
