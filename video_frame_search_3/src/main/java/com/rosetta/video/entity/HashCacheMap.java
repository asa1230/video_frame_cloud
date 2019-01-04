package com.rosetta.video.entity;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存特征信息
 * @Author: nya
 * @Date: 18-7-5 上午11:34
 */
public class HashCacheMap {

    private static final Logger log = LoggerFactory.getLogger(HashCacheMap.class);

    /**
     * 数据缓存map
     */
    public static Map<String, Object> dataMap = new ConcurrentHashMap<String, Object>();

    private static Long ENTRY_TIME = 0l;

    /**
     * 将一个key、value值放入内存缓存
     *
     * @param key
     * @param val
     */
    public static void put(String key, Object val) {
        dataMap.put(key, val);
        if (ENTRY_TIME == 0l) {
            ENTRY_TIME = System.currentTimeMillis();
        }
    }

    /**
     * 从缓存中获取一个key的数据(若过期返回null)
     *
     * @param cacheKey
     * @return
     */
    public static Object get(String cacheKey) {

        Object obj = HashCacheMap.dataMap.get(cacheKey);
        return obj;
    }

    public static void remove(String cacheKey) {
        dataMap.remove(cacheKey);
    }

    public static Boolean constainsKey(String cacheKey) {
        return dataMap.containsKey(cacheKey);
    }

    public static Integer size(){
        return dataMap.size();
    }

    public static void empty() { dataMap.clear(); }

}
