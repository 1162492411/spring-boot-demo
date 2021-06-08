package com.zyg.batch.job.simple;

import com.zyg.batch.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author zyg
 */
@Slf4j
@Component("simpleProcessor")
public class SimpleProcessor<T,K> implements ItemProcessor<User,Integer> {

    @Override
    public Integer process(User user) throws Exception {
        log.info("正在处理对象:{}",user);
        return user.getId();
    }
}
