package com.haha.gcmp.service.support.client.pool;

import com.haha.gcmp.service.support.client.SftpClient;
import org.apache.commons.pool2.impl.GenericObjectPool;

/**
 * @author SZFHH
 * @date 2020/10/31
 */
public class SftpClientPool extends GenericObjectPool<SftpClient> {
    public SftpClientPool(SftpClientFactory clientFactory) {
        super(clientFactory, clientFactory.getSftpPoolConfig());
    }
}
