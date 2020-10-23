package com.haha.gcmp.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.haha.gcmp.exception.ServiceException;
import com.haha.gcmp.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * String cache store.
 *
 * @author johnniang
 */

public abstract class AbstractStringCacheStore extends AbstractCacheStore<String, String> {
    private static final Logger log = LoggerFactory.getLogger(AbstractCacheStore.class);

    protected Optional<CacheWrapper<String>> jsonToCacheWrapper(String json) {
        Assert.hasText(json, "json value must not be null");
        CacheWrapper<String> cacheWrapper = null;
        try {
            cacheWrapper = JsonUtils.jsonToObject(json, CacheWrapper.class);
        } catch (IOException e) {
            e.printStackTrace();
            log.debug("Failed to convert json to wrapper value bytes: [{}]", json, e);
        }
        return Optional.ofNullable(cacheWrapper);
    }


    public <T> void put(String key, T value) {
        try {
            put(key, JsonUtils.objectToJson(value));
        } catch (JsonProcessingException e) {
            throw new ServiceException("Failed to convert " + value + " to json", e);
        }
    }

    public <T> void put(@NonNull String key, @NonNull T value, long timeout, @NonNull TimeUnit timeUnit) {
        try {
            put(key, JsonUtils.objectToJson(value), timeout, timeUnit);
        } catch (JsonProcessingException e) {
            throw new ServiceException("Failed to convert " + value + " to json", e);
        }
    }

    public <T> Optional<T> get(String key, Class<T> type) {
        Assert.notNull(type, "Type must not be null");

        return get(key).map(value -> {
            try {
                return JsonUtils.jsonToObject(value, type);
            } catch (IOException e) {
                log.error("Failed to convert json to type: " + type.getName(), e);
                return null;
            }
        });
    }
}
