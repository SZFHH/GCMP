package com.haha.gcmp.service.support.fileclient.pool;

import ch.ethz.ssh2.Connection;
import com.haha.gcmp.config.propertites.SshPoolConfig;
import com.haha.gcmp.exception.ServiceException;
import com.haha.gcmp.model.entity.ServerProperty;
import com.haha.gcmp.utils.SshUtils;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.io.IOException;

/**
 * @author SZFHH
 * @date 2020/10/31
 */
public class SshClientFactory extends BasePooledObjectFactory<Connection> {
    private final SshPoolConfig sshPoolConfig;

    private final ServerProperty serverProperty;

    public SshClientFactory(SshPoolConfig sshPoolConfig, ServerProperty serverProperty) {
        this.sshPoolConfig = sshPoolConfig;
        this.serverProperty = serverProperty;
    }

    /**
     * 新建对象
     */
    @Override
    public Connection create() throws Exception {
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
            throw new ServiceException("SSH连接异常。服务器：" + hostName, e);
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

    /**
     * 销毁对象
     */
    @Override
    public void destroyObject(PooledObject<Connection> p) throws Exception {
        Connection connection = p.getObject();
        connection.close();
        super.destroyObject(p);
    }

    /**
     * 验证对象
     */
    @Override
    public boolean validateObject(PooledObject<Connection> p) {
        Connection connection = p.getObject();
        return SshUtils.validate(connection);
    }

    public SshPoolConfig getSshPoolConfig() {
        return sshPoolConfig;
    }
}
