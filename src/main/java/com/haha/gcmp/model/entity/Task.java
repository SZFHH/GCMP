package com.haha.gcmp.model.entity;

import com.haha.gcmp.model.enums.TaskStatusType;

/**
 * @author SZFHH
 * @date 2020/11/1
 */
public class Task {
    private int id;
    private String cmd;
    private TaskStatusType status;
    private int serverId;
    private int gpus;
    private int imageId;
    private String podName;
    private int userId;
    private int removed;
    private long startTime;

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public int getRemoved() {
        return removed;
    }

    public void setRemoved(int removed) {
        this.removed = removed;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPodName() {
        return podName;
    }

    public void setPodName(String podName) {
        this.podName = podName;
    }

    public int getId() {
        return id;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
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

    public TaskStatusType getStatus() {
        return status;
    }

    public void setStatus(TaskStatusType status) {
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
}
