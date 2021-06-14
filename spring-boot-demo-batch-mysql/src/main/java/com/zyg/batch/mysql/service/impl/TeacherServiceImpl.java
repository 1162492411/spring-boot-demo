package com.zyg.batch.mysql.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zyg.batch.mysql.entity.Teacher;
import com.zyg.batch.mysql.mapper.TeacherMapper;
import com.zyg.batch.mysql.service.ITeacherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author zyg
 */
@Service
@Slf4j
public class TeacherServiceImpl  extends ServiceImpl<TeacherMapper, Teacher> implements ITeacherService {
    private static int loopCount = 0;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
    public void saveBatchList(List<Teacher> teacherList) throws Exception {
        log.info("进入saveBatchList");
        loopCount++;
        Thread.sleep(5000);
        if(loopCount>10){
            throw new Exception("guyide");
        }
        this.saveBatch(teacherList);
    }
}
