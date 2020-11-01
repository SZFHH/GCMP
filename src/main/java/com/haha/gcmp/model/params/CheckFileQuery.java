package com.haha.gcmp.model.params;

/**
 * @author SZFHH
 * @date 2020/10/28
 */
public class CheckFileQuery extends BaseChunkParam {

    private long chunkSize;

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
