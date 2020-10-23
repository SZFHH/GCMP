package com.haha.gcmp.controller;

import com.haha.gcmp.exception.BadRequestException;
import com.haha.gcmp.model.params.LoginParam;
import com.haha.gcmp.model.params.RegisterParam;
import com.haha.gcmp.model.support.BaseResponse;
import com.haha.gcmp.security.token.AuthToken;
import com.haha.gcmp.service.AdminService;
import com.haha.gcmp.service.AuthService;
import com.haha.gcmp.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author SZFHH
 * @date 2020/10/22
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AuthService authService;
    private final UserService userService;
    private final AdminService adminService;

    public AdminController(AuthService authService, UserService userService, AdminService adminService) {
        this.authService = authService;
        this.userService = userService;
        this.adminService = adminService;
    }

    @PostMapping("login")
    public AuthToken auth(@RequestBody @Valid LoginParam loginParam) {
        return authService.authenticate(loginParam);
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
}
