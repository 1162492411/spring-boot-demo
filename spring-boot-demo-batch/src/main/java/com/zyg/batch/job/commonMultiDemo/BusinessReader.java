package com.zyg.batch.job.commonMultiDemo;

import com.zyg.batch.entity.User;
import com.zyg.batch.job.commonSupport.AbstractListItemReader;
import com.zyg.batch.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class BusinessReader<K> extends AbstractListItemReader<User> {

    @Autowired
    private IUserService userService;

    @Override
    protected List<User> doReadPage() {
        int idLeft = (int) getParameterValues().get("leftId");
        int idRight = (int) getParameterValues().get("rightId");
        return userService.selectByIdRange(idLeft,idRight,getSkipRows(),getPageSize());
    }

}
