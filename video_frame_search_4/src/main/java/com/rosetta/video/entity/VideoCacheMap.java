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
public class VideoCacheMap {

    private static final Logger log = LoggerFactory.getLogger(VideoCacheMap.class);

    /**
     * 数据缓存map
     */
    public static Map<String, Object> dataMap = new ConcurrentHashMap<String, Object>();

    private static Long ENTRY_TIME = 0l;

    private static Integer size = 0;

    /**
     * 将一个key、value值放入内存缓存
     *
     * @param key
     * @param val
     */
    public static void put(String key, Object val) {
        dataMap.put(key, val);
        size++;
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

        Object obj = VideoCacheMap.dataMap.get(cacheKey);
        return obj;
    }

    public static void remove(String cacheKey) {
        dataMap.remove(cacheKey);
        size = 0 ;
    }

    public static Boolean constainsKey(String cacheKey) {
        return dataMap.containsKey(cacheKey);
    }

    public static Integer size(){
        return size;
    }

}
