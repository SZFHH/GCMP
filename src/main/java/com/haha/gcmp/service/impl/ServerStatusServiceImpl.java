package com.haha.gcmp.service.impl;

import com.haha.gcmp.client.statusclient.StatusClient;
import com.haha.gcmp.config.propertites.GcmpProperties;
import com.haha.gcmp.model.dto.ServerPropertyDTO;
import com.haha.gcmp.model.entity.ServerProperty;
import com.haha.gcmp.model.entity.ServerStatus;
import com.haha.gcmp.service.ServerStatusService;
import com.haha.gcmp.service.base.AbstractServerService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Server status service implementation.
 *
 * @author SZFHH
 * @date 2020/11/1
 */
@Service
public class ServerStatusServiceImpl extends AbstractServerService<StatusClient> implements ServerStatusService {

    protected ServerStatusServiceImpl(GcmpProperties gcmpProperties) {
        super(gcmpProperties);
    }

    @Override
    protected StatusClient doInitClientContainer(ServerProperty serverProperty) {
        return new StatusClient(serverProperty);
    }

    @Override
    public ServerStatus getAllAvailable(int serverId) {
        StatusClient statusClient = getClient(serverId);
        return statusClient.getAllAvailable();
    }

    @Override
    public List<ServerStatus> getServersAllAvailable() {
        List<ServerStatus> rv = new ArrayList<>();
        int count = gcmpProperties.getServerProperties().size();
        for (int i = 0; i < count; i++) {
            rv.add(getAllAvailable(i));
        }
        return rv;
    }

    @Override
    public List<Integer> getServersGpuAvailable() {
        List<Integer> rv = new ArrayList<>();
        int count = gcmpProperties.getServerProperties().size();
        for (int i = 0; i < count; i++) {
            rv.add(getGpuAvailable(i));
        }
        return rv;
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

    @Override
    public ServerPropertyDTO getServerProperty(int serverId) {
        return new ServerPropertyDTO(
            gcmpProperties.getServerProperties().get(serverId).getHostName(),
            getGpuTotal(serverId),
            gcmpProperties.getServerProperties().get(serverId).getGpuSeries(),
            getDiskTotal(serverId),
            getMemoryTotal(serverId));
    }

    @Override
    public List<ServerPropertyDTO> getServersServerProperty() {
        List<ServerPropertyDTO> rv = new ArrayList<>();
        int count = gcmpProperties.getServerProperties().size();
        for (int i = 0; i < count; i++) {
            rv.add(getServerProperty(i));
        }
        return rv;
    }


}
