package com.zyg.batch.job.multiRead;

import com.zyg.batch.entity.User;
import com.zyg.batch.exception.BusinessException;
import com.zyg.batch.job.compositeReader.OutReader;
import com.zyg.batch.job.simple.SimpleProcessor;
import com.zyg.batch.job.simple.SimpleWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration("multiReadJobConfig")
@Slf4j
public class JobConfig {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    /**
     * 编排 - 定义Step,将ItemReader、ItemProcess、ItemWriter编排到一起
     */
    @Bean("multiReaderStep")
    public Step mybatisPagingStep(){
        return  stepBuilderFactory.get("multiReaderStep")
            .chunk(2)
            .reader(new AggregateReader())
            .processor(new AggregateProcessor())
            .writer(new AggregateWriter())
            .build();
    }

    /**
     * 编排 - 定义Job,将Step编排到一起
     */
    @Bean("multiReaderJob")
    public Job mybatisPagingJob(){
        return jobBuilderFactory.get("multiReaderJob")
            .start(mybatisPagingStep())
            .build();
    }

}

