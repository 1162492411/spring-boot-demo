package com.zyg.batch.mysql.job.txSimple;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

@Slf4j
public class EmptyWriter<T> implements ItemWriter<T> {
    @Override
    public void write(List<? extends T> list) throws Exception {
        log.info("writer接收参数:{}",new ObjectMapper().writeValueAsString(list));
        Thread.sleep(5000);
    }
}
