package com.haha.gcmp.service.base;

/**
 * @author SZFHH
 * @date 2020/10/24
 */
public interface ServerService<T> {
    /**
     * Get a client connected to server.
     *
     * @param serverId server id
     * @return Client
     */
    T getClient(int serverId);

}
