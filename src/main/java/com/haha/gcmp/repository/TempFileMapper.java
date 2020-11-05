package com.haha.gcmp.repository;

import com.haha.gcmp.model.entity.TempFile;

/**
 * @author SZFHH
 * @date 2020/10/26
 */
public interface TempFileMapper {
    /**
     * 检查临时文件是否存在
     *
     * @param tempFile must not be null.
     * @return true if temp file exists, false otherwise.
     */
    boolean exists(TempFile tempFile);

    /**
     * 删除临时文件信息
     *
     * @param tempFile must not be null
     * @return changed lines
     */
    int remove(TempFile tempFile);

    /**
     * 添加临时文件信息
     *
     * @param tempFile must not be null
     * @return changed lines
     */
    int insert(TempFile tempFile);
}
