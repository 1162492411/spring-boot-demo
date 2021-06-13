package com.zyg.batch.job.txSimple;

import com.zyg.batch.entity.Teacher;
import com.zyg.batch.entity.User;
import com.zyg.batch.exception.BusinessException;
import com.zyg.batch.job.compositeReader.OutReader;
import com.zyg.batch.job.simple.SimpleProcessor;
import com.zyg.batch.job.simple.SimpleWriter;
import com.zyg.batch.mapper.TeacherMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.function.FunctionItemProcessor;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.support.PassThroughItemProcessor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import java.util.List;
import java.util.function.Function;

@Configuration("txSimpleDemo-JobConfig")
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

    @Bean("txSimpleDemo-Reader")
    public ItemReader<User> reader(){
        MyBatisPagingItemReader<User> reader = new MyBatisPagingItemReader<>();
        reader.setSqlSessionFactory(sqlSessionFactory);
        reader.setQueryId("com.zyg.batch.mapper.UserMapper.selectByBatchPage");
        return reader;
    }

    @Bean("txSimpleDemo-processor")
    public ItemProcessor processor(){
        return new FunctionItemProcessor<>(user -> {
            Teacher teacher = new Teacher();
            BeanUtils.copyProperties(user,teacher);
            return teacher;
        });
    }

    @Bean("txSimpleDemo-Writer")
    public ItemWriter writer(){
        return new Writer();
    }

    /**
     * 编排 - 定义Step,将ItemReader、ItemProcess、ItemWriter编排到一起
     */
    @Bean("txSimpleDemo-Step")
    public Step mybatisPagingStep(){
        DefaultTransactionAttribute txAttribute = new DefaultTransactionAttribute();
        txAttribute.setPropagationBehavior(Propagation.REQUIRED.value());
        txAttribute.setIsolationLevel(Isolation.DEFAULT.value());
        txAttribute.setTimeout(10);

        TaskletStep step = stepBuilderFactory.get("txSimpleDemo-Step")
            .chunk(2)
            .reader(reader())
            .processor(processor())
            .writer(writer())
            .build();
        //配置事务
        step.setTransactionManager(txManager);
        step.setTransactionAttribute(txAttribute);
        return step;
    }

    /**
     * 编排 - 定义Job,将Step编排到一起
     */
    @Bean("txSimpleDemo-Job")
    public Job mybatisPagingJob(){
        return jobBuilderFactory.get("txSimpleDemo-Job")
            .start(mybatisPagingStep())
            .build();
    }

}
