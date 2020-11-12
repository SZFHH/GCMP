package com.haha.gcmp.client.statusclient;

import com.haha.gcmp.model.entity.ServerStatus;

/**
 * @author SZFHH
 * @date 2020/11/12
 */
public interface StatusClient {
    /**
     * 获取单台服务器所有状态信息
     *
     * @return ServerStatus
     */
    ServerStatus getAllAvailable();

    /**
     * 获取GPU型号
     *
     * @return gpu型号
     */
    String getGpuSeries();

    /**
     * 获取单台服务器gpu总数
     *
     * @return gpu总数
     */
    int getGpuTotal();

    /**
     * 获取单台服务器可用gpu
     *
     * @return 可用gpu
     */
    int getGpuAvailable();

    /**
     * 获取单台服务器硬盘总量
     *
     * @return 硬盘总量
     */
    long getDiskTotal();

    /**
     * 获取单台服务器内存总量
     *
     * @return 内存总量
     */
    long getMemoryTotal();

    /**
     * 获取空闲硬盘空间
     *
     * @return 空闲硬盘空间
     */
    long getDiskAvailable();

    /**
     * 获取看空闲内存空间
     *
     * @return 空闲内存空间
     */
    long getMemoryAvailable();

    /**
     * 申请gpu
     *
     * @param num 申请gpu数
     * @return 如果成功：-1，否则返回当前可用gpu数
     */
    int requestForGpus(int num);

    /**
     * 归还gpu
     *
     * @param num 归还gpu数
     */
    void returnGpus(int num);
}
