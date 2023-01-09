package com.lyw;

import com.lyw.config.RedisClientSingletonBeanFactoryForRawJavaCode;
import com.lyw.core.tokenbucket.TokenBucketRawLuaServiceImpl;

import java.util.concurrent.TimeUnit;

public class Test3 {
    public static void main(String[] args) throws InterruptedException {
        TokenBucketRawLuaServiceImpl tokenBucketRawLuaService = new TokenBucketRawLuaServiceImpl(RedisClientSingletonBeanFactoryForRawJavaCode.getInstance());
        new Thread(()->test(tokenBucketRawLuaService),"Thread1").start();
        TokenBucketRawLuaServiceImpl tokenBucketRawLuaService2 = new TokenBucketRawLuaServiceImpl(RedisClientSingletonBeanFactoryForRawJavaCode.getInstance());
        new Thread(()->test(tokenBucketRawLuaService2),"Thread2").start();
    }

    private static void test(TokenBucketRawLuaServiceImpl tokenBucketRawLuaService) {
        for(int i = 0; i < 100; i++){
            System.out.println(Thread.currentThread().getName() + " " +(tokenBucketRawLuaService.canAccess("test", 2000, 10, 10, 10000) ? "can Access" : "can not Access"));
        }
    }
}
