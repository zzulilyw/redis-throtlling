package com.lyw.config;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisClientSingletonBeanFactoryForRawJavaCode {

    private volatile static JedisPool instance = null;

    public static Jedis getInstance(){
        if (instance == null){
            synchronized (RedisClientSingletonBeanFactoryForRawJavaCode.class){
                if (instance == null){
                    instance = new JedisPool("127.0.0.1", 6379);
                }
            }
        }
        return instance.getResource();
    }
}
