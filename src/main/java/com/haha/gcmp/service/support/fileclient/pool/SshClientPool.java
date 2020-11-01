package com.haha.gcmp.service.support.fileclient.pool;

import ch.ethz.ssh2.Connection;
import org.apache.commons.pool2.impl.GenericObjectPool;

/**
 * @author SZFHH
 * @date 2020/10/31
 */
public class SshClientPool extends GenericObjectPool<Connection> {
    public SshClientPool(SshClientFactory clientFactory) {
        super(clientFactory, clientFactory.getSshPoolConfig());
    }
}
