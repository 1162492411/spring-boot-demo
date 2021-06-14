package com.zyg.batch.mysql.job.txSimple;

import com.zyg.batch.mysql.entity.Teacher;
import com.zyg.batch.mysql.entity.User;
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
import org.springframework.batch.item.function.FunctionItemProcessor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import java.util.HashMap;
import java.util.Map;


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
    @StepScope
    @Bean("txSimpleDemo-Reader")
    public ItemReader<User> reader(@Value("#{jobParameters[leftAge]}")Long left, @Value("#{jobParameters[rightAge]}")Long right){
        MyBatisPagingItemReader<User> reader = new MyBatisPagingItemReader<>();
        reader.setSqlSessionFactory(sqlSessionFactory);
        reader.setQueryId("com.zyg.batch.mysql.mapper.UserMapper.selectByAgeRange");
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("ageLeft",left == null ? 0 : left.intValue());
        paramMap.put("ageRight",right == null ? 0 : right.intValue());
        reader.setParameterValues(paramMap);
        reader.setName("txSimpleDemo-reader");
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
        txAttribute.setPropagationBehavior(Propagation.REQUIRES_NEW.value());
        txAttribute.setIsolationLevel(Isolation.DEFAULT.value());
        //这个配置是生效的，我们可以在这里配置为2s超时,在最外层Writer中直接休眠超出2s，可以得出以下报错
        // "Transaction timed out: deadline was Mon Jun 14 11:27:11 CST 2021"
        txAttribute.setTimeout(20);

        TaskletStep step = stepBuilderFactory.get("txSimpleDemo-Step")
            .chunk(2)
            .reader(reader(null,null))
            .processor(processor())
            .writer(writer())
//            .writer(new EmptyWriter())
            .build();
        //配置事务
        step.setTransactionManager(txManager);
        //在这里配置的事务参数会覆盖到reader和writer的每一次事务中
        step.setTransactionAttribute(txAttribute);
        step.setName("txSimpleDemo-step");
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
