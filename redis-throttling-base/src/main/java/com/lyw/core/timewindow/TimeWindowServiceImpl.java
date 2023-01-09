package com.lyw.core.timewindow;

import com.lyw.core.TimeWindow;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

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

    private Jedis jedis;
    /**
     * 记录上次的执行时间，更新下次的时间
     */
    private long lastMillis;

    public TimeWindowServiceImpl(Jedis jedis) {
        this.time = 1;
        this.timeUnit = TimeUnit.MINUTES;
        this.count = 100L;
        this.jedis = jedis;
        this.key = prefix + UUID.randomUUID().toString();
        this.lastMillis = System.currentTimeMillis();
        initTask();
    }

    public TimeWindowServiceImpl(long time, TimeUnit timeUnit, long count, Jedis jedis) {
        this.time = time;
        this.timeUnit = timeUnit;
        this.count = count;
        this.jedis = jedis;
        this.key = prefix + UUID.randomUUID().toString();
        this.lastMillis = System.currentTimeMillis();
        initTask();
    }

    private void initTask() {
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
     *
     * @param token
     * @return
     */
    @Override
    public boolean canAccess(String token) {
        Pipeline pipelined = jedis.pipelined();
        Response<Long> zcount1 = pipelined.zcount(key, 0, -1);
        Response<Long> zadd = pipelined.zadd(key, System.currentTimeMillis(), token);
        Response<Long> zcount = pipelined.zcount(key, 0, -1);
        pipelined.sync();

        Long c = zcount.get();
        Long c1 = zcount1.get();
        if (c1 + 1 != c){
            System.out.println("出现并发问题");
        }
        return c - 1 <= count;

    }


    /**
     * 删除redis
     */
    public void renew(long millis) {
        jedis.zremrangeByRank(key, 0, millis);
    }


}
