package com.haha.gcmp.service.impl;

import com.haha.gcmp.client.statusclient.StatusClientImpl;
import com.haha.gcmp.config.propertites.GcmpProperties;
import com.haha.gcmp.config.propertites.StatusSshPoolConfig;
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
public class ServerStatusServiceImpl extends AbstractServerService<StatusClientImpl> implements ServerStatusService {
    private final StatusSshPoolConfig sshPoolConfig;

    protected ServerStatusServiceImpl(GcmpProperties gcmpProperties, StatusSshPoolConfig sshPoolConfig) {
        super(gcmpProperties);
        this.sshPoolConfig = sshPoolConfig;
    }

    @Override
    protected StatusClientImpl doInitClientContainer(ServerProperty serverProperty) {
        return new StatusClientImpl(sshPoolConfig, serverProperty);
    }

    @Override
    public ServerStatus getAllAvailable(int serverId) {
        StatusClientImpl statusClient = getClient(serverId);
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
        StatusClientImpl statusClient = getClient(serverId);
        return statusClient.getGpuAvailable();
    }

    @Override
    public long getDiskAvailable(int serverId) {
        StatusClientImpl statusClient = getClient(serverId);
        return statusClient.getDiskAvailable();
    }

    @Override
    public long getMemoryAvailable(int serverId) {
        StatusClientImpl statusClient = getClient(serverId);
        return statusClient.getMemoryAvailable();
    }

    @Override
    public int getGpuTotal(int serverId) {
        StatusClientImpl statusClient = getClient(serverId);
        return statusClient.getGpuTotal();
    }

    @Override
    public long getDiskTotal(int serverId) {
        StatusClientImpl statusClient = getClient(serverId);
        return statusClient.getDiskTotal();
    }

    @Override
    public long getMemoryTotal(int serverId) {
        StatusClientImpl statusClient = getClient(serverId);
        return statusClient.getMemoryTotal();
    }

    @Override
    public int requestForGpus(int serverId, int gpus) {
        StatusClientImpl statusClient = getClient(serverId);
        return statusClient.requestForGpus(gpus);
    }

    @Override
    public void returnGpus(int serverId, int gpus) {
        StatusClientImpl statusClient = getClient(serverId);
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
