package com.lyw.core.timewindow;

import com.lyw.core.TimeWindow;
import io.lettuce.core.LettuceFutures;
import io.lettuce.core.Range;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;

import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 通过zset来实现：
 * 每个时间节点，删除范围内的队列元素信息
 * 需要有一个线程来做这件事情
 * 该类型的变量应保证全局唯一，不同类型
 */
public class TimeWindowServiceImpl implements TimeWindow {

    private static String prefix = "throttling:timewindow:";

    private long time;
    private TimeUnit timeUnit;
    private long count;

    private String key;

    private RedisClient redisClient;
    /**
     * 记录上次的执行时间，更新下次的时间
     */
    private long lastMillis;

    public TimeWindowServiceImpl(RedisClient redisClient){
        this.time = 1;
        this.timeUnit = TimeUnit.MINUTES;
        this.count = 100L;
        this.redisClient = redisClient;
        this.key = prefix + UUID.randomUUID().toString();
        this.lastMillis = System.currentTimeMillis();
        initTask();
    }

    public TimeWindowServiceImpl(long time, TimeUnit timeUnit, long count, RedisClient redisClient){
        this.time = time;
        this.timeUnit = timeUnit;
        this.count = count;
        this.redisClient = redisClient;
        this.key = prefix + UUID.randomUUID().toString();
        this.lastMillis = System.currentTimeMillis();
        initTask();
    }

    private void initTask(){
        Timer timer = new Timer();
        long l = timeUnit.toMillis(time);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                renew(lastMillis);
                lastMillis = lastMillis + l;
            }
        }, l, l);
    }

    /**
     * TODO 添加之后要进行校验
     * @param token
     * @return
     */
    @Override
    public boolean canAccess(String token) {
        StatefulRedisConnection<String, String> connect = redisClient.connect();
        RedisAsyncCommands<String, String> async = connect.async();
        connect.setAutoFlushCommands(false);
        RedisFuture<Long> zcount1 = async.zcount(key, Range.unbounded());
        RedisFuture<Long> zadd = async.zadd(key, System.currentTimeMillis(), token);
        RedisFuture<Long> zcount = async.zcount(key, Range.unbounded());
        connect.flushCommands();
        LettuceFutures.awaitAll(10, TimeUnit.SECONDS, zcount1, zadd, zcount);
        try {
            Long c = zcount.get();
            return c - 1 <= count;
        } catch (InterruptedException e) {
            return false;
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 删除redis
     */
    public void renew(long millis){
        RedisCommands<String, String> sync = redisClient.connect().sync();
        sync.zremrangebyrank(key, 0, millis);
    }


}
