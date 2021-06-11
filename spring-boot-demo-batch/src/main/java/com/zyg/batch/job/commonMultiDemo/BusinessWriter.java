package com.zyg.batch.job.commonMultiDemo;

import com.zyg.batch.entity.Teacher;
import com.zyg.batch.job.commonSupport.AbstractListItemWriter;
import com.zyg.batch.service.ITeacherService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;

/**
 * @author zyg
 */
public class BusinessWriter<K> extends AbstractListItemWriter<Teacher> {
    @Autowired
    private ITeacherService teacherService;

    @Override
    protected void doWrite(List<? extends Teacher> item) {
        teacherService.saveBatch((Collection<Teacher>) item);
    }
}
