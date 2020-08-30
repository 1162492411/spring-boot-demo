package com.xkcoding.async.configuration;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 自定义线程池方法二：自定义类，配置默认Executor与默认异步异常处理器
 * @author zyg
 */
public class CusAsyncConfigure implements AsyncConfigurer {

    /**
     * 配置默认Executor
     */
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor threadPool = new ThreadPoolTaskExecutor();
        threadPool.setThreadNamePrefix("cus-async-configure-");
        threadPool.setCorePoolSize(2);
        threadPool.setMaxPoolSize(3);
        threadPool.setKeepAliveSeconds(60);
        threadPool.setQueueCapacity(5);
        threadPool.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        return threadPool;
    }

    /**
     * 配置默认异步异常处理器
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new CusAsyncUncaughtExceptionHandler();
    }
}
