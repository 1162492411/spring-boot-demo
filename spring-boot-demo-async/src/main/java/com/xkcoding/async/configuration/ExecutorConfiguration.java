package com.xkcoding.async.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 自定义线程池方法一：线程池配置类
 * @author zyg
 */
@Configuration
public class ExecutorConfiguration {

    /**
     * 配置应用访问日志专用线程池
     * @return
     */
    @Bean(name = "sodAppLogAsyncExecutor")
    public ThreadPoolTaskExecutor asyncExecutor() {
        ThreadPoolTaskExecutor threadPool = new ThreadPoolTaskExecutor();
        threadPool.setThreadNamePrefix("drs-sodAppLog-");
        threadPool.setCorePoolSize(3);
        threadPool.setMaxPoolSize(4);
        threadPool.setKeepAliveSeconds(60);
        threadPool.setQueueCapacity(11);
        threadPool.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        //优雅关闭
        threadPool.setWaitForTasksToCompleteOnShutdown(true);
        threadPool.setAwaitTerminationSeconds(60 * 15);
        return threadPool;
    }

    /**
     * 配置自定义演示线程池
     * @return
     */
    @Bean(name = "demoExecutor")
    public ThreadPoolTaskExecutor demoExecutor() {
        ThreadPoolTaskExecutor threadPool = new ThreadPoolTaskExecutor();
        threadPool.setThreadNamePrefix("demo-");
        threadPool.setCorePoolSize(2);
        threadPool.setMaxPoolSize(3);
        threadPool.setKeepAliveSeconds(60);
        threadPool.setQueueCapacity(5);
        threadPool.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        return threadPool;
    }

    /**
     * 配置自定义的演示用简单线程池
     * @return
     */
    @Bean(name = "demoSimpleExecutor")
    public SimpleAsyncTaskExecutor demoSimpleExecutor(){
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();
        executor.setDaemon(true);
        executor.setThreadPriority(5);
        executor.setThreadNamePrefix("simple-demo-");
        executor.setConcurrencyLimit(2);
        return executor;
    }

}
