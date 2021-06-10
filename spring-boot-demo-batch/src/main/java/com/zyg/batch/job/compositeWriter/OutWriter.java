package com.zyg.batch.job.compositeWriter;

import com.zyg.batch.entity.Teacher;
import com.zyg.batch.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class OutWriter<T> implements ItemWriter<User>{
    private ItemWriter innerWriter1;
    private ItemWriter innerWriter2;


    public void setInnerWriter1(ItemWriter innerWriter1) {
        this.innerWriter1 = innerWriter1;
    }

    public void setInnerWriter2(ItemWriter innerWriter2) {
        this.innerWriter2 = innerWriter2;
    }

    @Override
    public void write(List<? extends User> userList) throws Exception {
        if(CollectionUtils.isEmpty(userList)){
            return;
        }
        innerWriter1.write(userList);
        List<Teacher> teacherList = new ArrayList<>(userList.size());
        for (User user : userList) {
            Teacher teacher = new Teacher();
            BeanUtils.copyProperties(user,teacher);
            teacherList.add(teacher);
        }
        innerWriter2.write(teacherList);
    }
}
