package com.haha.gcmp.service;

import com.haha.gcmp.model.entity.Data;
import com.haha.gcmp.model.entity.TempFile;
import com.haha.gcmp.model.params.*;
import com.haha.gcmp.model.support.CheckFileResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author SZFHH
 * @date 2020/10/25
 */
public interface DataService {

    /**
     * 获取一个目录下所有的文件（夹）
     *
     * @param dataParam must not be null
     * @return list of data
     */
    List<Data> listDataDir(DataParam dataParam);

    /**
     * 解压
     *
     * @param dataParam must not be null
     */
    void unzip(DataParam dataParam);

    /**
     * 删除文件（夹）
     *
     * @param dataParam must not be null
     */
    void remove(DataParam dataParam);

    /**
     * 获取用户数据的绝对路径
     *
     * @param relativePath relative to a user's data root
     * @return absolute path of user data
     */
    String getUserDataPath(String relativePath);

    /**
     * 获取用户临时文件的绝对路径
     *
     * @param identifier md5 of file to upload, must not be null
     * @return absolute path of temp file
     */
    String getUserTempPath(String identifier);

    /**
     * 检查待上传文件还需要上传哪些分片
     *
     * @param checkFileQuery must not be null
     * @return check file result
     */
    CheckFileResult checkChunk(CheckFileQuery checkFileQuery);

    /**
     * 上传分片
     *
     * @param param must not be null
     * @param file  must not be null
     */
    void uploadChunk(UploadChunkParam param, MultipartFile file);

    /**
     * 合并分片
     *
     * @param mergeChunkParam must not be null
     */
    void mergeChunk(MergeChunkParam mergeChunkParam);

    /**
     * 取消上传
     *
     * @param tempFile must not be null
     */
    void cancelUpload(TempFile tempFile);

    /**
     * 移动数据
     *
     * @param dataMoveParam must not be null
     */
    void move(DataMoveParam dataMoveParam);

    /**
     * 拷贝数据
     *
     * @param dataMoveParam must not be null
     */
    void copy(DataMoveParam dataMoveParam);

    /**
     * 新建文件夹
     *
     * @param dataParam must not be null
     */
    void newDir(DataParam dataParam);

    /**
     * 下载文件（非分片传输，有大小限制）
     *
     * @param dataParam must not be null
     */
    byte[] getFile(DataParam dataParam);
}
