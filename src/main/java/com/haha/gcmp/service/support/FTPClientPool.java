package com.haha.gcmp.service.support;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolableObjectFactory;

import java.util.NoSuchElementException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 实现了一个FTPClient连接池
 *
 * @author heaven
 */
public class FTPClientPool implements ObjectPool<FTPClient> {
    private static final int DEFAULT_POOL_SIZE = 2;
    private final BlockingQueue<FTPClient> pool;
    private final FtpClientFactory factory;
    private static final ThreadLocal<FTPClient> existedFTPClient = new ThreadLocal<>();
    private static final ThreadLocal<Integer> reentrantTime = ThreadLocal.withInitial(() -> 0);

    /**
     * 初始化连接池，需要注入一个工厂来提供FTPClient实例
     *
     * @param factory
     * @throws Exception
     */
    public FTPClientPool(FtpClientFactory factory) throws Exception {
        this(DEFAULT_POOL_SIZE, factory);
    }

    public FTPClientPool(int poolSize, FtpClientFactory factory) throws Exception {
        this.factory = factory;
        pool = new ArrayBlockingQueue<FTPClient>(poolSize * 2);
        initPool(poolSize);
    }

    /**
     * 初始化连接池，需要注入一个工厂来提供FTPClient实例
     *
     * @param maxPoolSize
     * @throws Exception
     */
    private void initPool(int maxPoolSize) throws Exception {
        for (int i = 0; i < maxPoolSize; i++) {
            //往池中添加对象
            addObject();
        }

    }

    /* (non-Javadoc)
     * @see org.apache.commons.pool.ObjectPool#borrowObject()
     */
    @Override
    public FTPClient borrowObject() throws Exception, NoSuchElementException, IllegalStateException {
        FTPClient client;
        client = existedFTPClient.get();
        if (client != null) {
            reentrantTime.set(reentrantTime.get() + 1);
            return client;
        }

        client = pool.take();
        if (!factory.validateObject(client)) {
            //使对象在池中失效
            invalidateObject(client);
            //制造并添加新对象到池中
            client = factory.makeObject();
            addObject();
        }
        existedFTPClient.set(client);
        reentrantTime.set(1);
        return client;

    }

    /* (non-Javadoc)
     * @see org.apache.commons.pool.ObjectPool#returnObject(java.lang.Object)
     */
    @Override
    public void returnObject(FTPClient client) throws Exception {
        reentrantTime.set(reentrantTime.get() - 1);
        if (reentrantTime.get() == 0) {
            pool.put(client);
            existedFTPClient.set(null);
        }


    }

    @Override
    public void invalidateObject(FTPClient client) throws Exception {
        //移除无效的客户端
        pool.remove(client);
    }

    /* (non-Javadoc)
     * @see org.apache.commons.pool.ObjectPool#addObject()
     */
    @Override
    public void addObject() throws Exception, IllegalStateException, UnsupportedOperationException {
        //插入对象到队列
        pool.put(factory.makeObject());
    }

    @Override
    public int getNumIdle() throws UnsupportedOperationException {
        return 0;
    }

    @Override
    public int getNumActive() throws UnsupportedOperationException {
        return 0;
    }

    public void clear() throws Exception, UnsupportedOperationException {

    }

    /* (non-Javadoc)
     * @see org.apache.commons.pool.ObjectPool#close()
     */
    @Override
    public void close() throws Exception {
        while (pool.iterator().hasNext()) {
            FTPClient client = pool.take();
            factory.destroyObject(client);
        }
    }

    @Override
    public void setFactory(PoolableObjectFactory<FTPClient> poolableObjectFactory) throws IllegalStateException, UnsupportedOperationException {

    }

}
