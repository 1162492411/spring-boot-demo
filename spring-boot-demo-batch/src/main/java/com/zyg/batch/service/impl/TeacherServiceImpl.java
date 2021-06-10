package com.zyg.batch.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zyg.batch.entity.Teacher;
import com.zyg.batch.mapper.TeacherMapper;
import com.zyg.batch.mapper.UserMapper;
import com.zyg.batch.service.ITeacherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zyg
 */
@Service
@Slf4j
public class TeacherServiceImpl  extends ServiceImpl<TeacherMapper, Teacher> implements ITeacherService {
    @Autowired
    private TeacherMapper teacherMapper;
}
