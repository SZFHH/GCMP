package com.haha.gcmp.model.params;

/**
 * Data move param
 *
 * @author SZFHH
 * @date 2020/10/27
 */
public class DataMoveParam {

    String srcRelativePath;

    String targetRelativePath;

    int serverId;

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

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }
}
