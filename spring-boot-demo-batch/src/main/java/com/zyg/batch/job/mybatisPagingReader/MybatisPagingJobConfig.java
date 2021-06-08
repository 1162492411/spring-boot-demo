package com.zyg.batch.job.mybatisPagingReader;

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
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


/**
 * @author zyg
 */
@Configuration
@Slf4j
public class MybatisPagingJobConfig {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @StepScope
    @Bean("userAgePagingReader")
    public ItemReader<User> userAgePagingReader(@Value("#{jobParameters[leftAge]}")Long leftAge){
        MyBatisPagingItemReader reader = new MyBatisPagingItemReader();
        reader.setSqlSessionFactory(sqlSessionFactory);
        reader.setQueryId("com.zyg.batch.mapper.UserMapper.selectByAgeLeft");
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("ageLeft",(leftAge == null ? 0 : leftAge.intValue()));
        reader.setParameterValues(paramMap);
        reader.setName("zygUserAgeMybatisPagingReader");
        reader.setPageSize(4);
        return reader;
    }

    /**
     * 编排 - 定义Step,将ItemReader、ItemProcess、ItemWriter编排到一起
     */
    @Bean("userMybatisPagingStep")
    public Step mybatisPagingStep(){
        return  stepBuilderFactory.get("userMybatisPagingStep")
            .chunk(2)
            .reader(userAgePagingReader(null))
            .processor(new SimpleProcessor())
            .writer(new SimpleWriter<>())
            .build();
    }

    /**
     * 编排 - 定义Job,将Step编排到一起
      */
    @Bean("userMybatisPagingJob")
    public Job mybatisPagingJob(){
        return jobBuilderFactory.get("userMybatisPagingJob")
            .start(mybatisPagingStep())
            .build();
    }

}
