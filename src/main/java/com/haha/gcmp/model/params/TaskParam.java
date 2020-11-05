package com.haha.gcmp.model.params;

/**
 * Task param
 *
 * @author SZFHH
 * @date 2020/11/1
 */
public class TaskParam {

    int serverId;

    /**
     * 镜像id
     */
    int imageId;

    /**
     * 任务的命令
     */
    String cmd;

    /**
     * 额外的python包
     */
    String extraPythonPackage;

    /**
     * 需要的gpu数量
     */
    int gpus;

    /**
     * python的版本
     */
    int pyVersion;

    public int getImageId() {
        return imageId;
    }

    public int getPyVersion() {
        return pyVersion;
    }

    public void setPyVersion(int pyVersion) {
        this.pyVersion = pyVersion;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getExtraPythonPackage() {
        return extraPythonPackage;
    }

    public void setExtraPythonPackage(String extraPythonPackage) {
        this.extraPythonPackage = extraPythonPackage;
    }

    public int getGpus() {
        return gpus;
    }

    public void setGpus(int gpus) {
        this.gpus = gpus;
    }
}
