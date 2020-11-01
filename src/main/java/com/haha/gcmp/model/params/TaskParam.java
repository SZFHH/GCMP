package com.haha.gcmp.model.params;

/**
 * @author SZFHH
 * @date 2020/11/1
 */
public class TaskParam {
    int serverId;
    int environmentId;
    String cmd;
    String extraPythonPackage;
    int gpus;
    int pyVersion;

    public int getEnvironmentId() {
        return environmentId;
    }

    public int getPyVersion() {
        return pyVersion;
    }

    public void setPyVersion(int pyVersion) {
        this.pyVersion = pyVersion;
    }

    public void setEnvironmentId(int environmentId) {
        this.environmentId = environmentId;
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
