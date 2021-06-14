package com.zyg.batch.mysql;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author zyg
 */
@SpringBootApplication
@EnableBatchProcessing
@EnableTransactionManagement(proxyTargetClass = true)
@MapperScan("com.zyg.batch.mysql.mapper")
public class BatchMysqlApplication {

    public static void main(String[] args) {
        SpringApplication.run(BatchMysqlApplication.class, args);
    }

}
