package com.haha.gcmp.client.fileclient.pool;

import ch.ethz.ssh2.Connection;
import com.haha.gcmp.config.propertites.SshPoolConfig;
import com.haha.gcmp.exception.ServiceException;
import com.haha.gcmp.model.entity.ServerProperty;
import com.haha.gcmp.utils.SshUtils;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * SshClientFactory
 *
 * @author SZFHH
 * @date 2020/10/31
 */
public class SshClientFactory extends BasePooledObjectFactory<Connection> {
    private static final Logger logger = LoggerFactory.getLogger(SshClientFactory.class);

    private final SshPoolConfig sshPoolConfig;

    private final ServerProperty serverProperty;

    public SshClientFactory(SshPoolConfig sshPoolConfig, ServerProperty serverProperty) {
        this.sshPoolConfig = sshPoolConfig;
        this.serverProperty = serverProperty;
    }

    @Override
    public Connection create() {
        boolean authenticated;
        String hostIp = serverProperty.getHostIp();
        String username = serverProperty.getUsername();
        String password = serverProperty.getPassword();
        String hostName = serverProperty.getHostName();
        Connection connection = new Connection(hostIp, 22);
        try {
            connection.connect();
            authenticated = connection.authenticateWithPassword(username, password);
        } catch (IOException e) {
            throw new ServiceException("SSH连接IO异常。服务器：" + hostName, e);
        }
        if (!authenticated) {
            throw new ServiceException("SSH连接验证异常。服务器：" + hostName);
        }
        return connection;
    }

    @Override
    public PooledObject<Connection> wrap(Connection connection) {
        return new DefaultPooledObject<>(connection);
    }


    @Override
    public void destroyObject(PooledObject<Connection> p) throws Exception {
        Connection connection = p.getObject();
        connection.close();
        super.destroyObject(p);
    }

    @Override
    public boolean validateObject(PooledObject<Connection> p) {
        Connection connection = p.getObject();
        return SshUtils.validate(connection);
    }

    public SshPoolConfig getSshPoolConfig() {
        return sshPoolConfig;
    }
}
