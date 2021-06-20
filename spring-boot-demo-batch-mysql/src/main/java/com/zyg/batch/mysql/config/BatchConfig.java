package com.zyg.batch.mysql.config;

import com.zyg.batch.mysql.plugin.ShadowStatementPlugin;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class BatchConfig extends DefaultBatchConfigurer {
//    @Qualifier("dataSource")
    @Autowired
    private DataSource dataSource;

    //将spring batch 的记录存取在主数据源中
//    @Override
//    protected JobRepository createJobRepository() throws Exception {
//        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
//        factory.setDataSource(dataSource);
//        factory.setTransactionManager(this.getTransactionManager());
//        factory.afterPropertiesSet();
//        return factory.getObject();
//    }

//    @Bean
//    public ShadowStatementPlugin shadowStatementPlugin(){
//        return new ShadowStatementPlugin();
//    }



}
