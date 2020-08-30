package com.xkcoding.async.task;

import com.xkcoding.async.SpringBootDemoAsyncApplicationTests;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * <p>
 * 测试任务
 * </p>
 *
 * @package: com.xkcoding.async.task
 * @description: 测试任务
 * @author: yangkai.shen
 * @date: Created in 2018-12-29 10:49
 * @copyright: Copyright (c) 2018
 * @version: V1.0
 * @modified: yangkai.shen
 */
@Slf4j
public class TaskFactoryTest extends SpringBootDemoAsyncApplicationTests {
    @Autowired
    private TaskFactory task;

    /**
     * 测试异步任务
     */
    @Test
    public void t1() throws InterruptedException {
        task.asyncTask1();
        task.task3();
        for (int i = 0; i < 20; i++) {
            int index = i+1;
//            task.logTask(index);
            task.demoTask(index);
            Thread.sleep(10);
        }
    }

}
