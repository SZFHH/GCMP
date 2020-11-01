package com.haha.gcmp.model.entity;

/**
 * @author SZFHH
 * @date 2020/10/26
 */
public class TempFileInfo {
    String md5;
    int serverId;
    String relativePath;

    public TempFileInfo() {
    }

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public TempFileInfo(String md5, int serverId, String relativePath) {
        this.md5 = md5;
        this.serverId = serverId;
        this.relativePath = relativePath;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }


}
