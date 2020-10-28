package com.haha.gcmp.model.params;

/**
 * @author SZFHH
 * @date 2020/10/27
 */
public class DataMoveParam {
    String srcRelativePath;
    String targetRelativePath;
    String hostName;

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getTargetRelativePath() {
        return targetRelativePath;
    }

    public void setTargetRelativePath(String targetRelativePath) {
        this.targetRelativePath = targetRelativePath;
    }

    public String getSrcRelativePath() {
        return srcRelativePath;
    }

    public void setSrcRelativePath(String srcRelativePath) {
        this.srcRelativePath = srcRelativePath;
    }
}
