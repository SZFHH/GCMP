package com.haha.gcmp.service.support.client;

import com.haha.gcmp.config.propertites.FtpPoolConfig;
import com.haha.gcmp.config.propertites.SshPoolConfig;
import com.haha.gcmp.exception.ServiceException;
import com.haha.gcmp.model.entity.DataFile;
import com.haha.gcmp.model.support.ServerProperty;
import com.haha.gcmp.service.support.client.pool.FtpClientFactory;
import com.haha.gcmp.service.support.client.pool.FtpClientPool;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author SZFHH
 * @date 2020/10/31
 */
public class FtpFileClientImpl extends AbstractFileClient<FTPClient> {

    public FtpFileClientImpl(FtpPoolConfig ftpPoolConfig, SshPoolConfig sshPoolConfig, ServerProperty serverProperty) {
        super(sshPoolConfig, serverProperty);
        FtpClientFactory ftpClientFactory = new FtpClientFactory(ftpPoolConfig, serverProperty);
        FtpClientPool ftpClientPool = new FtpClientPool(ftpClientFactory);
        setFtpClientPool(ftpClientPool);
    }

    @Override
    protected List<DataFile> doListDir(String dirPath, FTPClient ftpClient) {
        FTPFile[] ftpFiles;
        try {
            ftpFiles = ftpClient.listFiles(dirPath);
        } catch (IOException e) {
            throw new ServiceException("获取文件列表异常。服务器：" + hostName + " 路径：" + dirPath, e);
        }
        List<DataFile> dataFiles = new ArrayList<>();
        for (FTPFile ftpFile : ftpFiles) {
            dataFiles.add(new DataFile(ftpFile.isFile(), ftpFile.getName(), ftpFile.getSize(), ""));
        }
        return dataFiles;

    }

    @Override
    protected void doPut(byte[] data, String remoteFilePath, FTPClient ftpClient) {
        try {
            ftpClient.storeFile(remoteFilePath, new ByteArrayInputStream(data));
        } catch (IOException e) {
            throw new ServiceException("上传文件(" + remoteFilePath + ")至服务器(" + hostName + ")异常。", e);
        }
    }
}
