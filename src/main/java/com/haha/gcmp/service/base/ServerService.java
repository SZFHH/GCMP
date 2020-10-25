package com.haha.gcmp.service.base;

/**
 * @author SZFHH
 * @date 2020/10/24
 */
public interface ServerService<T> {

    String getHostIp(String hostName);

    T getClient(String hostName);
}
