package com.zyg.batch.job.simple;

import com.zyg.batch.entity.User;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("simpleReader")
public class SimpleReader<T> extends ListItemReader<User> {

    private static List<User> userList = new ArrayList<>();


    static {
        userList.add(com.zyg.batch.entity.User.builder().id(1).build());
        userList.add(com.zyg.batch.entity.User.builder().id(2).build());
        userList.add(com.zyg.batch.entity.User.builder().id(3).build());
        userList.add(com.zyg.batch.entity.User.builder().id(4).build());
        userList.add(com.zyg.batch.entity.User.builder().id(5).build());
    }

    public SimpleReader() {
        super(userList);
    }

}
