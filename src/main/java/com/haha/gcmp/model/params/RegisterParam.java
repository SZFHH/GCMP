package com.haha.gcmp.model.params;

import com.haha.gcmp.model.entity.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import static com.haha.gcmp.model.support.GcmpConst.DEFAULT_DOCKER_QUOTA;

/**
 * Register param
 *
 * @author SZFHH
 * @date 2020/10/23
 */
public class RegisterParam implements BaseParam<User> {
    @NotBlank(message = "用户名或邮箱不能为空")
    @Size(max = 255, message = "用户名或邮箱的字符长度不能超过 {max}")
    private String userName;

    @NotBlank(message = "登录密码不能为空")
    @Size(max = 100, message = "用户密码字符长度不能超过 {max}")
    private String password;

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public User toEntity() {
        return new User(userName, password, DEFAULT_DOCKER_QUOTA);
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
