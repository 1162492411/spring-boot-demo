package com.zyg.batch.job.multiRead;

import com.zyg.batch.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class AggregateReader implements ItemReader<List<User>> {
    private int count = 0;

    @Override
    public List<User> read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
       if(count >= 15){
           return null;
       }
        List<User> userList = new ArrayList<>();
       userList.add(User.builder().id(++count).build());
       userList.add(User.builder().id(++count).build());
       userList.add(User.builder().id(++count).build());
       userList.add(User.builder().id(++count).build());
       userList.add(User.builder().id(++count).build());
       log.info("{}-->模拟读取数据{}" ,count,userList);
       return userList;
    }
}
