package com.haha.gcmp.model.support;

/**
 * @author SZFHH
 * @date 2020/10/31
 */
public class ServerProperty {
    private String username;
    private String hostIp;
    private String password;

    public ServerProperty(String hostName, String hostIp, String username, String password) {
        this.username = username;
        this.hostIp = hostIp;
        this.password = password;
        this.hostName = hostName;
    }

    private String hostName;

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
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

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

}
