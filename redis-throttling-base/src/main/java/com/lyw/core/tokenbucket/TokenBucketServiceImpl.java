package com.lyw.core.tokenbucket;

import com.lyw.core.ThrottlingService;
import redis.clients.jedis.Jedis;

import java.util.*;

/**
 * 实现令牌桶算法，完成限流操作
 */
public class TokenBucketServiceImpl implements ThrottlingService {


    private static String prefix = "throttling:funnel:";

    private Jedis jedis;
    public TokenBucketServiceImpl(Jedis jedis){
        this.jedis = jedis;
    }


    @Override
    public boolean canAccess(String token) {
        return false;
    }

    public boolean canAccess(String token, int capacity, int quota, int operations, int seconds) {
        List<Long> result = (List<Long>) jedis.eval("return redis.call('cl.throttle', KEYS[1], ARGV[1], ARGV[2], ARGV[3], ARGV[4])",
                Arrays.asList(prefix + token), Arrays.asList(String.valueOf(capacity), String.valueOf(operations), String.valueOf(seconds), String.valueOf(quota)));
        return result.get(0) == 0L;
    }
}
