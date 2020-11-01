package com.haha.gcmp.model.params;

/**
 * @author SZFHH
 * @date 2020/10/26
 */
public class DataParam {
    private String relativePath;
    private boolean file;
    private int serverId;

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    public boolean isFile() {
        return file;
    }

    public void setFile(boolean file) {
        this.file = file;
    }


}
