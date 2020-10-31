package com.haha.gcmp.service.support.client.pool;

import ch.ethz.ssh2.Connection;
import com.haha.gcmp.config.propertites.SftpPoolConfig;
import com.haha.gcmp.exception.ServiceException;
import com.haha.gcmp.model.support.ServerProperty;
import com.haha.gcmp.service.support.client.SftpClient;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author SZFHH
 * @date 2020/10/31
 */
public class SftpClientFactory extends BasePooledObjectFactory<SftpClient> {
    private static final Logger logger = LoggerFactory.getLogger(FtpClientFactory.class);

    private final SftpPoolConfig sftpPoolConfig;

    private final ServerProperty serverProperty;

    public SftpClientFactory(SftpPoolConfig sftpPoolConfig, ServerProperty serverProperty) {
        this.sftpPoolConfig = sftpPoolConfig;
        this.serverProperty = serverProperty;
    }

    /**
     * 新建对象
     */
    @Override
    public SftpClient create() throws Exception {
        boolean authenticated;
        String hostIp = serverProperty.getHostIp();
        String username = serverProperty.getUsername();
        String password = serverProperty.getPassword();
        String hostName = serverProperty.getHostName();
        System.out.println("创建一个ssh");
        Connection connection = new Connection(hostIp, 22);
        try {
            connection.connect();
            authenticated = connection.authenticateWithPassword(username, password);
        } catch (IOException e) {
            throw new ServiceException("SSH连接异常。服务器：" + hostName, e);
        }
        if (!authenticated) {
            throw new ServiceException("SSH连接验证异常。服务器：" + hostName);
        }
        return new SftpClient(connection);
    }

    @Override
    public PooledObject<SftpClient> wrap(SftpClient sftpClient) {
        return new DefaultPooledObject<>(sftpClient);
    }

    /**
     * 销毁对象
     */
    @Override
    public void destroyObject(PooledObject<SftpClient> p) throws Exception {
        SftpClient sftpClient = p.getObject();
        sftpClient.destroy();
        super.destroyObject(p);
    }

    /**
     * 验证对象
     */
    @Override
    public boolean validateObject(PooledObject<SftpClient> p) {
        SftpClient sftpClient = p.getObject();
        return sftpClient.validate();
    }

    public SftpPoolConfig getSftpPoolConfig() {
        return sftpPoolConfig;
    }
}
