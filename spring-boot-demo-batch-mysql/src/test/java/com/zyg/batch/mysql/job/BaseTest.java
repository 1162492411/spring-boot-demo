package com.zyg.batch.mysql.job;

import com.zyg.batch.mysql.entity.Teacher;
import com.zyg.batch.mysql.service.ITeacherService;
import com.zyg.batch.mysql.util.SpringBeanUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class BaseTest {
    @Autowired
    public JobLauncher jobLauncher;
    public JobParameters emptyJobParameters = new JobParameters();

    @Autowired
    private ITeacherService teacherService;

    @Test
    @Rollback(value = false)
    public void testMybatisPagingJob() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        JobParameters jobParameters = new JobParametersBuilder()
            .addLong("leftAge", 12L)
            .addLong("rightAge", 17L)
            .addLong("randomKey",System.currentTimeMillis())
            .toJobParameters();
        Job job = SpringBeanUtil.getBean("txSimpleDemo-Job",Job.class);
        JobExecution jobExecution = jobLauncher.run(job, jobParameters);
        Assert.assertEquals(jobExecution.getStatus(),BatchStatus.COMPLETED);
    }


    /**
     * 测试executeType
     * 结果 1.在mp+xml中时会报错Cannot change the ExecutorType when there is an existing transaction
     * 2.在mp+service时不会报错可以正常执行
     * 建议debug第一种方式,或者采用其他只使用mybatis的方式进行对比测试，看一下是哪个地方修改了executeType
     *
     */
    @Test
    public void testExecuteTypeJob() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        Job job = SpringBeanUtil.getBean("executeTypeDemo-Job",Job.class);
        JobParameters jobParameters = new JobParametersBuilder()
            .addLong("randomKey",System.currentTimeMillis())
            .toJobParameters();
        JobExecution jobExecution = jobLauncher.run(job, jobParameters);
        Assert.assertEquals(jobExecution.getStatus(),BatchStatus.COMPLETED);
    }



}
