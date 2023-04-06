package cn.qaq.valveapi.utils;

import java.util.concurrent.TimeUnit;
import com.fasterxml.jackson.core.JsonProcessingException;;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class Guava {
    private static Cache<String, String> cache;
    static {
        // 创建一个缓存对象
        cache = CacheBuilder.newBuilder()
                .maximumSize(2)
                .expireAfterAccess(120, TimeUnit.SECONDS)
                .build();
    }
    public static  void  GuavaDataSet (String user, String requestValues) throws JsonProcessingException {
        // 创建一个缓存对象，并定义缓存数据的过期时间为120秒

        // 将数据放入缓存
        cache.put(user, requestValues);
    }
    public static String GuavaDataGet (String user) throws JsonProcessingException {

        // 从缓存中获取数据
        String value = cache.getIfPresent(user);
        return value;
    }

}


