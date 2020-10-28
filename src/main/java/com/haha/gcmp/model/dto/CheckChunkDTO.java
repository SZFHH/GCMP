package com.haha.gcmp.model.dto;

import java.util.List;

/**
 * @author SZFHH
 * @date 2020/10/28
 */
public class CheckChunkDTO {
    public static int EXISTED = 0;
    public static int NON_EXISTED = 1;
    private List<String> existedChunk;
    private int status;

    public CheckChunkDTO() {
    }

    public CheckChunkDTO(List<String> existedChunk, int status) {
        this.existedChunk = existedChunk;
        this.status = status;
    }

    public List<String> getExistedChunk() {
        return existedChunk;
    }

    public void setExistedChunk(List<String> existedChunk) {
        this.existedChunk = existedChunk;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
