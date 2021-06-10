package com.zyg.batch.job.compositeWriter;

import com.zyg.batch.entity.Teacher;
import com.zyg.batch.service.ITeacherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;
@Slf4j
public class InnerWriter2 implements ItemWriter<Teacher> {

    @Autowired
    private ITeacherService teacherService;

    @Override
    public void write(List<? extends Teacher> list) throws Exception {
        log.info("此时user表的数据条数为:{}",teacherService.count());
        teacherService.saveBatch((Collection<Teacher>) list);
        log.info("此时user表的数据条数为:{}",teacherService.count());
    }
}
