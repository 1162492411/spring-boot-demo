package com.zyg.batch.mysql.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zyg.batch.mysql.entity.Teacher;

import java.util.List;

public interface ITeacherService extends IService<Teacher> {

    void saveBatchList(List<Teacher> teacherList) throws Exception;

}
