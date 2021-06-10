package com.zyg.batch.job.multiRead;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zyg.batch.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

@Slf4j
public class AggregateWriter implements ItemWriter<List<String>> {
    private int loopCount = 0;
    @Override
    public void write(List<? extends List<String>> list) throws Exception {
        loopCount++;
        log.info("{}次writer : 接收到{}条数据,具体内容为:{}",loopCount,list.size(),new ObjectMapper().writeValueAsString(list));
        if(loopCount <= 3){
            log.info("{}次writer : 模拟抛出异常",loopCount);
            throw new BusinessException();
        }
        list.forEach(innerList -> log.info("{}次writer : 模拟读取单条数据：{}",loopCount,innerList));
        log.info("{}次writer : writer end \n",loopCount);
    }
}
