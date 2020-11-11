package com.haha.gcmp.model.params;

import com.haha.gcmp.model.entity.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import static com.haha.gcmp.model.support.GcmpConst.DEFAULT_DOCKER_QUOTA;

/**
 * Login param
 *
 * @author SZFHH
 * @date 2020/10/23
 */
public class LoginParam implements BaseParam<User> {
    @NotBlank(message = "用户名或邮箱不能为空")
    @Size(max = 255, message = "用户名或邮箱的字符长度不能超过 {max}")
    private String username;

    @NotBlank(message = "登录密码不能为空")
    @Size(max = 100, message = "用户密码字符长度不能超过 {max}")
    private String password;

    private boolean rememberMe;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    @Override
    public User toEntity() {
        return new User(username, password, DEFAULT_DOCKER_QUOTA);
    }
}

