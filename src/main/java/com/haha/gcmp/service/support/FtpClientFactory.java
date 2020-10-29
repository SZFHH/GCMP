package com.haha.gcmp.service.support;

/**
 * @author SZFHH
 * @date 2020/10/27
 */

import com.haha.gcmp.config.FTPClientConfigure;
import com.haha.gcmp.exception.ServiceException;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.pool.PoolableObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE;

/**
 * FTPClient工厂类，通过FTPClient工厂提供FTPClient实例的创建和销毁
 *
 * @author heaven
 */
public class FtpClientFactory implements PoolableObjectFactory<FTPClient> {
    private static Logger logger = LoggerFactory.getLogger("file");
    private FTPClientConfigure config;

    //给工厂传入一个参数对象，方便配置FTPClient的相关参数
    public FtpClientFactory(FTPClientConfigure config) {
        this.config = config;
    }

    /* (non-Javadoc)
     * @see org.apache.commons.pool.PoolableObjectFactory#makeObject()
     */
    @Override
    public FTPClient makeObject() throws IOException {
        FTPClient ftpClient = new FTPClient();
//        ftpClient.setConnectTimeout(config.getClientTimeout());

        ftpClient.connect(config.getHost(), config.getPort());
        int reply = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftpClient.disconnect();
            throw new IOException("ftpClient连接失败:" + config.getUsername() + " ; password:" + config.getPassword());
        }
        boolean result = ftpClient.login(config.getUsername(), config.getPassword());
        if (!result) {
            throw new ServiceException("ftpClient登陆失败! userName:" + config.getUsername() + " ; password:" + config.getPassword());
        }
        ftpClient.setFileType(BINARY_FILE_TYPE);
        ftpClient.setBufferSize(1024);
//            ftpClient.setControlEncoding(config.getEncoding());
        ftpClient.enterLocalPassiveMode();

        return ftpClient;
    }

    /* (non-Javadoc)
     * @see org.apache.commons.pool.PoolableObjectFactory#destroyObject(java.lang.Object)
     */
    @Override
    public void destroyObject(FTPClient ftpClient) throws Exception {
        try {
            if (ftpClient != null && ftpClient.isConnected()) {
                ftpClient.logout();
            }
        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            // 注意,一定要在finally代码中断开连接，否则会导致占用ftp连接情况
            try {
                ftpClient.disconnect();
            } catch (IOException io) {
                io.printStackTrace();
            }
        }
    }

    /* (non-Javadoc)
     * @see org.apache.commons.pool.PoolableObjectFactory#validateObject(java.lang.Object)
     */
    @Override
    public boolean validateObject(FTPClient ftpClient) {
        boolean valid = false;
        try {
            if (ftpClient != null) {
                valid = ftpClient.sendNoOp();
            }
        } catch (IOException e) {
            System.out.println(ftpClient + "失效");
            System.out.println(e.getMessage());
        }
        return valid;
    }

    @Override
    public void activateObject(FTPClient ftpClient) throws Exception {
    }

    @Override
    public void passivateObject(FTPClient ftpClient) throws Exception {

    }
}
