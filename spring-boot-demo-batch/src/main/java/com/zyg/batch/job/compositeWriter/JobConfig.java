package com.zyg.batch.job.compositeWriter;

import com.zyg.batch.entity.Teacher;
import com.zyg.batch.entity.User;
import com.zyg.batch.exception.BusinessException;
import com.zyg.batch.job.compositeReader.OutReader;
import com.zyg.batch.job.simple.SimpleProcessor;
import com.zyg.batch.job.simple.SimpleWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.support.PassThroughItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

@Configuration("compositeWriterJobConfig")
@Slf4j
public class JobConfig {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private SqlSessionFactory sqlSessionFactory;
    @Autowired
    private PlatformTransactionManager txManager;

    @Bean("compositeWriterReader")
    public ItemReader<User> reader(){
        MyBatisPagingItemReader<User> reader = new MyBatisPagingItemReader<>();
        reader.setSqlSessionFactory(sqlSessionFactory);
        reader.setQueryId("com.zyg.batch.mapper.UserMapper.selectByBatchPage");
        return reader;
    }

    @Bean("i1")
    public ItemWriter i1(){
        return new InnerWriter1();
    }

    @Bean("i2")
    public InnerWriter2 i2(){
        return new InnerWriter2();
    }

    @Bean("compositeWriterOutWriter")
    public ItemWriter<User> compositeWriters(){
        OutWriter<User> outWriter = new OutWriter<>();
//        outWriter.setInnerWriter1(new InnerWriter1());
//        outWriter.setInnerWriter2(new InnerWriter2());
        outWriter.setInnerWriter1(i1());
        outWriter.setInnerWriter2(i2());
        return outWriter;
    }

    /**
     * 编排 - 定义Step,将ItemReader、ItemProcess、ItemWriter编排到一起
     */
    @Bean("compositeWriterStep")
    public Step mybatisPagingStep(){
        DefaultTransactionAttribute txAttribute = new DefaultTransactionAttribute();
        txAttribute.setPropagationBehavior(Propagation.NESTED.value());
        txAttribute.setIsolationLevel(Isolation.REPEATABLE_READ.value());
        txAttribute.setTimeout(10);

        TaskletStep step = stepBuilderFactory.get("compositeWriterStep")
            .chunk(2)
            .reader(reader())
            .processor(new PassThroughItemProcessor())
            .writer(compositeWriters())
            .build();
        //配置事务
        step.setTransactionManager(txManager);
//        step.setTransactionAttribute(txAttribute);
        return step;
    }

    /**
     * 编排 - 定义Job,将Step编排到一起
     */
    @Bean("compositeWriterJob")
    public Job mybatisPagingJob(){
        return jobBuilderFactory.get("compositeWriterJob")
            .start(mybatisPagingStep())
            .build();
    }

}
