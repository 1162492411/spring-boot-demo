package com.zyg.batch.job.compositeReader;

import com.zyg.batch.entity.User;
import com.zyg.batch.job.simple.SimpleProcessor;
import com.zyg.batch.job.simple.SimpleWriter;
import lombok.extern.slf4j.Slf4j;
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

/**
 * @author zyg
 */
@Configuration("compositeReaderDemo-JobConfig")
@Slf4j
public class CompositeReaderJobConfig {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @StepScope
    @Bean("compositeReaderDemo-OutReader")
    public ItemReader<User> compositeReaderOutReader(@Value("#{jobParameters[leftAge]}")Long leftAge,@Value("#{jobParameters[rightAge]}")Long rightAge){
        OutReader outReader = new OutReader();
        outReader.setLeftAge(leftAge == null ? 0 : leftAge.intValue());
        outReader.setRightAge(rightAge == null ? 0 : rightAge.intValue());
        outReader.setPageSize(4);
        return outReader;
    }

    /**
     * 编排 - 定义Step,将ItemReader、ItemProcess、ItemWriter编排到一起
     */
    @Bean("compositeReaderDemo-Step")
    public Step mybatisPagingStep(){
        return  stepBuilderFactory.get("compositeReaderDemo-Step")
            .chunk(2)
            .reader(compositeReaderOutReader(null,null))
            .processor(new SimpleProcessor())
            .writer(new SimpleWriter<>())
            .stream(new OutReader())
            .build();
    }

    /**
     * 编排 - 定义Job,将Step编排到一起
     */
    @Bean("compositeReaderDemo-Job")
    public Job mybatisPagingJob(){
        return jobBuilderFactory.get("compositeReaderDemo-Job")
            .start(mybatisPagingStep())
            .build();
    }

}
