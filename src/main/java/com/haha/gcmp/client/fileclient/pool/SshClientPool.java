package com.haha.gcmp.client.fileclient.pool;

import ch.ethz.ssh2.Connection;
import org.apache.commons.pool2.impl.GenericObjectPool;

/**
 * SshClient连接池
 *
 * @author SZFHH
 * @date 2020/10/31
 */
public class SshClientPool extends GenericObjectPool<Connection> {
    public SshClientPool(SshClientFactory clientFactory) {
        super(clientFactory, clientFactory.getSshPoolConfig());
    }
}
