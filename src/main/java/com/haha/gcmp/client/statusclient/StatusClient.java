package com.haha.gcmp.client.statusclient;

import ch.ethz.ssh2.Connection;
import com.haha.gcmp.exception.ServiceException;
import com.haha.gcmp.model.entity.ServerProperty;
import com.haha.gcmp.model.entity.ServerStatus;
import com.haha.gcmp.utils.SshUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 服务器空闲资源 Client
 *
 * @author SZFHH
 * @date 2020/11/1
 */
public class StatusClient {

    private static final Logger logger = LoggerFactory.getLogger(StatusClient.class);

    private final String hostName;
    private final String gpuSeries;
    private final int gpuTotal;
    private AtomicInteger gpuAvailable;
    private final long diskTotal;
    private final long memoryTotal;
    private final ReentrantLock lock;
    private final Connection connection;

    public StatusClient(ServerProperty serverProperty) {
        this.hostName = serverProperty.getHostName();
        boolean authenticated;
        Connection connection = new Connection(serverProperty.getHostIp(), 22);
        try {
            connection.connect();
            authenticated = connection.authenticateWithPassword(serverProperty.getUsername(), serverProperty.getPassword());
        } catch (IOException e) {
            throw new ServiceException("SSH连接IO异常。服务器：" + hostName, e);
        }
        if (!authenticated) {
            throw new ServiceException("SSH连接验证异常。服务器：" + hostName);
        }
        this.gpuTotal = serverProperty.getGpus();
        this.gpuAvailable = new AtomicInteger(gpuTotal);
        this.gpuSeries = serverProperty.getGpuSeries();
        this.connection = connection;
        this.lock = new ReentrantLock();
        try {
            this.memoryTotal = SshUtils.getMemoryInfo(this.connection)[0];
        } catch (IOException e) {
            throw new ServiceException("获取内存信息异常：" + hostName, e);
        }
        try {
            this.diskTotal = SshUtils.getDiskInfo(this.connection)[0];
        } catch (IOException e) {
            throw new ServiceException("获取硬盘信息异常：" + hostName, e);
        }
    }

    public ServerStatus getAllAvailable() {
        ServerStatus rv = new ServerStatus();
        try {
            long[] diskInfo = SshUtils.getDiskInfo(connection);
            rv.setDiskAvailable(diskInfo[1]);
        } catch (IOException e) {
            throw new ServiceException("获取硬盘信息异常：" + hostName, e);
        }
        try {
            long[] memoryInfo = SshUtils.getMemoryInfo(connection);
            rv.setMemoryAvailable(memoryInfo[1]);
        } catch (IOException e) {
            throw new ServiceException("获取内存信息异常：" + hostName, e);
        }
        rv.setGpuAvailable(gpuAvailable.get());
        return rv;
    }

    public String getGpuSeries() {
        return gpuSeries;
    }

    public int getGpuTotal() {
        return gpuTotal;
    }

    public int getGpuAvailable() {
        return gpuAvailable.get();
    }

    public ReentrantLock getLock() {
        return lock;
    }

    public long getDiskTotal() {
        return diskTotal;
    }

    public long getMemoryTotal() {
        return memoryTotal;
    }

    public long getDiskAvailable() {
        try {
            return SshUtils.getDiskInfo(this.connection)[1];
        } catch (IOException e) {
            throw new ServiceException("获取硬盘信息异常：" + hostName, e);
        }
    }

    public long getMemoryAvailable() {
        try {
            return SshUtils.getMemoryInfo(this.connection)[1];
        } catch (IOException e) {
            throw new ServiceException("获取内存信息异常：" + hostName, e);
        }
    }

    public int requestForGpus(int num) {
        int n;
        while ((n = gpuAvailable.get()) >= num) {
            if (gpuAvailable.compareAndSet(n, n - num)) {
                return -1;
            }
        }
        return gpuAvailable.get();
    }

    public void returnGpus(int num) {
        gpuAvailable.addAndGet(num);
    }
}
