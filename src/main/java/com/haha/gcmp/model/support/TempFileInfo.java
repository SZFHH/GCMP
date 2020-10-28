package com.haha.gcmp.model.support;

/**
 * @author SZFHH
 * @date 2020/10/26
 */
public class TempFileInfo {
    String md5;
    String hostName;
    String relativePath;

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    public TempFileInfo(String md5, String hostName, String relativePath) {
        this.md5 = md5;
        this.hostName = hostName;
        this.relativePath = relativePath;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }
}
