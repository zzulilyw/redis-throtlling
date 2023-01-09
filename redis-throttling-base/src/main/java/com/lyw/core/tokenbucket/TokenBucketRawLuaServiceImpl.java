package com.lyw.core.tokenbucket;

import com.lyw.core.ThrottlingService;
import redis.clients.jedis.Jedis;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * lua原生方式实现
 */
public class TokenBucketRawLuaServiceImpl implements ThrottlingService {

    private static String prefix = "throttling:funnel:";

    private static String luaScript = null;

    static {
        Path path = Paths.get(
                "/Users/lyw/IdeaProjects/redis-throttling/redis-throttling-base/src/main/resources/lua/tokenBucket.lua"
        );
        try {
            luaScript = new String(Files.readAllBytes(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Jedis jedis;

    public TokenBucketRawLuaServiceImpl(Jedis jedis){
        this.jedis = jedis;
    }

    @Override
    public boolean canAccess(String token) {
        return false;
    }

    public boolean canAccess(String token, long intervalPerTokens, long initTokens, long bucketMaxTokens, long resetBucketInterval) {
        Long result = (Long) jedis.eval(luaScript,
                Arrays.asList(prefix + token),
                Arrays.asList(String.valueOf(intervalPerTokens), String.valueOf(System.currentTimeMillis()), String.valueOf(initTokens),
                        String.valueOf(bucketMaxTokens),
                        String.valueOf(resetBucketInterval)));
        System.out.println(Thread.currentThread().getName()+" result:" + result);
        return result != 0;
    }
}
