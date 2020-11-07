package com.haha.gcmp.service;

import com.haha.gcmp.model.dto.ServerPropertyDto;
import com.haha.gcmp.model.entity.ServerStatus;

import java.util.List;

/**
 * @author SZFHH
 * @date 2020/11/1
 */
public interface ServerStatusService {
    /**
     * 获取单个服务器的所有可用资源信息
     *
     * @param serverId server id
     * @return server status
     */
    ServerStatus getAllAvailable(int serverId);

    /**
     * 获取所有服务器的所有可用资源信息
     *
     * @return list of server status
     */
    List<ServerStatus> getServersAllAvailable();

    /**
     * 获取所有服务器的可用gpu数
     *
     * @return list of available gpus
     */
    List<Integer> getServersGpuAvailable();

    /**
     * 获取单台服务器的可用gpu数
     *
     * @param serverId serve id
     * @return available gpus
     */
    int getGpuAvailable(int serverId);

    /**
     * 获取单台服务器的硬盘剩余空间（字节）
     *
     * @param serverId server id
     * @return free disk space（byte）
     */
    long getDiskAvailable(int serverId);

    /**
     * 获取单台服务器的内存剩余空间（字节）
     *
     * @param serverId server id
     * @return free memory space（byte）
     */
    long getMemoryAvailable(int serverId);

    /**
     * 获取单台服务器的gpu总数
     *
     * @param serverId server id
     * @return total gpus
     */
    int getGpuTotal(int serverId);

    /**
     * 获取单台服务器的硬盘总空间
     *
     * @param serverId server id
     * @return total disk space
     */
    long getDiskTotal(int serverId);

    /**
     * 获取单台服务器的内存总空间
     *
     * @param serverId server id
     * @return total memory space
     */
    long getMemoryTotal(int serverId);

    /**
     * 申请gpu
     *
     * @param serverId server id
     * @param gpus     gpus
     * @return -1 if succeed, available gpus if failed.
     */
    int requestForGpus(int serverId, int gpus);

    /**
     * 归还gpu
     *
     * @param serverId server id
     * @param gpus     gpus
     */
    void returnGpus(int serverId, int gpus);

    /**
     * 获取单台服务器的属性
     *
     * @param serverId server id
     * @return ServerPropertyDto
     */
    ServerPropertyDto getServerProperty(int serverId);

    /**
     * 获取所有服务器的属性
     *
     * @return list of ServerPropertyDto
     */
    List<ServerPropertyDto> getServersServerProperty();

}
