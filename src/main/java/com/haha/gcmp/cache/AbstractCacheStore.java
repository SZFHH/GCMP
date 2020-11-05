package com.haha.gcmp.cache;

import com.haha.gcmp.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author SZFHH
 * @date 2020/10/18
 */

public abstract class AbstractCacheStore<K, V> implements CacheStore<K, V> {
    private static final Logger log = LoggerFactory.getLogger(AbstractCacheStore.class);

    /**
     * 获取cache wrapper
     *
     * @param key key must not be null
     * @return an optional cache wrapper
     */
    @NonNull
    abstract Optional<CacheWrapper<V>> getInternal(@NonNull K key);

    /**
     * 添加cache wrapper
     *
     * @param key          key must not be null
     * @param cacheWrapper cache wrapper must not be null
     */
    abstract void putInternal(@NonNull K key, @NonNull CacheWrapper<V> cacheWrapper);

    /**
     * 如果不存在，添加cache wrapper
     *
     * @param key          key must not be null
     * @param cacheWrapper cache wrapper must not be null
     * @return true if the key is absent and the value is set, false if the key is present before, or null if any other reason
     */
    abstract Boolean putInternalIfAbsent(@NonNull K key, @NonNull CacheWrapper<V> cacheWrapper);

    @Override
    public Optional<V> get(K key) {
        Assert.notNull(key, "Cache key must not be blank");
        return getInternal(key).map(cacheWrapper -> {
            if (cacheWrapper.getExpireAt() != null && cacheWrapper.getExpireAt().before(DateUtils.now())) {
                // Expired then delete it
                log.debug("Cache key: [{}] has been expired", key);
                delete(key);
                return null;
            }

            return cacheWrapper.getData();
        });
    }

    @Override
    public void put(K key, V value, long timeout, TimeUnit timeUnit) {
        putInternal(key, buildCacheWrapper(value, timeout, timeUnit));
    }

    @Override
    public Boolean putIfAbsent(K key, V value, long timeout, TimeUnit timeUnit) {
        return putInternalIfAbsent(key, buildCacheWrapper(value, timeout, timeUnit));
    }

    @Override
    public void put(K key, V value) {
        putInternal(key, buildCacheWrapper(value, 0, null));
    }


    @NonNull
    private CacheWrapper<V> buildCacheWrapper(@NonNull V value, long timeout, @Nullable TimeUnit timeUnit) {
        Assert.notNull(value, "Cache value must not be null");
        Assert.isTrue(timeout >= 0, "Cache expiration timeout must not be less than 1");

        Date now = DateUtils.now();

        Date expireAt = null;

        if (timeout > 0 && timeUnit != null) {
            expireAt = DateUtils.add(now, timeout, timeUnit);
        }

        // Build cache wrapper
        CacheWrapper<V> cacheWrapper = new CacheWrapper<>();
        cacheWrapper.setCreateAt(now);
        cacheWrapper.setExpireAt(expireAt);
        cacheWrapper.setData(value);

        return cacheWrapper;
    }
}
