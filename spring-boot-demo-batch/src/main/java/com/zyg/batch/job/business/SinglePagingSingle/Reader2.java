package com.zyg.batch.job.business.SinglePagingSingle;

import com.zyg.batch.entity.User;
import com.zyg.batch.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class Reader2 implements ItemReader<List<User>> {

    @Autowired
    private IUserService userService;

    private ExecutionContext executionContext;

    public void setExecutionContext(ExecutionContext executionContext) {
        this.executionContext = executionContext;
    }

    @Override
    public List<User> read() {
        log.info("在分页reader中读取到上下文中的参数：minId:{},maxId:{},loopCount:{}",
            this.executionContext.get("minId"),
            this.executionContext.get("maxId"),
            this.executionContext.get("loopCount")
        );
        return userService.selectByIdRange(1,100,2,5);
    }

}
