package com.zyg.batch.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zyg.batch.entity.User;
import com.zyg.batch.mapper.UserMapper;
import com.zyg.batch.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zyg
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> selectByIdRange(Integer idLeft, Integer idRight, Integer skipRows, Integer pageSize) {
        return userMapper.selectByIdRange(idLeft,idRight,skipRows,pageSize);
    }


    @Override
    public Integer selectMinId() {
        return userMapper.selectMinId();
    }

    @Override
    public Integer selectMaxId() {
        return userMapper.selectMaxId();
    }
}
