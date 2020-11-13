package com.haha.gcmp.model.params;

import com.haha.gcmp.model.entity.User;

/**
 * @author SZFHH
 * @date 2020/11/13
 */
public class UserParam implements BaseParam<User> {
    int id;
    String username;
    String password;
    int dockerQuota;

    @Override
    public User toEntity() {
        return new User(id, username, password, dockerQuota);
    }

    public UserParam() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public int getDockerQuota() {
        return dockerQuota;
    }

    public void setDockerQuota(int dockerQuota) {
        this.dockerQuota = dockerQuota;
    }
}
