package com.zyg.batch.job.business.SinglePagingSingle;


import com.zyg.batch.entity.User;
import com.zyg.batch.job.commonSupport.ListPassThroughItemProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zyg
 */
@Configuration("businessSinglePagingSingle-JobConfig")
@Slf4j
public class JobConfig {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

//    @JobScope
    @Bean("businessSinglePagingSingle-OutReader")
    public ItemReader<List<User>> reader(){
        //@Value("#{jobExecution}") JobExecution jobExecution
        OutReader outReader = new OutReader();
//        outReader.setExecutionContext(jobExecution.getExecutionContext());
        return outReader;
    }


    @Bean("businessSinglePagingSingle-innerReader2")
    @JobScope
    public Reader2 innerReader2(@Value("#{jobExecution}") JobExecution jobExecution){
        Reader2 reader = new Reader2();
        reader.setExecutionContext(jobExecution.getExecutionContext());
        return reader;
    }

    @Bean("businessSinglePagingSingle-Writer")
    public ItemWriter<List<User>> writer(){
        return new Writer();
    }

    /**
     * 编排 - 定义Step,将ItemReader、ItemProcess、ItemWriter编排到一起
     */
    @Bean("businessSinglePagingSingle-Step")
    public Step mybatisPagingStep(){
        return  stepBuilderFactory.get("businessSinglePagingSingle-Step")
            .<List<User>,List<User>>chunk(1)
            .reader(reader())
            .processor(new ListPassThroughItemProcessor<>())
            .writer(writer())
            .build();
    }


    @Bean("businessSinglePagingSingle-Step2")
    public Step step2(){
        return  stepBuilderFactory.get("businessSinglePagingSingle-Step2")
            .<List<User>,List<User>>chunk(1)
            .reader(innerReader2(null))
            .processor(new ListPassThroughItemProcessor<>())
            .writer(writer())
            .build();
    }


    /**
     * 编排 - 定义Job,将Step编排到一起
     */
    @Bean("businessSinglePagingSingle-Job")
    public Job mybatisPagingJob(){
        return jobBuilderFactory.get("businessSinglePagingSingle-Job")
            .start(mybatisPagingStep())
            .next(step2())
            .build();
    }

}

