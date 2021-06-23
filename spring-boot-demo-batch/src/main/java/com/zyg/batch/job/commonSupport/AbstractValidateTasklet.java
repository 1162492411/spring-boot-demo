package com.zyg.batch.job.commonSupport;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;


/**
 * 校验Tasklet,子类在adjustExecute()中写入业务校验逻辑,返回是否允许任务继续向后执行
 * @author zyg
 */
@Slf4j
public abstract class AbstractValidateTasklet implements Tasklet, StepExecutionListener {

    private boolean canExecute = true;

    private ExecutionContext selfStepExecution;

    private ExecutionContext selfJobExecution;

    private JobParameters jobParameters;

    public ExecutionContext getSelfStepExecution() {
        return selfStepExecution;
    }

    public ExecutionContext getSelfJobExecution() {
        return selfJobExecution;
    }

    public JobParameters getJobParameters() {
        return jobParameters;
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        log.info("进入校验tasklet");
        try{
            canExecute = adjustExecute();
        }catch (Exception e){
            log.error("准备初始数据时发生异常",e);
            canExecute = false;
        }
        log.info("校验tasklet - 是否允许执行:{}",canExecute);
        return RepeatStatus.FINISHED;
    }

    /**
     * 子类返回是否允许继续向下执行
     * @return
     */
    abstract protected boolean adjustExecute();

    @Override
    public void beforeStep(StepExecution stepExecution) {

        this.selfStepExecution = stepExecution.getExecutionContext();
        this.selfJobExecution = stepExecution.getJobExecution().getExecutionContext();
        this.jobParameters = stepExecution.getJobParameters();
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        this.selfStepExecution = null;
        this.selfJobExecution = null;
        this.jobParameters = null;

        if(!canExecute){
            stepExecution.setStatus(BatchStatus.FAILED);
            return ExitStatus.FAILED;
        }
        return ExitStatus.COMPLETED;
    }

}
