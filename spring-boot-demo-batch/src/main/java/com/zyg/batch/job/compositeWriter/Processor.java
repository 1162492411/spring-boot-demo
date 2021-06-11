package com.zyg.batch.job.compositeWriter;

import com.zyg.batch.entity.User;
import org.springframework.batch.item.ItemProcessor;

public class Processor implements ItemProcessor<User,User> {

    @Override
    public User process(User origin) throws Exception {
        if(origin == null) {
            return null;
        }
        origin.setId(100 + origin.getId());
        return origin;
    }

}
