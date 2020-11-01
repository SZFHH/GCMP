package com.haha.gcmp.service.support.fileclient;

import ch.ethz.ssh2.SFTPv3DirectoryEntry;
import com.haha.gcmp.config.propertites.SftpPoolConfig;
import com.haha.gcmp.config.propertites.SshPoolConfig;
import com.haha.gcmp.exception.ServiceException;
import com.haha.gcmp.model.entity.DataFile;
import com.haha.gcmp.model.entity.ServerProperty;
import com.haha.gcmp.service.support.fileclient.pool.SftpClientFactory;
import com.haha.gcmp.service.support.fileclient.pool.SftpClientPool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author SZFHH
 * @date 2020/10/25
 */
public class SftpFileClientImpl extends AbstractFileClient<SftpClient> {

    public SftpFileClientImpl(SftpPoolConfig sftpPoolConfig, SshPoolConfig sshPoolConfig, ServerProperty serverProperty) {
        super(sshPoolConfig, serverProperty);
        SftpClientFactory sftpClientFactory = new SftpClientFactory(sftpPoolConfig, serverProperty);
        SftpClientPool sftpClientPool = new SftpClientPool(sftpClientFactory);
        setFtpClientPool(sftpClientPool);
    }


    @Override
    protected List<DataFile> doListDir(String dirPath, SftpClient ftpClient) {
        List<?> temp;
        try {
            temp = ftpClient.ls(dirPath);
        } catch (IOException e) {
            throw new ServiceException("获取文件列表异常。服务器：" + hostName + " 路径：" + dirPath, e);
        }
        List<DataFile> dataFiles = new ArrayList<>();
        for (Object o : temp) {
            SFTPv3DirectoryEntry ftpFile = (SFTPv3DirectoryEntry) o;
            dataFiles.add(new DataFile(ftpFile.attributes.isRegularFile(), ftpFile.filename, ftpFile.attributes.size, ""));
        }
        return dataFiles;
    }

    @Override
    protected void doPut(byte[] data, String remoteFilePath, SftpClient ftpClient) {
        try {
            ftpClient.put(data, remoteFilePath);
        } catch (IOException e) {
            throw new ServiceException("上传文件(" + remoteFilePath + ")至服务器(" + hostName + ")异常。", e);
        }
    }
}

