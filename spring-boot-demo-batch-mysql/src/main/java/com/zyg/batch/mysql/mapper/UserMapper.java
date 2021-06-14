package com.zyg.batch.mysql.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zyg.batch.mysql.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zyg
 */
public interface UserMapper extends BaseMapper<User> {

    List<User> selectByAgeLeft(@Param("ageLeft")Integer ageLeft,@Param("_skiprows")Integer skipRows,@Param("_pagesize")Integer pageSize);

    List<User> selectByAgeRange(@Param("ageLeft")Integer ageLeft,@Param("ageRight")Integer ageRight,
                                @Param("_skiprows")Integer skipRows,@Param("_pagesize")Integer pageSize);

    List<User> selectByIdRange(@Param("idLeft")Integer ageLeft,@Param("idRight")Integer ageRight,
                                @Param("_skiprows")Integer skipRows,@Param("_pagesize")Integer pageSize);

}
