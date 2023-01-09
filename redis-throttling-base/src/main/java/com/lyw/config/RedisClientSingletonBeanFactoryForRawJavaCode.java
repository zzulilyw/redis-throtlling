package com.lyw.config;

import io.lettuce.core.RedisClient;

public class RedisClientSingletonBeanFactoryForRawJavaCode {

    private volatile static RedisClient instance = null;

    public static RedisClient getInstance(){
        if (instance == null){
            synchronized (RedisClientSingletonBeanFactoryForRawJavaCode.class){
                if (instance == null){
                    instance = RedisClient.create("redis://127.0.0.1:6379");
                }
            }
        }
        return instance;
    }
}
