package com.haha.gcmp.repository;

import com.haha.gcmp.model.support.TempFileInfo;

/**
 * @author SZFHH
 * @date 2020/10/26
 */
public interface FileMapper {

    TempFileInfo find(TempFileInfo tempFileInfo);

    int remove(TempFileInfo tempFileInfo);

    int insert(TempFileInfo tempFileInfo);
}
