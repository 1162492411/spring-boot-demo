package com.zyg.batch.mysql.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zyg.batch.mysql.entity.User;

import java.util.List;

public interface IUserService extends IService<User> {


    List<User> selectByIdRange(Integer idLeft, Integer idRight, Integer skipRows, Integer pageSize);
}
