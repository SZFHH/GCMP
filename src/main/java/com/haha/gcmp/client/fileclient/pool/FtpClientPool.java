package com.haha.gcmp.client.fileclient.pool;


import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool2.impl.GenericObjectPool;

/**
 * FtpClient连接池
 *
 * @author SZFHH
 * @date 2020/10/31
 */
public class FtpClientPool extends GenericObjectPool<FTPClient> {

    public FtpClientPool(FtpClientFactory clientFactory) {
        super(clientFactory, clientFactory.getFtpPoolConfig());
    }
}