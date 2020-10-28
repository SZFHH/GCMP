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
        User user = userMapper.findById(id);
        if (user == null) {
            throw new NotFoundException("The id does not exist").setErrorData(id);
        }
        return user;
    }

    @Override
    public User getByIdOfNullable(int id) {
        return userMapper.findById(id);
    }

    @Override
    public User getByNameOfNonNull(String userName) {
        User user = userMapper.findByUserName(userName);
        if (user == null) {
            throw new NotFoundException("The user name does not exist").setErrorData(userName);
        }
        return user;
    }

    @Override
    public User getByNameOfNullable(String userName) {
        return userMapper.findByUserName(userName);
    }

    @Override
    public int createUser(RegisterParam registerParam) {
        User user = registerParam.toEntity();
        setPassword(user, registerParam.getPassword());
        return userMapper.insert(user);
    }

    @Override
    public User getCurrentUser() {
        return userMapper.findByUserName("szf");
//        return SecurityUtils.getCurrentUser();
    }

    @Override
    public List<User> getAllUser() {
        return userMapper.findAll();
    }

    private void setPassword(User user, String plainPassword) {
        user.setPassword(BCrypt.hashpw(plainPassword, BCrypt.gensalt()));
    }
}
