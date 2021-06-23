# SimpleJob中任一Step的BatchStatus不成功则整个Job终止

## 背景
通常情况下我们会存在一些任务参数校验的业务场景，这些校验包含两部分: 值校验及业务校验。值校验的逻辑比较简单，通常情况下如果任务参数包装在JobParameters中，我们可以通过对JobParameters进行ParameterValidation满足我们的业务诉求，但是业务校验则需要手动在reader/listener中进行校验，如果业务校验不通过那么我们期望整个Job停止运行。而实际编码中通常会存在多个step，因此我们希望能够找到一种方式，使得step中业务校验不通过时整个job停止运行

## Job级别切入点寻找
通常我们这样配置Job
```java
class JobConfig{
    /**
     * 编排 - 定义Job,将Step编排到一起
      */
    @Bean("onceStepNotSuccessDemo-Job")
    public Job mybatisPagingJob(){
        return jobBuilderFactory.get("onceStepNotSuccessDemo-Job")
            .start(validateStep())
            .next(step2())
            .build();
    }
}
```
这种方式实际上是借助JobBuilder来快速构建Job。JobBuilder中存在两类具体实现类: SimpleJobBuilder与JobFlowBuilder。SimpleJobBuilder构造的Job为SimpleJob，JobFlowBuilder构造的Job为SimpleFlow。
### SimpleJob切入点寻找
我们先看SimpleJob。该Job在运行时入口为doExecute()方法，该方法代码如下
```java
class SimpleJob {
@Override
	protected void doExecute(JobExecution execution) throws JobInterruptedException, JobRestartException,
	StartLimitExceededException {

		StepExecution stepExecution = null;
		for (Step step : steps) {
			stepExecution = handleStep(step, execution);
			if (stepExecution.getStatus() != BatchStatus.COMPLETED) {
				//
				// Terminate the job if a step fails
				//
				break;
			}
		}

		//
		// Update the job status to be the same as the last step
		//
		if (stepExecution != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Upgrading JobExecution status: " + stepExecution);
			}
			execution.upgradeStatus(stepExecution.getStatus());
			execution.setExitStatus(stepExecution.getExitStatus());
		}
	}
}
```
可以看到，如果某个step的状态不是完成，那么就会导致整个job提前终止。因此我们只要想办法将业务校验逻辑封装在step中，业务校验不通过时将step的batchStatus置为成功以外的状态，就能满足我们的业务诉求。

### SimpleFlow切入点寻找
SimpleFlow运行时的入口也是doExecute(),通过层层方法调用，最终我们找到这样一段核心代码
```java
class FlowJob{

	@Override
	public FlowExecution resume(String stateName, FlowExecutor executor) throws FlowExecutionException {
		FlowExecutionStatus status = FlowExecutionStatus.UNKNOWN;
		State state = stateMap.get(stateName);
		StepExecution stepExecution = null;
		// Terminate if there are no more states
        //切入点1
		while (isFlowContinued(state, status, stepExecution)) {
			stateName = state.getName();
			try {
				status = state.handle(executor);
				stepExecution = executor.getStepExecution();
			}
			catch (FlowExecutionException e) {
                //切入点2
				executor.close(new FlowExecution(stateName, status));
				throw e;
			}
			catch (Exception e) {
                //切入点3
				executor.close(new FlowExecution(stateName, status));
				throw new FlowExecutionException(String.format("Ended flow=%s at state=%s with exception", name,
																	  stateName), e);
			}
			state = nextState(stateName, status, stepExecution);
		}
		FlowExecution result = new FlowExecution(stateName, status);
		executor.close(result);
		return result;
	}

	protected boolean isFlowContinued(State state, FlowExecutionStatus status, StepExecution stepExecution) {
		boolean continued = true;

		continued = state != null && status!=FlowExecutionStatus.STOPPED;

		if(stepExecution != null) {
			Boolean reRun = (Boolean) stepExecution.getExecutionContext().get("batch.restart");
			Boolean executed = (Boolean) stepExecution.getExecutionContext().get("batch.executed");

			if((executed == null || !executed) && reRun != null && reRun && status == FlowExecutionStatus.STOPPED && !state.getName().endsWith(stepExecution.getStepName()) ) {
				continued = true;
			}
		}

		return continued;
	}

}
```
因此在这种方式下，可以考虑通过这几个切入点来满足我们的业务诉求

## Step级别切入点寻找
在SimpleJob/SimpleFlow的核心代码中,在执行step时,最终都是通过this.stepHandler.handleStep(step, execution)来委托执行Step,该方法最终会调用AbstractStep.execute()方法,AbstractStep是所有Step的父类，在该类中,存在这样一处代码
```java
class 该方法最终会调用AbstractStep{
  public final void execute(StepExecution stepExecution) throws JobInterruptedException, UnexpectedJobExecutionException {
      if (stepExecution.isTerminateOnly()) {
        throw new JobInterruptedException("JobExecution interrupted.");
      }
  }
}
```
因此，我们也可以在Step中通过修改terminateOnly字段的值来满足我们的业务诉求。注意 ：这种方式下,如果在Step中间部分修改了该字段的值,仍然会继续执行该Step的代码直到该Step结束，因此如果通过该切入点，需要各位开发根据实际的业务场景来判断是否需要提前结束该Step中的后半部分代码

## 切入点示例
### SimpleJob切入点示例1
ReaderOne中，
1）将校验结果放在成员变量canExecute中；
2）继承ItemReader，在read方法中实现业务校验逻辑，校验结果记录在canExecute变量；
3）继承StepExecutionListener，在afterStep中根据canExecute的值修改StepExecution的BatchStatus为成功以外的其他值

这样，在构造Job时，我们只需要将第一个Step指定为ReaderOne，即可满足业务诉求

### SimpleJob切入点示例2
按照示例1的思路,直接抽取一个父类做一些公共逻辑,这样开发人员只需要继承该父类即可
详见AbstractValidateTasklet

### SimpleFlow切入点示例
略,遇到业务场景时再行实验

### Step的terminateOnly切入点示例
略,遇到业务场景时再行实验
