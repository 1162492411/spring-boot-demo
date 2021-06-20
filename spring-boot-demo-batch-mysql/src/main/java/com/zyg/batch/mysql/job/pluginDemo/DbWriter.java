package com.zyg.batch.mysql.job.pluginDemo;

import com.zyg.batch.mysql.entity.User;
import com.zyg.batch.mysql.mapper.TeacherMapper;
import com.zyg.batch.mysql.plugin.ShadowThreadLocalParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class DbWriter implements ItemWriter<List<User>> {
    @Autowired
    private TeacherMapper teacherMapper;

    @Override
    public void write(List<? extends List<User>> items) throws Exception {
        log.info("进入了writer");
            teacherMapper.insertFromUser();
        ShadowThreadLocalParams.remove();
    }
}
