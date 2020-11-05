package com.haha.gcmp.model.params;

/**
 * 查询待上传的文件是否存在，需要上传的分片
 *
 * @author SZFHH
 * @date 2020/10/28
 */
public class CheckFileQuery extends BaseChunkParam {
    /**
     * 每个分片的大小（字节）
     */
    private long chunkSize;

    /**
     * 文件的总大小（字节）
     */
    private long totalSize;

    public long getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(long chunkSize) {
        this.chunkSize = chunkSize;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }


}
