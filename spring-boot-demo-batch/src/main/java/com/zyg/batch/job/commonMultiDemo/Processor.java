package com.zyg.batch.job.commonMultiDemo;

import com.zyg.batch.entity.Teacher;
import com.zyg.batch.entity.User;
import com.zyg.batch.job.commonSupport.AbstractListItemProcessor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.BeanUtils;

public class Processor<T,K> extends AbstractListItemProcessor<User, Teacher> {
    @Override
    protected Teacher doProcess(User currentItem) {
        Teacher teacher = new Teacher();
        BeanUtils.copyProperties(currentItem,teacher);
        return teacher;
    }

//    @Override
//    public Teacher process(User item) throws Exception {
//        Teacher teacher = new Teacher();
//        BeanUtils.copyProperties(item,teacher);
//        return teacher;
//    }
}
