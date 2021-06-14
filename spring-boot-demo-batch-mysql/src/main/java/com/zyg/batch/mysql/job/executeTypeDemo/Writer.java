package com.zyg.batch.mysql.job.executeTypeDemo;
import com.zyg.batch.mysql.entity.Teacher;
import com.zyg.batch.mysql.service.ITeacherService;
import com.zyg.batch.mysql.util.SpringBeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;

@Slf4j
public class Writer implements ItemWriter<Teacher> {
    @Autowired
    private ITeacherService teacherService;

    @Override
    public void write(List<? extends Teacher> list) throws Exception {
        log.info("进入了writer");
        if(CollectionUtils.isEmpty(list)){
            return;
        }
        teacherService.saveBatchList((List<Teacher>) list);
        log.info("此时数量是" + teacherService.count());
//        for (Teacher teacher : list) {
//            log.info("模拟写入");
//            teacherService.save(teacher);
//        }
//        log.info("此时teacher表数据有{}条",teacherService.count());
    }
}
