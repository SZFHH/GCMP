package com.haha.gcmp.client.fileclient;

import com.haha.gcmp.model.entity.Data;

import java.util.List;

/**
 * File Client Interface
 *
 * @author SZFHH
 * @date 2020/10/31
 */
public interface FileClient {
    /**
     * 获得一个服务器目录下的所有文件（夹）信息
     *
     * @param dirPath 服务器上文件夹绝对路径
     * @return 文件（夹）信息列表
     */
    List<Data> listDir(String dirPath);

    /**
     * 把文件保存到服务器
     *
     * @param data           文件byte数组
     * @param remoteFilePath 服务器上的绝对路径
     */
    void put(byte[] data, String remoteFilePath);

    /**
     * 在服务器上把一些文件合并成一个新的文件
     *
     * @param fileList       服务器上表示文件的绝对路径列表
     * @param targetFilePath 服务器上目标文件绝对路径
     */
    void mergeFiles(List<String> fileList, String targetFilePath);

    /**
     * 删除服务器文件
     *
     * @param remotePath 服务器上文件绝对路径
     */
    void removeFile(String remotePath);

    /**
     * 递归删除服务器文件夹
     *
     * @param remotePath 服务器上文件夹绝对路径
     */
    void removeDir(String remotePath);

    /**
     * 解压服务器上的压缩包
     *
     * @param remoteFilePath 服务器文件绝对路径
     */
    void unzip(String remoteFilePath);

    /**
     * 移动服务器文件（夹）
     *
     * @param srcPath    源绝对路径
     * @param targetPath 目标绝对路径
     */
    void move(String srcPath, String targetPath);

    /**
     * 如果父目录不存在的话，创建父目录
     *
     * @param remotePath 服务器文件（夹）绝对路径
     * @param permission 新目录的权限
     */
    void createParentDirIfNecessary(String remotePath, String permission);

    /**
     * 如果文件夹不存在的话，创建
     *
     * @param dirPath    服务器文件夹绝对路径
     * @param permission 新文件夹权限
     */
    void mkdirIfNotExist(String dirPath, String permission);

    /**
     * 递归创建文件夹
     *
     * @param dirPath    服务器文件夹绝对路径
     * @param permission 新文件夹权限
     */
    void mkdirs(String dirPath, String permission);

    /**
     * 文件夹是否存在
     *
     * @param remotePath 服务器文件夹绝对路径
     * @return 存在返回true
     */
    boolean checkDirectoryExists(String remotePath);

    /**
     * 文件是否存在
     *
     * @param remotePath 服务器文件绝对路径
     * @return 存在返回true
     */
    boolean checkFileExists(String remotePath);

    /**
     * 获取文件信息
     *
     * @param remotePath 服务器文件绝对路径
     * @return Data
     */
    Data getFileInfo(String remotePath);

    /**
     * 删除文件夹
     *
     * @param remotePath 服务器文件夹绝对路径
     */
    void removeDirIfExists(String remotePath);

    /**
     * 执行shell命令
     *
     * @param cmd          命令
     * @param exceptionMsg 出错异常信息
     * @return 命令的输出
     */
    String execShellCmd(String cmd, String exceptionMsg);

    /**
     * 更改文件（夹）权限
     *
     * @param permission 新的权限
     * @param remotePath 服务器绝对路径
     */
    void chmod(String permission, String remotePath);
}
