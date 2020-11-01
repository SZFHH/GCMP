package com.haha.gcmp.repository;

import com.haha.gcmp.model.entity.TempFileInfo;

/**
 * @author SZFHH
 * @date 2020/10/26
 */
public interface FileMapper {

    TempFileInfo get(TempFileInfo tempFileInfo);

    int remove(TempFileInfo tempFileInfo);

    int insert(TempFileInfo tempFileInfo);
}
