package com.zyg.batch.mysql.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zyg.batch.mysql.entity.User;
import com.zyg.batch.mysql.mapper.UserMapper;
import com.zyg.batch.mysql.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zyg
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
//    @Autowired
//    private UserMapper userMapper;

    @Override
    public List<User> selectByIdRange(Integer idLeft, Integer idRight, Integer skipRows, Integer pageSize) {
        return this.getBaseMapper().selectByIdRange(idLeft,idRight,skipRows,pageSize);
    }
}
