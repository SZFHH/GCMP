package com.haha.gcmp.model.entity;

import java.util.Objects;

/**
 * @author SZFHH
 * @date 2020/10/23
 */
public class User {
    private int id;
    private String username;
    private String password;
    private int dockerQuota;

    public User() {
    }

    public User(int id, String username, String password, int dockerQuota) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.dockerQuota = dockerQuota;
    }

    public int getDockerQuota() {
        return dockerQuota;
    }

    public void setDockerQuota(int dockerQuota) {
        this.dockerQuota = dockerQuota;
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

    public void setPassWord(String password) {
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }
        User user = (User) o;
        return getId() == user.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
