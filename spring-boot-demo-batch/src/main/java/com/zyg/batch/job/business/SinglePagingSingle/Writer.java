package com.zyg.batch.job.business.SinglePagingSingle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zyg.batch.entity.User;
import com.zyg.batch.job.commonSupport.AbstractListItemWriter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class Writer extends AbstractListItemWriter<User> {
    @Override
    protected void doWrite(List<? extends User> item) {
        try {
            log.info("writer - 模拟消费:{}",new ObjectMapper().writeValueAsString(item));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
