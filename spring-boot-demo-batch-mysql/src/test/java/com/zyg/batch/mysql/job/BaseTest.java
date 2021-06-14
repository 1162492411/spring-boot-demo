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

    @Test
    @Rollback(value = false)
    public void testTx() throws Exception {
        for (int i = 0; i < 3; i++) {
            List<Teacher> teacherList = new ArrayList<>();
            for (int j = 0; j < 2; j++) {
                Teacher teacher = new Teacher();
                teacher.setName("mock" + (j+1) + i * 10);
                teacherList.add(teacher);
            }
            teacherService.saveBatch(teacherList);
        }
    }



}
