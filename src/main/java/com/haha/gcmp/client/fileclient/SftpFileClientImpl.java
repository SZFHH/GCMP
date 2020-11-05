package com.haha.gcmp.client.fileclient;

import ch.ethz.ssh2.SFTPv3DirectoryEntry;
import com.haha.gcmp.client.fileclient.pool.SftpClientFactory;
import com.haha.gcmp.client.fileclient.pool.SftpClientPool;
import com.haha.gcmp.config.propertites.SftpPoolConfig;
import com.haha.gcmp.config.propertites.SshPoolConfig;
import com.haha.gcmp.exception.ServiceException;
import com.haha.gcmp.model.entity.Data;
import com.haha.gcmp.model.entity.ServerProperty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 基于SFTP协议的 File Client实现
 *
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
    protected List<Data> doListDir(String dirPath, SftpClient ftpClient) {
        List<?> temp;
        try {
            temp = ftpClient.ls(dirPath);
        } catch (IOException e) {
            throw new ServiceException("获取文件列表异常。服务器：" + hostName + " 路径：" + dirPath, e);
        }
        List<Data> data = new ArrayList<>();
        for (Object o : temp) {
            SFTPv3DirectoryEntry ftpFile = (SFTPv3DirectoryEntry) o;
            data.add(new Data(ftpFile.attributes.isRegularFile(), ftpFile.filename, ftpFile.attributes.size, ""));
        }
        return data;
    }

    @Override
    protected void doPut(byte[] data, String remoteFilePath, SftpClient ftpClient) {
        try {
            ftpClient.put(data, remoteFilePath);
        } catch (IOException e) {
            throw new ServiceException("上传文件:" + remoteFilePath + " 至服务器:" + hostName + "异常。", e);
        }
    }
}

