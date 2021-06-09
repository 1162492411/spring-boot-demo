package com.zyg.batch.job.multiRead;

import com.zyg.batch.entity.User;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AggregateProcessor<T,K> implements ItemProcessor<List<User>,List<String>> {
    @Override
    public List<String> process(List<User> users) {
        if(CollectionUtils.isEmpty(users)){
            return Collections.emptyList();
        }
        List<String> resultList = new ArrayList<>(users.size());
        users.forEach(e -> resultList.add(e.getId() + ""));
        return resultList;
    }
}
