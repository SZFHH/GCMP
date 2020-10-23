package com.haha.gcmp.service.impl;

import com.haha.gcmp.config.GcmpProperties;
import com.haha.gcmp.model.entity.User;
import com.haha.gcmp.model.params.RegisterParam;
import com.haha.gcmp.service.AdminService;
import com.haha.gcmp.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author SZFHH
 * @date 2020/10/23
 */
@Service
public class AdminServiceImpl implements AdminService {
    private final UserService userService;
    private final GcmpProperties gcmpProperties;

    public AdminServiceImpl(UserService userService, GcmpProperties gcmpProperties) {
        this.userService = userService;
        this.gcmpProperties = gcmpProperties;
    }

    @Override
    public boolean isInitialized() {
        User user = userService.getByNameOfNullable(gcmpProperties.getAdminName());
        return user != null;
    }

    @Override
    public void initialize(RegisterParam registerParam) {
        registerParam.setUserName(gcmpProperties.getAdminName());
        userService.createUser(registerParam);
    }
}
