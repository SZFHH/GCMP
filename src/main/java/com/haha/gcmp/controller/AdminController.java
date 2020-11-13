package com.haha.gcmp.controller;

import com.haha.gcmp.config.propertites.GcmpProperties;
import com.haha.gcmp.exception.BadRequestException;
import com.haha.gcmp.model.dto.UserDTO;
import com.haha.gcmp.model.entity.User;
import com.haha.gcmp.model.params.LoginParam;
import com.haha.gcmp.model.params.UserParam;
import com.haha.gcmp.model.support.BaseResponse;
import com.haha.gcmp.security.token.AuthToken;
import com.haha.gcmp.service.AdminService;
import com.haha.gcmp.service.AuthService;
import com.haha.gcmp.service.UserService;
import org.springframework.web.bind.annotation.*;

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
    private final UserService userService;

    public AdminController(AuthService authService, AdminService adminService, GcmpProperties gcmpProperties, UserService userService) {
        this.authService = authService;
        this.adminService = adminService;
        this.gcmpProperties = gcmpProperties;
        this.userService = userService;
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
    public void init(@RequestBody UserParam registerParam) {
        if (adminService.isInitialized()) {
            throw new BadRequestException("管理员已经初始化！");
        }
        adminService.initialize(registerParam);
    }

    @GetMapping("is_initialized")
    public boolean isInitialized() {
        return adminService.isInitialized();
    }

    @PostMapping("logout")
    public void logout(HttpServletResponse response) {
        authService.logout(response);
    }

    @GetMapping("cur_user")
    public UserDTO currentUser() {
        User user = userService.getCurrentUser();
        return new UserDTO(user.getUsername(), authService.getToken(user));
    }

    @GetMapping("name")
    public BaseResponse<String> getAdminName() {
        return BaseResponse.ok("", gcmpProperties.getAdminName());
    }
}
