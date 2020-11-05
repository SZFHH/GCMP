package com.haha.gcmp.model.params;

/**
 * Upload chunk param
 *
 * @author SZFHH
 * @date 2020/10/26
 */
public class UploadChunkParam extends BaseChunkParam {

    /**
     * 单个分片的次序编号
     */
    private int chunkNumber;

    public int getChunkNumber() {
        return chunkNumber;
    }

    public void setChunkNumber(int chunkNumber) {
        this.chunkNumber = chunkNumber;
    }
}
