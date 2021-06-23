package com.zyg.batch.job.onceStepNotSucessDemo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zyg.batch.job.commonSupport.AbstractValidateTasklet;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BusinessTasklet extends AbstractValidateTasklet {

    @Override
    protected boolean adjustExecute() {
        try {
            log.info("获取jobParameters:{}",new ObjectMapper().writeValueAsString(getJobParameters()));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        getSelfJobExecution().put("JobValidateResultFlag","BusinessTaskletResult");
        getSelfStepExecution().put("StepValidateResultFlag","BusinessTaskletResult");
        return true;
    }

}
