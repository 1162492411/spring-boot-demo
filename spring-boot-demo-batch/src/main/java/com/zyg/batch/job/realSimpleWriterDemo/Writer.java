package com.zyg.batch.job.realSimpleWriterDemo;

import com.zyg.batch.entity.Teacher;
import com.zyg.batch.service.ITeacherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;

@Slf4j
public class Writer implements ItemWriter<Teacher> {
    @Autowired
    private ITeacherService teacherService;

    @Override
    public void write(List<? extends Teacher> list) throws Exception {
        log.info("writer接收到参数：{}",list);
        teacherService.saveBatch((Collection<com.zyg.batch.entity.Teacher>) list);
    }
}
