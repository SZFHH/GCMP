package com.haha.gcmp.controller;

import com.haha.gcmp.config.propertites.GcmpProperties;
import com.haha.gcmp.exception.BadRequestException;
import com.haha.gcmp.model.params.LoginParam;
import com.haha.gcmp.model.params.RegisterParam;
import com.haha.gcmp.model.support.BaseResponse;
import com.haha.gcmp.security.token.AuthToken;
import com.haha.gcmp.service.AdminService;
import com.haha.gcmp.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * Admin controller
 *
 * @author SZFHH
 * @date 2020/10/22
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AuthService authService;
    private final AdminService adminService;
    private final GcmpProperties gcmpProperties;

    public AdminController(AuthService authService, AdminService adminService, GcmpProperties gcmpProperties) {
        this.authService = authService;
        this.adminService = adminService;
        this.gcmpProperties = gcmpProperties;
    }

    @PostMapping("login")
    public AuthToken auth(@RequestBody @Valid LoginParam loginParam, HttpServletResponse response) {
        if (!loginParam.getUsername().equals(gcmpProperties.getAdminName())) {
            throw new BadRequestException("请用管理员账户登录！");
        }
        AuthToken authToken = authService.authenticate(loginParam);
        if (loginParam.isRememberMe()) {
            authService.setRememberMe(response, loginParam);
        }
        return authToken;
    }

    @PostMapping("init")
    public BaseResponse<String> init(@RequestBody @Valid RegisterParam registerParam) {
        if (adminService.isInitialized()) {
            throw new BadRequestException("管理员已经初始化！");
        }
        adminService.initialize(registerParam);
        return BaseResponse.ok("初始化成功！");
    }

    @PostMapping("is_initialized")
    public boolean isInitialized() {
        return adminService.isInitialized();
    }

    @PostMapping("logout")
    public void logout(HttpServletResponse response) {
        authService.logout(response);
    }
}
