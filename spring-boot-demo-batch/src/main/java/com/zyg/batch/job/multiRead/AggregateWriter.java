package com.zyg.batch.job.multiRead;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

@Slf4j
public class AggregateWriter implements ItemWriter<List<String>> {
    @Override
    public void write(List<? extends List<String>> list) throws Exception {
        log.info("writer接收到{}条数据",list == null ? 0 : list.size());
        list.forEach(innerList -> log.info("模拟读取单条数据：{}",innerList));
        log.info("writer --> \n");
    }
}
