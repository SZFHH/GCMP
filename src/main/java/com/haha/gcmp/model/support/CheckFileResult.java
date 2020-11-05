package com.haha.gcmp.model.support;

import java.util.List;

/**
 * Check file result
 *
 * @author SZFHH
 * @date 2020/10/28
 */
public class CheckFileResult {
    public static int EXISTED = 0;
    public static int NON_EXISTED = 1;

    /**
     * 已经存在的分片
     */
    private List<String> existedChunk;

    /**
     * 文件是否存在
     */
    private int status;

    public CheckFileResult() {
    }

    public CheckFileResult(List<String> existedChunk, int status) {
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
