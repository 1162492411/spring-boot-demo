package com.zyg.batch.job.onceStepNotSucessDemo;
import com.zyg.batch.entity.User;
import com.zyg.batch.job.simple.SimpleProcessor;
import com.zyg.batch.job.simple.SimpleWriter;
import jdk.internal.org.objectweb.asm.Handle;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.PassThroughItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zyg
 */
@Configuration("onceStepNotSuccessDemo-JobConfig")
@Slf4j
public class JobConfig {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

//    /**
//     * 示例1 - 编排 - 定义Step,将ItemReader、ItemProcess、ItemWriter编排到一起
//     */
//    @Bean("onceStepNotSuccessDemo-Step1")
//    public Step step1(){
//        return  stepBuilderFactory.get("onceStepNotSuccessDemo-Step1")
//            .chunk(1)
//            .reader(new ReaderOne())
//            .processor(new PassThroughItemProcessor<>())
//            .writer(new EmptyWriter())
//            .build();
//    }


    /**
     * 示例2 - 校验Step - 校验并准备数据
     * @return
     */
    @Bean("onceStepNotSuccessDemo-Step1")
    public Step step1(){
        return  stepBuilderFactory.get("onceStepNotSuccessDemo-Step1")
            .tasklet(new BusinessTasklet())
            .build();
    }

    /**
     * 编排 - 定义Step,将ItemReader、ItemProcess、ItemWriter编排到一起
     */
    @Bean("onceStepNotSuccessDemo-Step2")
    public Step step2(){
        return  stepBuilderFactory.get("onceStepNotSuccessDemo-Step2")
            .chunk(1)
            .reader(new ReaderTwo())
            .processor(new PassThroughItemProcessor<>())
            .writer(new EmptyWriter())
            .build();
    }

    /**
     * 编排 - 定义Job,将Step编排到一起
      */
    @Bean("onceStepNotSuccessDemo-Job")
    public Job mybatisPagingJob(){
        return jobBuilderFactory.get("onceStepNotSuccessDemo-Job")
            .start(step1())
            .next(step2())
            .build();
    }

}
