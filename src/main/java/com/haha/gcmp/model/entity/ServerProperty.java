package com.haha.gcmp.model.entity;

/**
 * @author SZFHH
 * @date 2020/10/31
 */
public class ServerProperty {
    private String hostName;
    private String hostIp;
    private String username;
    private String password;
    private int gpus;
    private String gpuSeries;

    public ServerProperty(String hostName, String hostIp, String username, String password, int gpus, String gpuSeries) {
        this.hostName = hostName;
        this.hostIp = hostIp;
        this.username = username;
        this.password = password;
        this.gpus = gpus;
        this.gpuSeries = gpuSeries;
    }

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

    public ServerProperty() {
    }

    public int getGpus() {
        return gpus;
    }

    public void setGpus(int gpus) {
        this.gpus = gpus;
    }

    public String getGpuSeries() {
        return gpuSeries;
    }

    public void setGpuSeries(String gpuSeries) {
        this.gpuSeries = gpuSeries;
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
