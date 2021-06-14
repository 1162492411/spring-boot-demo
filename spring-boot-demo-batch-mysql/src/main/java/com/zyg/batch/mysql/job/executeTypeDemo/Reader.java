package com.zyg.batch.mysql.job.executeTypeDemo;

import com.zyg.batch.mysql.entity.User;
import com.zyg.batch.mysql.service.IUserService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
public class Reader implements ItemReader<User> , ItemReadListener<User> {
    @Autowired
    private IUserService userService;

    private List<User> list;

    @Override
    public User read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if(list == null){
            list = userService.selectByIdRange(10,16,0,1);
        }
        if(CollectionUtils.isEmpty(list)){
            return null;
        }else {
            return list.remove(0);
        }
    }

    @SneakyThrows
    @Override
    public void beforeRead() {
        log.info("即将进入reader");
        Thread.sleep(3000);
    }

    @SneakyThrows
    @Override
    public void afterRead(User item) {
        log.info("reader结束了");
        Thread.sleep(3000);
    }

    @SneakyThrows
    @Override
    public void onReadError(Exception ex) {
        log.info("reader出错了");
        Thread.sleep(3000);
    }
}
