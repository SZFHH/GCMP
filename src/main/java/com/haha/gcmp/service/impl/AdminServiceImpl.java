package com.haha.gcmp.service.impl;

import com.haha.gcmp.config.propertites.GcmpProperties;
import com.haha.gcmp.model.entity.User;
import com.haha.gcmp.model.params.UserParam;
import com.haha.gcmp.service.AdminService;
import com.haha.gcmp.service.DataService;
import com.haha.gcmp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Admin service implementation.
 *
 * @author SZFHH
 * @date 2020/10/23
 */
@Service
public class AdminServiceImpl implements AdminService {
    private UserService userService;
    private DataService dataService;
    private final GcmpProperties gcmpProperties;
    private int adminId;


    public AdminServiceImpl(GcmpProperties gcmpProperties) {
        this.gcmpProperties = gcmpProperties;
        this.adminId = -1;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setDataService(DataService dataService) {
        this.dataService = dataService;
    }

    @Override
    public boolean isInitialized() {
        User user = userService.getByNameOfNullable(gcmpProperties.getAdminName());
        return user != null;
    }

    @Override
    public void initialize(UserParam registerParam) {
        registerParam.setUsername(gcmpProperties.getAdminName());
        userService.createUser(registerParam);
        int size = gcmpProperties.getServerProperties().size();
        String commonDataRoot = gcmpProperties.getCommonDataRoot();
        for (int i = 0; i < size; ++i) {
            dataService.newAbsoluteDir(commonDataRoot, i);
        }
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
