package com.haha.gcmp.controller;

import com.haha.gcmp.model.params.LoginParam;
import com.haha.gcmp.model.params.RegisterParam;
import com.haha.gcmp.model.support.BaseResponse;
import com.haha.gcmp.security.token.AuthToken;
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
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final AuthService authService;

    public UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("login")
    public AuthToken auth(@RequestBody @Valid LoginParam loginParam) {
        return authService.authenticate(loginParam);
    }

    @PostMapping("register")
    public BaseResponse<String> register(@RequestBody @Valid RegisterParam registerParam) {
        userService.createUser(registerParam);
        return BaseResponse.ok("注册成功！");
    }
}
