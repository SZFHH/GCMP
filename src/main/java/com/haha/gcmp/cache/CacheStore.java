package com.haha.gcmp.cache;

import org.springframework.lang.NonNull;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Cache store interface
 *
 * @author SZFHH
 * @date 2020/10/18
 */
public interface CacheStore<K, V> {

    /**
     * 获取
     *
     * @param key must not be null
     * @return cache value
     */
    @NonNull
    Optional<V> get(@NonNull K key);

    /**
     * 添加，会过期
     *
     * @param key      cache key must not be null
     * @param value    cache value must not be null
     * @param timeout  the key expiration must not be less than 1
     * @param timeUnit timeout unit
     */
    void put(@NonNull K key, @NonNull V value, long timeout, @NonNull TimeUnit timeUnit);

    /**
     * 如果不存在，添加
     *
     * @param key      cache key must not be null
     * @param value    cache value must not be null
     * @param timeout  the key expiration must not be less than 1
     * @param timeUnit timeout unit must not be null
     * @return true if the key is absent and the value is set, false if the key is present before, or null if any other reason
     */
    Boolean putIfAbsent(@NonNull K key, @NonNull V value, long timeout, @NonNull TimeUnit timeUnit);

    /**
     * 添加，不会过期
     *
     * @param key   cache key must not be null
     * @param value cache value must not be null
     */
    void put(@NonNull K key, @NonNull V value);

    /**
     * 删除
     *
     * @param key cache key must not be null
     */
    void delete(@NonNull K key);

}
