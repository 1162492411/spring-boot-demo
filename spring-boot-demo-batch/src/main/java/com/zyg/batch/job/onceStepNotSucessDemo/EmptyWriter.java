package com.zyg.batch.job.onceStepNotSucessDemo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

@Slf4j
public class EmptyWriter implements ItemWriter {
    @Override
    public void write(List list) throws Exception {
        log.info("write : do nothing");
    }
}
