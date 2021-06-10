package com.zyg.batch.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zyg.batch.entity.Teacher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TeacherMapper extends BaseMapper<Teacher> {

    List<Teacher> selectByAgeLeft(@Param("ageLeft")Integer ageLeft, @Param("_skiprows")Integer skipRows, @Param("_pagesize")Integer pageSize);

    List<Teacher> selectByAgeRange(@Param("ageLeft")Integer ageLeft,@Param("ageRight")Integer ageRight,
                                @Param("_skiprows")Integer skipRows,@Param("_pagesize")Integer pageSize);

    List<Teacher> selectByIdRange(@Param("idLeft")Integer ageLeft,@Param("idRight")Integer ageRight,
                               @Param("_skiprows")Integer skipRows,@Param("_pagesize")Integer pageSize);
}
