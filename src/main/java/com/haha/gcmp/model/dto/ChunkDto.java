package com.haha.gcmp.model.dto;

import com.haha.gcmp.model.entity.Fileinfo;

import java.io.Serializable;

/**
 * @Description: 分片上传响应
 * @date 2020/6/10 20:59
 */

public class ChunkDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean merge;

    private Fileinfo fileinfo;

    public ChunkDto(Boolean merge) {
        this.merge = merge;
    }
}
