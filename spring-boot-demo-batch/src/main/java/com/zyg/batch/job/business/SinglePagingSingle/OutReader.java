package com.zyg.batch.job.business.SinglePagingSingle;

import com.zyg.batch.entity.User;
import com.zyg.batch.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.item.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class OutReader implements ItemReader<List<User>>,StepExecutionListener{
    @Autowired
    private IUserService userService;
//    private PaReader paReader;

    private ExecutionContext executionContext;

    private static int loopCount = 0;

//    public void setPaReader(PaReader paReader) {
//        this.paReader = paReader;
//    }

    public void setExecutionContext(ExecutionContext executionContext) {
        this.executionContext = executionContext;
    }

    @Override
    public List<User> read() throws Exception {
        loopCount++;
        //模仿第一步读取业务，例如从概要表读取一个List或者读取一对边界值
        log.info("模仿第一步业务");
        int minId = userService.selectMinId();
        int maxId = userService.selectMaxId();
        //传递参数给下一步，应该通过上下文传递了
        this.executionContext.put("minId",minId);
        this.executionContext.put("maxId",maxId);
        this.executionContext.put("loopCount",loopCount);
        List<User> userList = new ArrayList<>();
        userList.add(User.builder().id(11).build());
        if(loopCount <= 1){
            return userList;
        }else{
            return null;
        }
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.executionContext = stepExecution.getJobExecution().getExecutionContext();
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return stepExecution.getExitStatus();
    }

}
