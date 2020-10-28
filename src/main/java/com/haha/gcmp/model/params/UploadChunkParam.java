package com.haha.gcmp.model.params;

/**
 * @author SZFHH
 * @date 2020/10/26
 */
public class UploadChunkParam extends BaseChunkParam {

    private int chunkNumber;

    private int totalChunks;


    public int getChunkNumber() {
        return chunkNumber;
    }

    public void setChunkNumber(int chunkNumber) {
        this.chunkNumber = chunkNumber;
    }

    public int getTotalChunks() {
        return totalChunks;
    }

    public void setTotalChunks(int totalChunks) {
        this.totalChunks = totalChunks;
    }


}
