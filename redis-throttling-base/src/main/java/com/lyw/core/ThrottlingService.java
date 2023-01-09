package com.lyw.core;

/**
 * 限流的接口定义
 */
public interface ThrottlingService {
    boolean canAccess(String token);

}
