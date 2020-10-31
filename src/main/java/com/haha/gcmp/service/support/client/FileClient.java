package com.haha.gcmp.service.support.client;

import com.haha.gcmp.model.entity.DataFile;

import java.util.List;

/**
 * @author SZFHH
 * @date 2020/10/31
 */
public interface FileClient {

    List<DataFile> listDir(String dirPath);

    void put(byte[] data, String remoteFilePath);

    void mergeFiles(List<String> fileList, String targetFilePath);

    void removeFile(String remotePath);

    void removeDir(String remotePath);

    void unzip(String remoteFilePath);

    void move(String srcPath, String targetPath);

    void createParentDirIfNecessary(String remotePath, String permission);

    void mkdirIfNotExist(String dirPath, String permission);

    void mkdirs(String dirPath, String permission);

    boolean checkDirectoryExists(String remotePath);

    boolean checkFileExists(String remotePath);

    DataFile getFileInfo(String remotePath);

    void removeDirIfExists(String remotePath);

    String execShellCmd(String cmd, String exceptionMsg);

    void chmod(String permission, String remotePath);
}
