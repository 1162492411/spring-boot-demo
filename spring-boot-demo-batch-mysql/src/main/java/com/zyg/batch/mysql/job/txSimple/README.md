# MySQL事务示例

## Job级别事务研究
Job级别与事务相关的子流程如下
1. 读取上次Job上下文([org.springframework.batch.core.repository.support.SimpleJobRepository.getLastJobExecution] : PROPAGATION_REQUIRES_NEW,ISOLATION_SERIALIZABLE)
2. 创建Job上下文([org.springframework.batch.core.repository.support.SimpleJobRepository.createJobExecution] : PROPAGATION_REQUIRES_NEW,ISOLATION_SERIALIZABLE)
3.通过JobRepository更新([org.springframework.batch.core.repository.support.SimpleJobRepository.update]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT)
4. 循环各Step的相关子流程


## Step级别事务研究(目前是单step,可能10～13是Job级别的)
Step级别与事务相关的子流程为
1.获取上次Step上下文([org.springframework.batch.core.repository.support.SimpleJobRepository.getLastStepExecution]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT)
2.获取上次Step执行次数([org.springframework.batch.core.repository.support.SimpleJobRepository.getStepExecutionCount]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT)
3.存储此次Step运行时的信息([org.springframework.batch.core.repository.support.SimpleJobRepository.add]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT)
4. 开始执行step的execute逻辑(此项无关)
5.通过JobRepository更新Job信息([org.springframework.batch.core.repository.support.SimpleJobRepository.update]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT)
6.更新Job上下文信息([org.springframework.batch.core.repository.support.SimpleJobRepository.updateExecutionContext]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT)
循环执行子流程
7.执行reader组件 --> 如果在构造Step时配置了Step级别的事务参数，那么按照该事务参数,执行read
8.执行processor -->(此处未实践，暂不清楚)
9.执行writer组件 --> 如果在构造Step时配置了Step级别的事务参数，那么按照该事务参数,执行read;否则按照writer中的各方法中的事务配置(也就是说step的事务参数配置会覆盖writer的事务参数配置)
循环结束
10.更新Job上下文信息([org.springframework.batch.core.repository.support.SimpleJobRepository.updateExecutionContext]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT)
11.通过JobRepository更新Job信息([org.springframework.batch.core.repository.support.SimpleJobRepository.update]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT)
12.又更新一次Job上下文信息([org.springframework.batch.core.repository.support.SimpleJobRepository.updateExecutionContext]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT)
13.更新Job信息([org.springframework.batch.core.repository.support.SimpleJobRepository.update]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT)

## reader级别事务研究
根据目前的实验结果来看,reader前会开启一个事务,最早到第一个writer时进行提交
目前实验出确实存在事务，但是有问题。
1.在一个step中，读取是batch的话(采用mybatis分页读取插件Mapper方式)，那么writer中也只能是batch操作，fori writer这种也不行,query也不行

## writer级别事务研究

writer每次执行时都会按照生效的事务参数进行一次事务(如果配置step级别事务参数,那么会覆盖writer内方法上的事务参数)

每个小writer内，如果配置的事务参数生效了，并且配置了rollBackException,那么小writer的回滚不影响其他小writer
