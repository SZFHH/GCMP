package com.haha.gcmp.client;

import ch.ethz.ssh2.Connection;
import com.haha.gcmp.client.fileclient.pool.SshClientFactory;
import com.haha.gcmp.client.fileclient.pool.SshClientPool;
import com.haha.gcmp.exception.ServiceException;
import com.haha.gcmp.model.entity.ServerProperty;
import com.haha.gcmp.utils.SshUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.io.IOException;

/**
 * @author SZFHH
 * @date 2020/11/12
 */
public abstract class AbstractSshClient implements SshClient {
    private final SshClientPool sshClientPool;

    protected AbstractSshClient(GenericObjectPoolConfig<Connection> sshPoolConfig, ServerProperty serverProperty) {
        SshClientFactory sshClientFactory = new SshClientFactory(sshPoolConfig, serverProperty);
        sshClientPool = new SshClientPool(sshClientFactory);
    }

    @Override
    public String execShellCmd(String cmd, String exceptionMsg) {
        Connection connection;
        try {
            connection = sshClientPool.borrowObject();
        } catch (Exception e) {
            throw new ServiceException("从ssh连接池获取连接异常", e);
        }
        try {
            return SshUtils.execCmd(connection, cmd);
        } catch (IOException e) {
            throw new ServiceException(exceptionMsg, e);
        } finally {
            sshClientPool.returnObject(connection);
        }
    }
}
