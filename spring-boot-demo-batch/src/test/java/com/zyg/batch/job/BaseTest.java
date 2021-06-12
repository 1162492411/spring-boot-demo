package com.zyg.batch.job;

import com.zyg.batch.utils.SpringBeanUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class BaseTest {
    @Autowired
    public JobLauncher jobLauncher;
    public JobParameters emptyJobParameters = new JobParameters();

    /**
     * MyBatis分页读取插件示例
     * @See com.zyg.batch.job.mybatisPagingReader
     */
    @Test
    public void testMybatisPagingJob() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        JobParameters jobParameters = new JobParametersBuilder().addLong("leftAge", 12L).toJobParameters();
        Job job = SpringBeanUtil.getBean("mybatisPagingDemo-Job",Job.class);
        JobExecution jobExecution = jobLauncher.run(job, jobParameters);
        Assert.assertEquals(jobExecution.getStatus(),BatchStatus.COMPLETED);
    }

    @Test
    public void testCompositeReaderJob() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        JobParameters jobParameters = new JobParametersBuilder()
            .addLong("leftAge", 11L)
            .addLong("rightAge", 17L)
            .toJobParameters();
        Job job = SpringBeanUtil.getBean("",Job.class);
        JobExecution jobExecution = jobLauncher.run(job, jobParameters);
        Assert.assertEquals(jobExecution.getStatus(),BatchStatus.COMPLETED);
    }

    @Test
    public void testMultiReaderJob() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        JobParameters jobParameters = new JobParametersBuilder()
            .addLong("leftAge", 11L)
            .addLong("rightAge", 17L)
            .toJobParameters();
        Job job = SpringBeanUtil.getBean("",Job.class);
        JobExecution jobExecution = jobLauncher.run(job, jobParameters);
        Assert.assertEquals(jobExecution.getStatus(),BatchStatus.COMPLETED);
    }

    //todo:这个job目前会无限循环,因为插入的数据导致下次会被读取出来
    @Test
    public void testCompositeWriterJob() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        JobParameters jobParameters = new JobParametersBuilder()
            .addLong("leftId", 11L)
            .addLong("rightRight", 17L)
            .toJobParameters();
        Job job = SpringBeanUtil.getBean("",Job.class);
        JobExecution jobExecution = jobLauncher.run(job, jobParameters);
        Assert.assertEquals(jobExecution.getStatus(),BatchStatus.COMPLETED);
    }

    @Test
    public void testMysqlWriterDemoJob() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        Job job = SpringBeanUtil.getBean("realSimpleWriterDemoJob",Job.class);
        JobExecution jobExecution = jobLauncher.run(job, emptyJobParameters);
        Assert.assertEquals(jobExecution.getStatus(),BatchStatus.COMPLETED);
    }

    @Test
    public void testCommonMultiDemoJobConfig() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        Job job = SpringBeanUtil.getBean("commonMultiDemoJob",Job.class);
        JobParameters jobParameters = new JobParametersBuilder()
            .addLong("leftId", 11L)
            .addLong("rightId", 17L)
            .toJobParameters();
        JobExecution jobExecution = jobLauncher.run(job, jobParameters);
        Assert.assertEquals(jobExecution.getStatus(),BatchStatus.COMPLETED);
    }

    @Test
    public void testTxSimpleJob() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        Job job = SpringBeanUtil.getBean("txSimpleJob",Job.class);
        JobExecution jobExecution = jobLauncher.run(job, emptyJobParameters);
        Assert.assertEquals(jobExecution.getStatus(),BatchStatus.COMPLETED);
    }


}
