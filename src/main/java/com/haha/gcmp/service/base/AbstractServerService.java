package com.haha.gcmp.service.base;

import com.haha.gcmp.config.propertites.GcmpProperties;
import com.haha.gcmp.model.entity.ServerProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Base server service implementation.
 *
 * @author SZFHH
 * @date 2020/10/24
 */
abstract public class AbstractServerService<T> implements ServerService<T> {
    protected final GcmpProperties gcmpProperties;
    protected volatile List<T> clientContainer;

    protected AbstractServerService(GcmpProperties gcmpProperties) {
        this.gcmpProperties = gcmpProperties;
    }


    public int getServerCount() {
        return gcmpProperties.getServerProperties().size();
    }

    @Override
    public T getClient(int serverId) {
        if (clientContainer == null) {
            synchronized (AbstractServerService.class) {
                if (clientContainer == null) {
                    initClientContainer();
                }
            }
        }
        return clientContainer.get(serverId);
    }

    protected void initClientContainer() {
        List<ServerProperty> serverProperties = gcmpProperties.getServerProperties();
        List<T> temp = new ArrayList<>();
        for (ServerProperty serverProperty : serverProperties) {
            temp.add(doInitClientContainer(serverProperty));
        }

        clientContainer = temp;
    }

    abstract protected T doInitClientContainer(ServerProperty serverProperty);


}
