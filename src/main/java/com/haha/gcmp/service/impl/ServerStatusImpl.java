package com.haha.gcmp.service.impl;

import com.haha.gcmp.config.propertites.GcmpProperties;
import com.haha.gcmp.model.entity.ServerProperty;
import com.haha.gcmp.model.entity.ServerStatus;
import com.haha.gcmp.service.ServerStatusService;
import com.haha.gcmp.service.base.AbstractServerService;
import com.haha.gcmp.service.support.statusclient.StatusClient;
import org.springframework.stereotype.Service;

/**
 * @author SZFHH
 * @date 2020/11/1
 */
@Service
public class ServerStatusImpl extends AbstractServerService<StatusClient> implements ServerStatusService {

    protected ServerStatusImpl(GcmpProperties gcmpProperties) {
        super(gcmpProperties);
    }

    @Override
    protected StatusClient doInitClientContainer(ServerProperty serverProperty) {
        return new StatusClient(serverProperty);
    }

    @Override
    public ServerStatus getAll(int serverId) {
        StatusClient statusClient = getClient(serverId);
        return statusClient.getAll();
    }

    @Override
    public int getGpuAvailable(int serverId) {
        StatusClient statusClient = getClient(serverId);
        return statusClient.getGpuAvailable();
    }

    @Override
    public long getDiskAvailable(int serverId) {
        StatusClient statusClient = getClient(serverId);
        return statusClient.getDiskAvailable();
    }

    @Override
    public long getMemoryAvailable(int serverId) {
        StatusClient statusClient = getClient(serverId);
        return statusClient.getMemoryAvailable();
    }

    @Override
    public int getGpuTotal(int serverId) {
        StatusClient statusClient = getClient(serverId);
        return statusClient.getGpuTotal();
    }

    @Override
    public long getDiskTotal(int serverId) {
        StatusClient statusClient = getClient(serverId);
        return statusClient.getDiskTotal();
    }

    @Override
    public long getMemoryTotal(int serverId) {
        StatusClient statusClient = getClient(serverId);
        return statusClient.getMemoryTotal();
    }

    @Override
    public int requestForGpus(int serverId, int gpus) {
        StatusClient statusClient = getClient(serverId);
        return statusClient.requestForGpus(gpus);
    }

    @Override
    public void returnGpus(int serverId, int gpus) {
        StatusClient statusClient = getClient(serverId);
        statusClient.returnGpus(gpus);
    }

}
