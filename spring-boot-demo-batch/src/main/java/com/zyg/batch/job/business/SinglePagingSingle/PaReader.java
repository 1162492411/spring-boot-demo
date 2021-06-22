package com.zyg.batch.job.business.SinglePagingSingle;

import com.zyg.batch.entity.User;
import com.zyg.batch.job.commonSupport.AbstractPeekPagingListReader;
import com.zyg.batch.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class PaReader extends AbstractPeekPagingListReader<User> {

    @Autowired
    private IUserService userService;

    private ExecutionContext executionContext;

    public void setExecutionContext(ExecutionContext executionContext) {
        this.executionContext = executionContext;
    }

    @Override
    protected List<User> doReadPage() {
//        log.info("在分页reader中读取到上下文中的参数：minId:{},maxId:{}",this.executionContext.get("minId"),this.executionContext.get("maxId"));
        return userService.selectByIdRange(1,100,this.getSkipRows(),this.getPageSize());
    }

//    //fixme : 如果paReader被其他Reader包裹，那么这时候paReader继承 StepListener是无法正常调用beforeStep和afterStep的
//    @Override
//    public void beforeStep(StepExecution stepExecution) {
//        //将StepExecutionContext读取出来以便read时进行修改
//        this.executionContext = stepExecution.getExecutionContext();
//    }
//
//    @Override
//    public ExitStatus afterStep(StepExecution stepExecution) {
//        this.executionContext = null;
//        return stepExecution.getExitStatus();
//    }

}
