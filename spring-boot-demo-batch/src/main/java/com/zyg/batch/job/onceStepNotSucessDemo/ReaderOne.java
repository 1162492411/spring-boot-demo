package com.zyg.batch.job.onceStepNotSucessDemo;

import org.springframework.batch.core.*;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

public class ReaderOne implements ItemReader<String>, StepExecutionListener {
    private boolean canExecute = true;
    private int loopCount = 0;

    @Override
    public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        this.canExecute = false;
        loopCount++;
        if(loopCount >= 3){
            return null;
        }
        return "customNotSuccess";
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        canExecute = true;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        if(!canExecute){
            stepExecution.setStatus(BatchStatus.ABANDONED);
            return ExitStatus.FAILED;
        }
        return ExitStatus.COMPLETED;
    }
}
