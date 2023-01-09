package com.lyw;

import com.lyw.core.TimeWindow;
import com.lyw.factory.AbstractTimeWindowFactory;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Test {
    public static void main(String[] args) throws InterruptedException {
        TimeWindow timeWindow = AbstractTimeWindowFactory.getInstance(1, TimeUnit.SECONDS, 10);
        new Thread(()->{
            testTimeWindow(timeWindow);
        }, "thread-1").start();

        new Thread(()->{
            testTimeWindow(timeWindow);
        },"thread-2").start();

    }

    private static void testTimeWindow(TimeWindow timeWindow) {
        String name = Thread.currentThread().getName();
        for (int i = 0; i < 100; i++){
            boolean b = timeWindow.canAccess(UUID.randomUUID().toString());
            System.out.println(name +"当前请求id:"+i+(b ? "成功" : "失败"));
            try {
                TimeUnit.MILLISECONDS.sleep(50L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println(name +"执行完成");
    }
}
