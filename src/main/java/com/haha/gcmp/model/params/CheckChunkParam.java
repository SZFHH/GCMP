package com.haha.gcmp.model.params;

/**
 * @author SZFHH
 * @date 2020/10/28
 */
public class CheckChunkParam extends BaseChunkParam {
    private int totalChunks;

    private long chunkSize;

    private long totalSize;

    public int getTotalChunks() {
        return totalChunks;
    }

    public void setTotalChunks(int totalChunks) {
        this.totalChunks = totalChunks;
    }

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
