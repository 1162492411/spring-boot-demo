package com.zyg.batch.job.simple;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author zyg
 */
@Configuration
@Slf4j
public class SimpleJob {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    /**
     * 编排 - 定义Step,将ItemReader、ItemProcess、ItemWriter编排到一起
     */
    @Bean
    public Step simpleStep(){
        return  stepBuilderFactory.get("simpleStep")
            .chunk(2)
            .reader(new SimpleReader<>())
            .processor(new SimpleProcessor())
            .writer(new SimpleWriter<>())
            .build();
    }

    /**
     * 编排 - 定义Job,将Step编排到一起
      */
    @Bean
    public Job simpleJob(){
        return jobBuilderFactory.get("simpleJob")
            .start(simpleStep())
            .build();
    }

}
