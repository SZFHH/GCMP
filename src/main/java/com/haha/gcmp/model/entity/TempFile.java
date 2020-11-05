package com.haha.gcmp.model.entity;

/**
 * @author SZFHH
 * @date 2020/10/26
 */
public class TempFile {
    String md5;
    int serverId;

    /**
     * 文件目标存储位置的相对路径
     */
    String relativePath;

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

    public TempFile(String md5, int serverId, String relativePath) {
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
