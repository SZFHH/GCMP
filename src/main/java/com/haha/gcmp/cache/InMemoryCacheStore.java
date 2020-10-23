package com.haha.gcmp.cache;


import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * In-memory cache store.
 *
 * @author johnniang
 */

public class InMemoryCacheStore extends AbstractStringCacheStore {
    private static final Logger log = LoggerFactory.getLogger(InMemoryCacheStore.class);
    /**
     * Cleaner schedule period. (s)
     */
    private final static long PERIOD = 60;

    /**
     * Cache container.
     */
    private final ConcurrentHashMap<String, CacheWrapper<String>> CACHE_CONTAINER = new ConcurrentHashMap<>();

    private final ScheduledExecutorService executor;

    /**
     * Lock.
     */
    private final Lock lock = new ReentrantLock();

    public InMemoryCacheStore() {
        // Run a cache store cleaner
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("InMemoryCache-pool-%d").build();
        executor = new ScheduledThreadPoolExecutor(1, namedThreadFactory);
        executor.scheduleAtFixedRate(new CacheExpiryCleaner(), 0, PERIOD, TimeUnit.SECONDS);
    }

    @Override
    Optional<CacheWrapper<String>> getInternal(String key) {
        Assert.hasText(key, "Cache key must not be blank");

        return Optional.ofNullable(CACHE_CONTAINER.get(key));
    }

    @Override
    void putInternal(String key, CacheWrapper<String> cacheWrapper) {
        Assert.hasText(key, "Cache key must not be blank");
        Assert.notNull(cacheWrapper, "Cache wrapper must not be null");

        // Put the cache wrapper
        CacheWrapper<String> putCacheWrapper = CACHE_CONTAINER.put(key, cacheWrapper);

        log.debug("Put [{}] cache result: [{}], original cache wrapper: [{}]", key, putCacheWrapper, cacheWrapper);
    }

    @Override
    Boolean putInternalIfAbsent(String key, CacheWrapper<String> cacheWrapper) {
        Assert.hasText(key, "Cache key must not be blank");
        Assert.notNull(cacheWrapper, "Cache wrapper must not be null");

        log.debug("Preparing to put key: [{}], value: [{}]", key, cacheWrapper);

        lock.lock();
        try {
            // Get the value before
            Optional<String> valueOptional = get(key);

            if (valueOptional.isPresent()) {
                log.warn("Failed to put the cache, because the key: [{}] has been present already", key);
                return false;
            }

            // Put the cache wrapper
            putInternal(key, cacheWrapper);
            log.debug("Put successfully");
            return true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void delete(String key) {
        Assert.hasText(key, "Cache key must not be blank");

        CACHE_CONTAINER.remove(key);
        log.debug("Removed key: [{}]", key);
    }


    public void preDestroy() {
        log.debug("Cancelling all timer tasks");
        executor.shutdown();
        clear();
    }

    private void clear() {
        CACHE_CONTAINER.clear();
    }


    private class CacheExpiryCleaner implements Runnable {

        @Override
        public void run() {
            CACHE_CONTAINER.keySet().forEach(key -> {
                if (!InMemoryCacheStore.this.get(key).isPresent()) {
                    log.debug("Deleted the cache: [{}] for expiration", key);
                }
            });
        }
    }
}
