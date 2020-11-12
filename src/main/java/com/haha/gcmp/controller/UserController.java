package com.haha.gcmp.controller;

import com.haha.gcmp.model.dto.UserDTO;
import com.haha.gcmp.model.entity.User;
import com.haha.gcmp.model.params.LoginParam;
import com.haha.gcmp.model.params.RegisterParam;
import com.haha.gcmp.model.support.BaseResponse;
import com.haha.gcmp.security.token.AuthToken;
import com.haha.gcmp.service.AuthService;
import com.haha.gcmp.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * User controller
 *
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
    public AuthToken auth(@RequestBody @Valid LoginParam loginParam, HttpServletResponse response) {
        AuthToken authToken = authService.authenticate(loginParam);
        if (loginParam.isRememberMe()) {
            authService.setRememberMe(response, loginParam);
        }
        return authToken;
    }

    @PostMapping("register")
    public BaseResponse<String> register(@RequestBody @Valid RegisterParam registerParam) {
        userService.createUser(registerParam);
        return BaseResponse.ok("注册成功！");
    }

    @GetMapping("cur_user")
    public UserDTO currentUser() {
        User user = userService.getCurrentUser();
        return new UserDTO(user.getUsername());
    }

    @PostMapping("logout")
    public void logout(HttpServletResponse response) {
        authService.logout(response);
    }
}
