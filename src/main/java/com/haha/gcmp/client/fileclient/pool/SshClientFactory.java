package com.haha.gcmp.client.fileclient.pool;

import ch.ethz.ssh2.Connection;
import com.haha.gcmp.exception.ServiceException;
import com.haha.gcmp.model.entity.ServerProperty;
import com.haha.gcmp.utils.SshUtils;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.io.IOException;

/**
 * SshClientFactory
 *
 * @author SZFHH
 * @date 2020/10/31
 */
public class SshClientFactory extends BasePooledObjectFactory<Connection> {

    private final GenericObjectPoolConfig<Connection> sshPoolConfig;

    private final ServerProperty serverProperty;

    public SshClientFactory(GenericObjectPoolConfig<Connection> sshPoolConfig, ServerProperty serverProperty) {
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

    public GenericObjectPoolConfig<Connection> getSshPoolConfig() {
        return sshPoolConfig;
    }
}
