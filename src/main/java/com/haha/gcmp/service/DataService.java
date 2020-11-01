package com.haha.gcmp.service;

import com.haha.gcmp.model.entity.DataFile;
import com.haha.gcmp.model.entity.TempFileInfo;
import com.haha.gcmp.model.params.*;
import com.haha.gcmp.model.support.CheckFileResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author SZFHH
 * @date 2020/10/25
 */
public interface DataService {

    List<DataFile> listDataDir(DataParam dataParam);

    void unzip(DataParam dataParam);

    void remove(DataParam dataParam);

    String getUserDataPath(String relativePath);

    String getUserTempPath(String identifier);

    CheckFileResult checkChunk(CheckFileQuery checkFileQuery);

    void uploadChunk(UploadChunkParam param, MultipartFile file);

    void mergeChunk(MergeChunkParam mergeChunkParam);

    void cancelUpload(TempFileInfo tempFileInfo);

    void move(DataMoveParam dataMoveParam);
}
