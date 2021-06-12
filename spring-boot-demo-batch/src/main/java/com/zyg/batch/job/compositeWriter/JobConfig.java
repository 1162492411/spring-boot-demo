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
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.support.PassThroughItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import java.util.HashMap;
import java.util.Map;

@Configuration("compositeWriterDemo-JobConfig")
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

    @StepScope
    @Bean("compositeWriterDemo-Reader")
    public ItemReader<User> reader(@Value("#{jobParameters[leftId]}")Long leftId, @Value("#{jobParameters[rightId]}")Long rightId){
        MyBatisPagingItemReader<User> reader = new MyBatisPagingItemReader<>();
        reader.setSqlSessionFactory(sqlSessionFactory);
        reader.setQueryId("com.zyg.batch.mapper.UserMapper.selectByBatchPage");
        Map<String,Object> params = new HashMap<>();
        params.put("idLeft",leftId);
        params.put("idRight",rightId);
        reader.setParameterValues(params);
        return reader;
    }

    @Bean("compositeWriterDemo-Writer1")
    public ItemWriter writer1(){
        return new InnerWriter1();
    }

    @Bean("compositeWriterDemo-Writer2")
    public InnerWriter2 writer2(){
        return new InnerWriter2();
    }

    @Bean("compositeWriterDemo-OutWriter")
    public ItemWriter<User> compositeWriters(){
        OutWriter<User> outWriter = new OutWriter<>();
        outWriter.setInnerWriter1(writer1());
        outWriter.setInnerWriter2(writer2());
        return outWriter;
    }

    @Bean("compositeWriterDemo-Processor")
    public ItemProcessor processor(){
        return new Processor();
    }

    /**
     * 编排 - 定义Step,将ItemReader、ItemProcess、ItemWriter编排到一起
     */
    @Bean("compositeWriterDemo-Step")
    public Step mybatisPagingStep(){
        DefaultTransactionAttribute txAttribute = new DefaultTransactionAttribute();
        txAttribute.setPropagationBehavior(Propagation.NESTED.value());
        txAttribute.setIsolationLevel(Isolation.REPEATABLE_READ.value());
        txAttribute.setTimeout(10);

        TaskletStep step = stepBuilderFactory.get("compositeWriterDemo-Step")
            .chunk(2)
            .reader(reader(null,null))
            .processor(processor())
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
    @Bean("compositeWriterDemo-Job")
    public Job mybatisPagingJob(){
        return jobBuilderFactory.get("compositeWriterDemo-Job")
            .start(mybatisPagingStep())
            .build();
    }

}
