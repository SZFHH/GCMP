package com.haha.gcmp.model.dto;

/**
 * Task output dto
 *
 * @author SZFHH
 * @date 2020/11/1
 */
public class TaskDTO {
    private int id;
    private String cmd;
    private String status;
    private int serverId;
    private int gpus;
    private String environment;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public int getGpus() {
        return gpus;
    }

    public void setGpus(int gpus) {
        this.gpus = gpus;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getGpuSeries() {
        return gpuSeries;
    }

    public void setGpuSeries(String gpuSeries) {
        this.gpuSeries = gpuSeries;
    }

    private String gpuSeries;

}
