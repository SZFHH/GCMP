package com.haha.gcmp.service;

import com.haha.gcmp.model.entity.Data;
import com.haha.gcmp.model.entity.TempFile;
import com.haha.gcmp.model.params.*;
import com.haha.gcmp.model.support.CheckFileResult;
import org.springframework.core.io.Resource;
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
    void removeRelativePath(DataParam dataParam);

    /**
     * 删除文件（夹）
     *
     * @param absolutePath absolute path
     * @param serverId     server id
     */
    void removeAbsolutePath(String absolutePath, int serverId);

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
     * 以相对用户根目录，管理员根目录的路径新建文件夹
     *
     * @param dataParam must not be null
     */
    void newRelativeDir(DataParam dataParam);

    /**
     * 以绝对路径新建文件夹
     *
     * @param absolutePath absolute path
     * @param serverId     server id
     */
    void newAbsoluteDir(String absolutePath, int serverId);

    /**
     * 下载文件（非分片传输）
     *
     * @param dataParam must not be null
     * @return byte of file
     */
    byte[] getFile(DataParam dataParam);

    /**
     * 获得所有的公共数据集记录
     *
     * @return list of data
     */
    List<Data> listCommonDataset();

    /**
     * 把公共数据集信息同步到数据库
     */
    void syncCommonData();

    /**
     * 删除temp file表中用户的记录
     *
     * @param userId user id
     */
    void removeTempFileByUserId(int userId);

    /**
     * 以Resource形式返回文件，减少下载等待时间
     *
     * @param dataParam
     * @return
     */
    Resource loadFileAsResource(DataParam dataParam);
}
