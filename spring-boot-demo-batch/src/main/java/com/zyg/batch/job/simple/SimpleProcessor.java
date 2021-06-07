package com.zyg.batch.job.simple;

import com.zyg.batch.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

/**
 * @author zyg
 */
@Slf4j
public class SimpleProcessor<T,K> implements ItemProcessor<User,Integer> {

    @Override
    public Integer process(User user) throws Exception {
        log.info("正在处理对象:{}",user);
        return user.getId();
    }
}
