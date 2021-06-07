package com.zyg.batch.job;

import org.junit.runner.RunWith;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class BaseTest {
    @Autowired
    public JobLauncher jobLauncher;
    public JobParameters emptyJobParameters = new JobParameters();

}
