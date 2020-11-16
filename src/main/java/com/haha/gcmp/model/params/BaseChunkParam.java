package com.haha.gcmp.model.params;

/**
 * Base chunk param
 *
 * @author SZFHH
 * @date 2020/10/28
 */
public abstract class BaseChunkParam {

    /**
     * 服务器编号
     */
    private int serverId;

    /**
     * 文件md5
     */
    private String md5;

    /**
     * 待上传文件目标位置的相对路径（相对用户所属的根目录）
     */
    private String relativePath;

    /**
     * 文件总分片数
     */
    private int totalChunks;

    public int getTotalChunks() {
        return totalChunks;
    }

    public void setTotalChunks(int totalChunks) {
        this.totalChunks = totalChunks;
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
}
