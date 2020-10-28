package com.haha.gcmp.service.base;

/**
 * @author SZFHH
 * @date 2020/10/24
 */
public interface ServerService<T> {

    String getHostIp(String hostName);

    String getHostUser(String hostName);

    String getHostPassword(String hostName);

    T getClient(String hostName);

}
