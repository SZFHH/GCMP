package com.haha.gcmp.model.dto;

/**
 * @author SZFHH
 * @date 2020/11/7
 */
public class ServerPropertyDTO {
    private String hostName;
    private int gpuTotal;
    private String gpuSeries;
    private long diskTotal;
    private long memoryTotal;

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

    public String getGpuSeries() {
        return gpuSeries;
    }

    public void setGpuSeries(String gpuSeries) {
        this.gpuSeries = gpuSeries;
    }

    public long getDiskTotal() {
        return diskTotal;
    }

    public void setDiskTotal(long diskTotal) {
        this.diskTotal = diskTotal;
    }

    public long getMemoryTotal() {
        return memoryTotal;
    }

    public void setMemoryTotal(long memoryTotal) {
        this.memoryTotal = memoryTotal;
    }


    public ServerPropertyDTO(String hostName, int gpuTotal, String gpuSeries, long diskTotal, long memoryTotal) {
        this.hostName = hostName;
        this.gpuTotal = gpuTotal;
        this.gpuSeries = gpuSeries;
        this.diskTotal = diskTotal;
        this.memoryTotal = memoryTotal;
    }
}
