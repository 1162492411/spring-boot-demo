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
    @Autowired
    @Qualifier("userMybatisPagingJob")
    private Job mybatisPagingJob;

    @Autowired
    @Qualifier("multiReaderJob")
    private Job multiReaderJob;

    @Test
    public void testMybatisPagingJob() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        JobParameters jobParameters = new JobParametersBuilder().addLong("leftAge", 12L).toJobParameters();
        JobExecution jobExecution = jobLauncher.run(mybatisPagingJob, jobParameters);

    }

    @Test
    public void testCompositeReaderJob() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        JobParameters jobParameters = new JobParametersBuilder()
            .addLong("leftAge", 11L)
            .addLong("rightAge", 17L)
            .toJobParameters();
        Job job = SpringBeanUtil.getBean("compositeReaderJob",Job.class);
        JobExecution jobExecution = jobLauncher.run(job, jobParameters);
    }

    @Test
    public void testMultiReaderJob() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        JobParameters jobParameters = new JobParametersBuilder()
            .addLong("leftAge", 11L)
            .addLong("rightAge", 17L)
            .toJobParameters();
        JobExecution jobExecution = jobLauncher.run(multiReaderJob, jobParameters);
    }

    @Test
    public void testCompositeWriterJob() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        JobParameters jobParameters = new JobParametersBuilder()
            .addLong("leftAge", 11L)
            .addLong("rightAge", 17L)
            .toJobParameters();
        Job job = SpringBeanUtil.getBean("compositeWriterJob",Job.class);
        JobExecution jobExecution = jobLauncher.run(job, jobParameters);
    }

    @Test
    public void testMysqlWriterDemoJob() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        Job job = SpringBeanUtil.getBean("mysqlWriterDemoJob",Job.class);
        JobExecution jobExecution = jobLauncher.run(job, emptyJobParameters);
        Assert.assertEquals(jobExecution.getStatus(),BatchStatus.COMPLETED);
    }


}
