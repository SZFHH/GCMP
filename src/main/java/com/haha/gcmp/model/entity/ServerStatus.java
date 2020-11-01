package com.haha.gcmp.model.entity;

/**
 * @author SZFHH
 * @date 2020/11/1
 */
public class ServerStatus {
    String hostName;
    int gpuTotal;
    int gpuAvailable;
    long diskTotal;
    long diskAvailable;
    long memoryTotal;
    long memoryAvailable;

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getGpuTotal() {
        return gpuTotal;
    }

    public void setGpuTotal(int gpuTotal) {
        this.gpuTotal = gpuTotal;
    }

    public int getGpuAvailable() {
        return gpuAvailable;
    }

    public void setGpuAvailable(int gpuAvailable) {
        this.gpuAvailable = gpuAvailable;
    }

    public long getDiskTotal() {
        return diskTotal;
    }

    public void setDiskTotal(long diskTotal) {
        this.diskTotal = diskTotal;
    }

    public long getDiskAvailable() {
        return diskAvailable;
    }

    public void setDiskAvailable(long diskAvailable) {
        this.diskAvailable = diskAvailable;
    }

    public long getMemoryTotal() {
        return memoryTotal;
    }

    public void setMemoryTotal(long memoryTotal) {
        this.memoryTotal = memoryTotal;
    }

    public long getMemoryAvailable() {
        return memoryAvailable;
    }

    public void setMemoryAvailable(long memoryAvailable) {
        this.memoryAvailable = memoryAvailable;
    }
}
