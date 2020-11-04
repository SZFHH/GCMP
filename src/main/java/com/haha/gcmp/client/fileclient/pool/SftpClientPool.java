package com.haha.gcmp.client.fileclient.pool;

import com.haha.gcmp.client.fileclient.SftpClient;
import org.apache.commons.pool2.impl.GenericObjectPool;

/**
 * SftpClient连接池
 *
 * @author SZFHH
 * @date 2020/10/31
 */
public class SftpClientPool extends GenericObjectPool<SftpClient> {
    public SftpClientPool(SftpClientFactory clientFactory) {
        super(clientFactory, clientFactory.getSftpPoolConfig());
    }
}
