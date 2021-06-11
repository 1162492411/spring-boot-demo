package com.zyg.batch.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zyg.batch.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IUserService extends IService<User> {


    List<User> selectByIdRange(Integer idLeft, Integer idRight, Integer skipRows, Integer pageSize);
}
