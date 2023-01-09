package com.lyw.factory;

import com.lyw.config.RedisClientSingletonBeanFactoryForRawJavaCode;
import com.lyw.core.TimeWindow;
import com.lyw.core.timewindow.TimeWindowServiceImpl;

import java.util.concurrent.TimeUnit;

public class AbstractTimeWindowFactory{
    public static TimeWindow getInstance() {
        return new TimeWindowServiceImpl(RedisClientSingletonBeanFactoryForRawJavaCode.getInstance());
    }

    public static TimeWindow getInstance(long time, TimeUnit timeUnit, int count) {
        return new TimeWindowServiceImpl(time, timeUnit, count, RedisClientSingletonBeanFactoryForRawJavaCode.getInstance());
    }
}
