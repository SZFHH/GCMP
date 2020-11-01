package com.haha.gcmp.service.support.fileclient.pool;

import com.haha.gcmp.config.propertites.FtpPoolConfig;
import com.haha.gcmp.exception.ServiceException;
import com.haha.gcmp.model.entity.ServerProperty;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * ftpclient 工厂
 *
 * @author jelly
 */
public class FtpClientFactory extends BasePooledObjectFactory<FTPClient> {

    private static final Logger logger = LoggerFactory.getLogger(FtpClientFactory.class);

    private final FtpPoolConfig ftpPoolConfig;

    private final ServerProperty serverProperty;

    public FtpClientFactory(FtpPoolConfig ftpPoolConfig, ServerProperty serverProperty) {
        this.ftpPoolConfig = ftpPoolConfig;
        this.serverProperty = serverProperty;
    }

    /**
     * 新建对象
     */
    @Override
    public FTPClient create() throws Exception {
        String hostIp = serverProperty.getHostIp();
        String username = serverProperty.getUsername();
        String password = serverProperty.getPassword();
        String hostName = serverProperty.getHostName();
        FTPClient ftpClient = new FTPClient();
        ftpClient.setConnectTimeout(ftpPoolConfig.getConnectTimeOut());
        try {

            logger.info("连接ftp服务器:" + hostIp + ":" + 21);
            ftpClient.connect(serverProperty.getHostIp(), 21);

            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                throw new ServiceException("ftpClient服务器拒绝连接，服务器" + hostName);
            }
            boolean result = ftpClient.login(username, password);
            if (!result) {
                logger.error("ftpClient登录失败!");
                throw new ServiceException("ftpClient登录失败! userName:" + username + ", password:"
                    + password);
            }

            ftpClient.setControlEncoding(ftpPoolConfig.getControlEncoding());
            ftpClient.setBufferSize(ftpPoolConfig.getBufferSize());
            ftpClient.setFileType(ftpPoolConfig.getFileType());
            ftpClient.setDataTimeout(ftpPoolConfig.getDataTimeout());
            ftpClient.setUseEPSVwithIPv4(ftpPoolConfig.isUseEpsvWithIpv4());
            if (ftpPoolConfig.isPassiveMode()) {
                logger.info("进入ftp被动模式");
                ftpClient.enterLocalPassiveMode();//进入被动模式
            }
        } catch (IOException e) {
            logger.error("FTP连接失败：", e);
            throw e;
        }
        return ftpClient;
    }

    @Override
    public PooledObject<FTPClient> wrap(FTPClient ftpClient) {
        return new DefaultPooledObject<>(ftpClient);
    }

    /**
     * 销毁对象
     */
    @Override
    public void destroyObject(PooledObject<FTPClient> p) throws Exception {
        FTPClient ftpClient = p.getObject();
        ftpClient.logout();
        ftpClient.disconnect();
        super.destroyObject(p);
    }

    /**
     * 验证对象
     */
    @Override
    public boolean validateObject(PooledObject<FTPClient> p) {
        FTPClient ftpClient = p.getObject();
        boolean connect = false;
        try {
            connect = ftpClient.sendNoOp();
        } catch (IOException e) {
            logger.error("验证ftp连接对象,返回false");
        }
        return connect;
    }

    public FtpPoolConfig getFtpPoolConfig() {
        return ftpPoolConfig;
    }
}