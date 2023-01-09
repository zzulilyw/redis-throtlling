package com.lyw;

import com.lyw.config.RedisClientSingletonBeanFactoryForRawJavaCode;
import com.lyw.core.tokenbucket.TokenBucketServiceImpl;

import java.util.concurrent.TimeUnit;

public class Test2 {
    public static void main(String[] args) throws InterruptedException {
        TokenBucketServiceImpl funnelService = new TokenBucketServiceImpl(RedisClientSingletonBeanFactoryForRawJavaCode.getInstance());
        for (int i = 0; i < 100; i++){
            boolean x = funnelService.canAccess("123", 5, 1, 20, 20);
            System.out.println(x);
            if (i == 50){
                TimeUnit.SECONDS.sleep(5);
            }
        }
    }
}
