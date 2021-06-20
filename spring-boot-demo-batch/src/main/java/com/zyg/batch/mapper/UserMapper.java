package com.zyg.batch.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zyg.batch.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zyg
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    List<User> selectByBatchPage(@Param("_skiprows")Integer skipRows,@Param("_pagesize")Integer pageSize);

    List<User> selectByAgeLeft(@Param("ageLeft")Integer ageLeft,@Param("_skiprows")Integer skipRows,@Param("_pagesize")Integer pageSize);

    List<User> selectByAgeRange(@Param("ageLeft")Integer ageLeft,@Param("ageRight")Integer ageRight,
                                @Param("_skiprows")Integer skipRows,@Param("_pagesize")Integer pageSize);

    List<User> selectByIdRange(@Param("idLeft")Integer ageLeft,@Param("idRight")Integer ageRight,
                                @Param("_skiprows")Integer skipRows,@Param("_pagesize")Integer pageSize);

    Integer selectMinId();

    Integer selectMaxId();



}
