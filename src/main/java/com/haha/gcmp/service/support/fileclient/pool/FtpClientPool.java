package com.haha.gcmp.service.support.fileclient.pool;


import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool2.impl.GenericObjectPool;

/**
 * FTP 客户端连接池
 *
 * @author jelly
 */
public class FtpClientPool extends GenericObjectPool<FTPClient> {

    public FtpClientPool(FtpClientFactory clientFactory) {
        super(clientFactory, clientFactory.getFtpPoolConfig());
    }
}