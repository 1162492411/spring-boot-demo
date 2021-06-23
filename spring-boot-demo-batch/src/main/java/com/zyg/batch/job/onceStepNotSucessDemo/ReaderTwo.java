package com.zyg.batch.job.onceStepNotSucessDemo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

@Slf4j
public class ReaderTwo implements ItemReader , StepExecutionListener {

    private static int loopCount = 0;
    @Override
    public Object read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        loopCount++;
        if(loopCount >= 4){
            return null;
        }
        log.info("run to here");
        return new Object();
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        try {
            log.info("接收到上一个step传递的Job上下文参数:{},step上下文参数:{}",
                new ObjectMapper().writeValueAsString(stepExecution.getJobExecution().getExecutionContext().get("JobValidateResultFlag")),
                new ObjectMapper().writeValueAsString(stepExecution.getExecutionContext().get("StepValidateResultFlag"))
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return null;
    }
}
