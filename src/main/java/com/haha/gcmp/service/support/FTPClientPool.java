package com.haha.gcmp.service.support;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolableObjectFactory;

import java.io.IOException;
import java.util.concurrent.*;

/**
 * 实现了一个FTPClient连接池
 *
 * @author heaven
 */
public class FTPClientPool implements ObjectPool<FTPClient> {
    private static final int DEFAULT_POOL_SIZE = 5;
    private final BlockingQueue<FTPClient> pool;
    private final FtpClientFactory factory;
    private final ScheduledExecutorService executor;
    private final static long PERIOD = 30;

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
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("FTPClientPool-pool-%d").build();
        executor = new ScheduledThreadPoolExecutor(1, namedThreadFactory);
        executor.scheduleAtFixedRate(new FTPClientPool.Activator(), 0, PERIOD, TimeUnit.SECONDS);
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
    public FTPClient borrowObject() throws IOException {
        FTPClient client = null;
        try {
            client = pool.take();
        } catch (InterruptedException e) {
            System.out.println("获取FTPClient阻塞被打断");
        }
        if (!factory.validateObject(client)) {
            //使对象在池中失效
            if (client != null) {
                invalidateObject(client);
            }
            //制造并添加新对象到池中
//            System.out.println("useless" + client);
            client = factory.makeObject();
//            System.out.println("newone" + client);
        }
        return client;

    }

    /* (non-Javadoc)
     * @see org.apache.commons.pool.ObjectPool#returnObject(java.lang.Object)
     */
    @Override
    public void returnObject(FTPClient client) {
        try {
            pool.put(client);
        } catch (InterruptedException e) {
            System.out.println("归还FTPClient阻塞被打断");
        }
    }

    @Override
    public void invalidateObject(FTPClient client) {
        //移除无效的客户端
        try {
            client.disconnect();
        } catch (IOException e) {
            System.out.println("invalidDataObject异常" + client);
            System.out.println(e.getMessage());
        }
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

    @Override
    public void clear() {

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

    private class Activator implements Runnable {

        @Override
        public void run() {
            System.out.println(pool.size());
        }
    }

}
