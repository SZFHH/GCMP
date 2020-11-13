package com.haha.gcmp.controller;

import com.haha.gcmp.model.dto.UserDTO;
import com.haha.gcmp.model.entity.User;
import com.haha.gcmp.model.params.LoginParam;
import com.haha.gcmp.model.params.UserParam;
import com.haha.gcmp.security.token.AuthToken;
import com.haha.gcmp.service.AuthService;
import com.haha.gcmp.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

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

    @PostMapping("")
    public void register(@RequestBody UserParam registerParam) {
        userService.createUser(registerParam);
    }

    @DeleteMapping("/{userId:\\d+}")
    public void removeUser(@PathVariable("userId") int userId) {
        userService.removeUser(userId);
    }

    @GetMapping("cur_user")
    public UserDTO currentUser() {
        User user = userService.getCurrentUser();
        return new UserDTO(user.getUsername(), authService.getToken(user));
    }

    @PostMapping("logout")
    public void logout(HttpServletResponse response) {
        authService.logout(response);
    }

    @GetMapping("all")
    public List<User> allUser() {
        return userService.getAllUser();
    }

    @PutMapping("/dockerQuota")
    public void updateDockerQuota(@RequestBody UserParam userParam) {
        userService.updateDockerQuota(userParam.toEntity());
    }

    @PutMapping("/password")
    public void updatePassword(@RequestBody UserParam userParam) {
        userService.updatePassword(userParam.toEntity());
    }

}
