package com.lyw.core.tokenbucket;

import com.lyw.core.ThrottlingService;
import io.lettuce.core.RedisClient;
import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

import java.util.*;

/**
 * 实现令牌桶算法，完成限流操作
 */
public class TokenBucketServiceImpl implements ThrottlingService {


    private static String prefix = "throttling:funnel:";

    private RedisClient redisClient;

    public TokenBucketServiceImpl(RedisClient redisClient) {
        this.redisClient = redisClient;
    }

    @Override
    public boolean canAccess(String token) {
        return false;
    }

    public boolean canAccess(String token, int capacity, int quota, int operations, int seconds) {
        StatefulRedisConnection<String, String> connect = redisClient.connect();
        RedisCommands<String, String> sync = connect.sync();
        List<Long> result = sync.eval("return redis.call('cl.throttle', KEYS[1], ARGV[1], ARGV[2], ARGV[3], ARGV[4])",
                ScriptOutputType.MULTI,
                new String[]{prefix + token},
                String.valueOf(capacity), String.valueOf(operations), String.valueOf(seconds), String.valueOf(quota));
        return result.get(0) == 0L;
    }
}
