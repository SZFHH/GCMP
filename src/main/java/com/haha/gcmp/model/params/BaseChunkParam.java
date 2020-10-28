package com.haha.gcmp.model.params;

/**
 * @author SZFHH
 * @date 2020/10/28
 */
public class BaseChunkParam {
    private String hostName;

    private String md5;

    private String relativePath;

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
