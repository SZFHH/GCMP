package com.haha.gcmp.service.base;

import com.haha.gcmp.config.propertites.GcmpProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author SZFHH
 * @date 2020/10/24
 */
abstract public class AbstractServerService<T> implements ServerService<T> {
    protected final GcmpProperties gcmpProperties;
    protected volatile Map<String, T> clientContainer;

    protected AbstractServerService(GcmpProperties gcmpProperties) {
        this.gcmpProperties = gcmpProperties;
    }

    @Override
    public String getHostIp(String hostName) {
        return gcmpProperties.getHostIps().get(hostName);
    }

    @Override
    public String getHostUser(String hostName) {
        return gcmpProperties.getHostUsers().get(hostName);
    }

    @Override
    public String getHostPassword(String hostName) {
        return gcmpProperties.getHostPasswords().get(hostName);
    }

    public Set<String> getAllHostName() {
        return gcmpProperties.getHostIps().keySet();
    }

    @Override
    public T getClient(String hostName) {
        if (clientContainer == null) {
            synchronized (AbstractServerService.class) {
                if (clientContainer == null) {
                    initClientContainer();
                }
            }
        }
        return clientContainer.get(hostName);
    }

    protected void initClientContainer() {
        Map<String, T> temp = new HashMap<>();
        Set<String> hostNames = getAllHostName();
        for (String hostName : hostNames) {
            String hostIp = getHostIp(hostName);
            String userName = getHostUser(hostName);
            String password = getHostPassword(hostName);
            temp.put(hostName, doInitClientContainer(hostName, hostIp, userName, password));
        }
        clientContainer = temp;
    }

    abstract protected T doInitClientContainer(String hostName, String hostIp, String username, String password);


}
