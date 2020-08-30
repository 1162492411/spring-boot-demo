package com.xkcoding.async.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;

/**
 * 自定义线程池方法二：自定义默认异步异常处理器
 * @author zyg
 */
public class CusAsyncUncaughtExceptionHandler implements AsyncUncaughtExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        logger.error("自定义异步异常处理器捕捉到异常，",ex);
    }
}
