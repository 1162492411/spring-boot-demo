package com.zyg.batch.job.compositeWriter;

import com.zyg.batch.entity.User;
import com.zyg.batch.mapper.UserMapper;
import com.zyg.batch.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;

@Slf4j
public class InnerWriter1 implements ItemWriter<User> {
    @Autowired
    private IUserService userService;

    @Override
    public void write(List<? extends User> list) throws Exception {
        userService.saveBatch((Collection<User>) list);
    }

}
