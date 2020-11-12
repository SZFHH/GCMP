package com.haha.gcmp.client.statusclient;

import com.haha.gcmp.client.AbstractSshClient;
import com.haha.gcmp.config.propertites.StatusSshPoolConfig;
import com.haha.gcmp.model.entity.ServerProperty;
import com.haha.gcmp.model.entity.ServerStatus;
import com.haha.gcmp.utils.SshUtils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 服务器空闲资源 Client
 *
 * @author SZFHH
 * @date 2020/11/1
 */
public class StatusClientImpl extends AbstractSshClient implements StatusClient {

    private final String hostName;
    private final String gpuSeries;
    private final int gpuTotal;
    private AtomicInteger gpuAvailable;
    private final long diskTotal;
    private final long memoryTotal;

    public StatusClientImpl(StatusSshPoolConfig sshPoolConfig, ServerProperty serverProperty) {
        super(sshPoolConfig, serverProperty);
        this.hostName = serverProperty.getHostName();
        this.gpuTotal = serverProperty.getGpus();
        this.gpuAvailable = new AtomicInteger(gpuTotal);
        this.gpuSeries = serverProperty.getGpuSeries();
        this.memoryTotal = getMemoryInfo()[0];
        this.diskTotal = getDiskInfo()[0];

    }

    private long[] getMemoryInfo() {
        String cmd = "free -b";
        String msg = "获取内存信息异常：" + hostName;
        String rawInfo = execShellCmd(cmd, msg);
        return SshUtils.processMemoryInfo(rawInfo);
    }

    private long[] getDiskInfo() {
        String cmd = "df";
        String msg = "获取硬盘信息异常：" + hostName;
        String rawInfo = execShellCmd(cmd, msg);
        return SshUtils.processDiskInfo(rawInfo);
    }

    @Override
    public ServerStatus getAllAvailable() {
        ServerStatus rv = new ServerStatus();

        long[] diskInfo = getDiskInfo();
        rv.setDiskAvailable(diskInfo[1]);

        long[] memoryInfo = getMemoryInfo();
        rv.setMemoryAvailable(memoryInfo[1]);

        rv.setGpuAvailable(gpuAvailable.get());
        return rv;
    }

    @Override
    public String getGpuSeries() {
        return gpuSeries;
    }

    @Override
    public int getGpuTotal() {
        return gpuTotal;
    }

    @Override
    public int getGpuAvailable() {
        return gpuAvailable.get();
    }

    @Override
    public long getDiskTotal() {
        return diskTotal;
    }

    @Override
    public long getMemoryTotal() {
        return memoryTotal;
    }

    @Override
    public long getDiskAvailable() {
        return getDiskInfo()[1];
    }

    @Override
    public long getMemoryAvailable() {
        return getMemoryInfo()[1];
    }

    @Override
    public int requestForGpus(int num) {
        int n;
        while ((n = gpuAvailable.get()) >= num) {
            if (gpuAvailable.compareAndSet(n, n - num)) {
                return -1;
            }
        }
        return gpuAvailable.get();
    }

    @Override
    public void returnGpus(int num) {
        gpuAvailable.addAndGet(num);
    }

}
