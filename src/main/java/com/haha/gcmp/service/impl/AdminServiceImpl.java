package com.haha.gcmp.service.impl;

import com.haha.gcmp.config.propertites.GcmpProperties;
import com.haha.gcmp.model.entity.User;
import com.haha.gcmp.model.params.RegisterParam;
import com.haha.gcmp.service.AdminService;
import com.haha.gcmp.service.UserService;
import org.springframework.stereotype.Service;

/**
 * Admin service implementation.
 *
 * @author SZFHH
 * @date 2020/10/23
 */
@Service
public class AdminServiceImpl implements AdminService {
    private final UserService userService;
    private final GcmpProperties gcmpProperties;
    private int adminId;

    public AdminServiceImpl(UserService userService, GcmpProperties gcmpProperties) {
        this.userService = userService;
        this.gcmpProperties = gcmpProperties;
        this.adminId = -1;
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

    @Override
    public int getAdminId() {
        if (adminId != -1) {
            return adminId;
        }
        User user = userService.getByNameOfNonNull(gcmpProperties.getAdminName());
        adminId = user.getId();
        return adminId;
    }
}
