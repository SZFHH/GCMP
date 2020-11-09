package com.haha.gcmp.client.fileclient;

import com.haha.gcmp.client.fileclient.pool.FtpClientFactory;
import com.haha.gcmp.client.fileclient.pool.FtpClientPool;
import com.haha.gcmp.config.propertites.FtpPoolConfig;
import com.haha.gcmp.config.propertites.SshPoolConfig;
import com.haha.gcmp.exception.ServiceException;
import com.haha.gcmp.model.entity.Data;
import com.haha.gcmp.model.entity.ServerProperty;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 基于Ftp协议的 File Client实现
 *
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
    protected List<Data> doListDir(String dirPath, FTPClient ftpClient) {
        FTPFile[] ftpFiles;
        try {
            ftpFiles = ftpClient.listFiles(dirPath);
        } catch (IOException e) {
            throw new ServiceException("获取文件列表异常。服务器：" + hostName + " 路径：" + dirPath, e);
        }
        List<Data> data = new ArrayList<>();
        for (FTPFile ftpFile : ftpFiles) {
            data.add(new Data(ftpFile.isFile(), ftpFile.getName(), ftpFile.getSize(), ""));
        }
        return data;

    }

    @Override
    protected void doPut(byte[] data, String remoteFilePath, FTPClient ftpClient) {
        try {
            ftpClient.storeFile(remoteFilePath, new ByteArrayInputStream(data));
        } catch (IOException e) {
            throw new ServiceException("上传文件:" + remoteFilePath + " 至服务器:" + hostName + "异常。", e);
        }
    }

    @Override
    protected byte[] doGet(String remoteFilePath, FTPClient ftpClient) {
        try {
            InputStream inputStream = ftpClient.retrieveFileStream(remoteFilePath);
            byte[] rv = IOUtils.toByteArray(inputStream);
            inputStream.close();
            ftpClient.completePendingCommand();
            return rv;
        } catch (IOException e) {
            throw new ServiceException("下载文件:" + remoteFilePath + " 从服务器:" + hostName + "异常。", e);

        }
    }
}
