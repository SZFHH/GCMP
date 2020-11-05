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

    /**
     * 把list转换为map
     *
     * @param list          对象列表
     * @param keyFunction   从对象获取key的方法
     * @param valueFunction 从对象获取value的方法
     * @param <ID>          对象key类型
     * @param <D>           对象类型
     * @param <V>           对象value类型
     * @return Map<ID, V>
     */
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

    /**
     * 把list转换为map，以id为关键字进行分组
     *
     * @param list            对象列表
     * @param mappingFunction 获取id的方法
     * @param <ID>            对象id类型
     * @param <D>             对象类型
     * @return Map<ID, List < D>>
     */
    public static <ID, D> Map<ID, List<D>> convertToListMap(Collection<D> list, Function<D, ID> mappingFunction) {
        Assert.notNull(mappingFunction, "mapping function must not be null");


        Map<ID, List<D>> resultMap = new HashMap<>();

        list.forEach(data -> resultMap.computeIfAbsent(mappingFunction.apply(data), id -> new LinkedList<>()).add(data));

        return resultMap;
    }
}
