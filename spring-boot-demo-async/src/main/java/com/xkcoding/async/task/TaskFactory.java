package com.xkcoding.async.task;

import com.xkcoding.async.util.UnsafeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 任务工厂
 * </p>
 *
 * @package: com.xkcoding.async.task
 * @description: 任务工厂
 * @author: yangkai.shen
 * @date: Created in 2018-12-29 10:37
 * @copyright: Copyright (c) 2018
 * @version: V1.0
 * @modified: yangkai.shen
 */
@Component
@Slf4j
public class TaskFactory {



    /**
     * 模拟3秒的同步任务
     */
    public void task3() throws InterruptedException {
        doTask("task3", 3);
    }

    /**
     * 模拟5秒的异步任务
     */
    @Async
    public Future<Boolean> asyncTask1() throws InterruptedException {
        doTask("asyncTask1", 5);
        return new AsyncResult<>(Boolean.TRUE);
    }

    /**
     * 模拟日志专用线程池的任务
     */
    @Async(value = "sodAppLogAsyncExecutor")
    public void logTask(int x) throws InterruptedException {
        doTask("日志专用task"+x,1);
    }

    /**
     * 模拟演示专用线程池的任务
     */
    @Async(value = "demoSimpleExecutor")
    //通过查看线程的内存地址可以发现,SimpleAsyncExecutor每次都是新建的线程,并没有复用；通过查看它的doExecute()也可以证实此点
    public void demoTask(int i) throws InterruptedException {
        doTask("演示专用task" + i,1);
    }

    private void doTask(String taskName, Integer time) {
        log.info("{}模拟执行【{}】,线程内存地址:{}", taskName, Thread.currentThread().getName(), UnsafeUtil.addressOf(Thread.currentThread()));
    }



}
