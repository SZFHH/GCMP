package com.haha.gcmp.model.params;

/**
 * Data param
 *
 * @author SZFHH
 * @date 2020/10/26
 */
public class DataParam {

    /**
     * 相对服务器中用户根文件的相对路径
     */
    private String relativePath;

    /**
     * 是否是文件
     */
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
