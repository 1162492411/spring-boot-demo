package com.zyg.batch.mysql.job.pluginDemo;

import com.zyg.batch.mysql.entity.Teacher;
import com.zyg.batch.mysql.entity.User;
import com.zyg.batch.mysql.job.txSimple.EmptyWriter;
import com.zyg.batch.mysql.service.IUserService;
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
import org.springframework.batch.item.adapter.ItemReaderAdapter;
import org.springframework.batch.item.function.FunctionItemProcessor;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.item.support.PassThroughItemProcessor;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Configuration("pluginDemo-JobConfig")
@Slf4j
public class JobConfig {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    //测试 : 查询类sql
    @StepScope
    @Bean("pluginDemo-Reader")
    public ItemReader<List<User>> reader(@Value("#{jobParameters[leftAge]}")Long left, @Value("#{jobParameters[rightAge]}")Long right){
        return new DbReader();
    }

    @Bean("pluginDemo-reader")
    public ItemReader<List<User>> mockList(){
        List<List<User>> outList = new ArrayList<>();

        List<User> innerList1 = new ArrayList<>();
        innerList1.add(User.builder().id(101).build());
        innerList1.add(User.builder().id(102).build());
        outList.add(innerList1);

        List<User> innerList2 = new ArrayList<>();
        innerList2.add(User.builder().id(201).build());
        innerList2.add(User.builder().id(202).build());
        outList.add(innerList2);

        return new ListItemReader<>(outList);
    }

    @Bean("pluginDemo-writer")
    public ItemWriter<List<User>> writer(){
        return new DbWriter();
    }

    /**
     * 编排 - 定义Step,将ItemReader、ItemProcess、ItemWriter编排到一起
     */
    @Bean("pluginDemo-Step")
    public Step mybatisPagingStep(){
        TaskletStep step = stepBuilderFactory.get("pluginDemo-Step")
            .<List<User>,List<User>>chunk(2)
            .reader(reader(null,null))
            .processor(new PassThroughItemProcessor<>())
//            .writer(new DbWriter())
            .writer(writer())
            .build();

        step.setName("txSimpleDemo-step");
        return step;
    }

    /**
     * 编排 - 定义Job,将Step编排到一起
     */
    @Bean("pluginDemo-Job")
    public Job mybatisPagingJob(){
        return jobBuilderFactory.get("pluginDemo-Job")
            .start(mybatisPagingStep())
            .build();
    }

}
