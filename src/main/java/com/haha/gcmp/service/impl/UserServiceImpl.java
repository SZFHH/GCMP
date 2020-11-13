package com.haha.gcmp.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.haha.gcmp.exception.BadRequestException;
import com.haha.gcmp.exception.NotFoundException;
import com.haha.gcmp.model.entity.User;
import com.haha.gcmp.model.params.UserParam;
import com.haha.gcmp.repository.UserMapper;
import com.haha.gcmp.security.util.SecurityUtils;
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
        User user = userMapper.getByUsername(userName);
        if (user == null) {
            throw new NotFoundException("没找到用户：" + userName);
        }
        return user;
    }

    @Override
    public User getByNameOfNullable(String userName) {
        return userMapper.getByUsername(userName);
    }

    @Override
    public void createUser(UserParam registerParam) {
        User user = registerParam.toEntity();
        if (userMapper.getByUsername(user.getUsername()) != null) {
            throw new BadRequestException("用户名已存在");
        }
        setPassword(user);
        userMapper.insert(user);
    }

    @Override
    public User getCurrentUser() {
        return SecurityUtils.getCurrentUser();
    }

    @Override
    public List<User> getAllUser() {
        return userMapper.listAll();
    }

    @Override
    public int updatePassword(User user) {
        setPassword(user);
        return userMapper.updatePassword(user);
    }

    @Override
    public int updateDockerQuota(User user) {
        return userMapper.updateDockerQuota(user);
    }

    @Override
    public void removeUser(int userId) {

    }


    private void setPassword(User user) {
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
    }
}
