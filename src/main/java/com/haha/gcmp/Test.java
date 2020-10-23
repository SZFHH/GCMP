package com.haha.gcmp;

import com.haha.gcmp.cache.InMemoryCacheStore;

/**
 * @author SZFHH
 * @date 2020/10/23
 */
public class Test {
    public static void main(String[] args) {
        InMemoryCacheStore cache = new InMemoryCacheStore();
        cache.put("abc", "abc");
        cache.preDestroy();

    }
}
