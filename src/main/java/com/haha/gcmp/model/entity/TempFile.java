package com.haha.gcmp.model.entity;

/**
 * @author SZFHH
 * @date 2020/10/26
 */
public class TempFile {
    private String md5;
    private int serverId;
    private int userId;

    /**
     * 文件目标存储位置的相对路径
     */
    private String relativePath;

    public TempFile(String md5, int serverId, int userId, String relativePath) {
        this.md5 = md5;
        this.serverId = serverId;
        this.userId = userId;
        this.relativePath = relativePath;
    }

    public TempFile() {
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

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
