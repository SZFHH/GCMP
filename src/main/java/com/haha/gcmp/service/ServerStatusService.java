package com.haha.gcmp.service;

import com.haha.gcmp.model.entity.ServerStatus;

/**
 * @author SZFHH
 * @date 2020/11/1
 */
public interface ServerStatusService {
    ServerStatus getAll(int serverId);

    int getGpuAvailable(int serverId);

    long getDiskAvailable(int serverId);

    long getMemoryAvailable(int serverId);

    int getGpuTotal(int serverId);

    long getDiskTotal(int serverId);

    long getMemoryTotal(int serverId);

    int requestForGpus(int serverId, int gpus);

    void returnGpus(int serverId, int gpus);

}
