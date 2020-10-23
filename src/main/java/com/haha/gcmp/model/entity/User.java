package com.haha.gcmp.model.entity;

import static com.haha.gcmp.config.GcmpConst.DEFAULT_DOCKER_QUOTA;

/**
 * @author SZFHH
 * @date 2020/10/23
 */
public class User {
    private int id;
    private String userName;
    private String password;
    private int dockerQuota;

    public User() {
    }

    public User(String userName, String password, int dockerQuota) {
        this.userName = userName;
        this.password = password;
        this.dockerQuota = dockerQuota;
    }

    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
        this.dockerQuota = DEFAULT_DOCKER_QUOTA;
    }

    public int getDockerQuota() {
        return dockerQuota;
    }

    public void setDockerQuota(int dockerQuota) {
        this.dockerQuota = dockerQuota;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
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
}
