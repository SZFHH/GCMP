package com.haha.gcmp.model.entity;

/**
 * @author SZFHH
 * @date 2020/11/1
 */
public class ServerStatus {

    private int gpuAvailable;

    private long diskAvailable;

    private long memoryAvailable;

    public int getGpuAvailable() {
        return gpuAvailable;
    }

    public void setGpuAvailable(int gpuAvailable) {
        this.gpuAvailable = gpuAvailable;
    }

    public long getDiskAvailable() {
        return diskAvailable;
    }

    public void setDiskAvailable(long diskAvailable) {
        this.diskAvailable = diskAvailable;
    }

    public long getMemoryAvailable() {
        return memoryAvailable;
    }

    public void setMemoryAvailable(long memoryAvailable) {
        this.memoryAvailable = memoryAvailable;
    }
}
