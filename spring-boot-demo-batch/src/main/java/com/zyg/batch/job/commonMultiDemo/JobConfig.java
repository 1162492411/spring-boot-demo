package com.zyg.batch.job.commonMultiDemo;


import com.zyg.batch.entity.Teacher;
import com.zyg.batch.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zyg
 */
@Configuration("commonMultiDemo-JobConfig")
@Slf4j
public class JobConfig {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @StepScope
    @Bean("commonMultiDemo-Reader")
    public ItemReader<User> reader(@Value("#{jobParameters[leftId]}")Long leftId,@Value("#{jobParameters[rightId]}")Long rightId){
        BusinessReader reader = new BusinessReader();
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("leftId",leftId == null ? 0 : leftId.intValue());
        paramMap.put("rightId",rightId == null ? 0 : rightId.intValue());
        reader.setParameterValues(paramMap);
        reader.setPageSize(4);
        return reader;
    }

    @Bean("commonMultiDemo-Writer")
    public ItemWriter<Teacher> writer(){
        return new BusinessWriter();
    }

    /**
     * 编排 - 定义Step,将ItemReader、ItemProcess、ItemWriter编排到一起
     */
    @Bean("commonMultiDemo-Step")
    public Step mybatisPagingStep(){
        return  stepBuilderFactory.get("commonMultiDemo-Step")
            .chunk(1)
            .reader(reader(null,null))
            .processor(new Processor())
            .writer(writer())
            .build();
    }

    /**
     * 编排 - 定义Job,将Step编排到一起
     */
    @Bean("commonMultiDemo-Job")
    public Job mybatisPagingJob(){
        return jobBuilderFactory.get("commonMultiDemo-Job")
            .start(mybatisPagingStep())
            .build();
    }

}

