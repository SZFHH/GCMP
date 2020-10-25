package com.haha.gcmp.utils;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.*;
import java.util.function.Function;

/**
 * @author SZFHH
 * @date 2020/10/24
 */
public class CollectionUtils {

    public static <ID, D, V> Map<ID, V> convertToMap(@Nullable Collection<D> list, @NonNull Function<D, ID> keyFunction, @NonNull Function<D, V> valueFunction) {
        Assert.notNull(keyFunction, "Key function must not be null");
        Assert.notNull(valueFunction, "Value function must not be null");

        if (org.springframework.util.CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }

        Map<ID, V> resultMap = new HashMap<>();

        list.forEach(data -> resultMap.putIfAbsent(keyFunction.apply(data), valueFunction.apply(data)));

        return resultMap;
    }

    public static <ID, D> Map<ID, List<D>> convertToListMap(Collection<D> list, Function<D, ID> mappingFunction) {
        Assert.notNull(mappingFunction, "mapping function must not be null");


        Map<ID, List<D>> resultMap = new HashMap<>();

        list.forEach(data -> resultMap.computeIfAbsent(mappingFunction.apply(data), id -> new LinkedList<>()).add(data));

        return resultMap;
    }
}
