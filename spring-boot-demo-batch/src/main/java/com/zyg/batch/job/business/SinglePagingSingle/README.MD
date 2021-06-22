# 业务模型 - 顺序逻辑嵌套分页逻辑
## 嵌套业务逻辑
常见的嵌套业务逻辑存在以下几种情况
A1. 1) 读取边界数据 2) 以边界数据为范围,进行"无限分页读取"的逻辑
A2. 1) 读取边界数据 2) 以边界数据为范围,进行"无限分页读取并写入"的逻辑
A3. 1) 读取一次数据 2)读取边界数据/list数据 3) 以边界数据为范围,进行"无限分页读取"的逻辑 4)对分页读取的汇总状态进行记录/通知逻辑
A4. 1) 读取一次数据 2)读取边界数据/list数据 3) 以边界数据为范围,进行"无限分页读取并写入"的逻辑 4)对分页读取的汇总状态进行记录/通知逻辑,如果全成功则成功,如果部分失败则xx
B1. 1) 读取list数据 2) 遍历list中每一个元素,以单个元素为范围进行"无限分页读取的逻辑"
B2. 1) 读取边界数据 2) 遍历list中每一个元素,以单个元素为范围,进行"无限分页读取并写入"的逻辑
B3. 1) 读取一次数据 2)读取边界数据/list数据 3) 遍历list中每一个元素,以单个元素为范围,进行"无限分页读取"的逻辑 4)对分页读取的汇总状态进行记录/通知逻辑
B4. 1) 读取一次数据 2)读取边界数据/list数据 3) 遍历list中每一个元素,以单个元素为范围,进行"无限分页读取并写入"的逻辑 4)对分页读取的汇总状态进行记录/通知逻辑,如果全成功则成功,如果部分失败则xx

    考虑到Batch框架结束运行一个Step的依据是reader读取的数据为空，如果将逻辑1，2组装为一个大Reader,那么会出现逻辑1重复运行多次直到逻辑2
读取数据为空,并且这样无法解决A2、A4等这种在分页读取数据后涉及分页写入的情况，固然,我们可能可以通过Batch框架的一些特性,如isReadQueue,或者代码自行
处理来防止逻辑1被重复执行,但这样一来涉及的Batch功能点更多,并且将流程变为了有状态的流程,那么就需要考虑更多种异常、恢复、回滚等情况,这样开发人员改造成本过高.
    因此,针对A1~B4的业务场景，更合适的方式还是将其拆分到不同的step中,步骤间的参数传递可以借助Batch框架来完成

## 参数传递 - 上下文方式
目前一个Reader获取Step范围的上下文或Job级别的上下文有两种方式
1. 继承StepExecutionListener，在beforeStep()中获取上下文(这种方式下,可以在afterStep()在step结束时销毁成员变量,从而避免被其他Job使用该Reader时读取到本次注入的上下文)
    * 通过stepExecution.getExecutionContext()获取Step范围的上下文
    * 通过stepExecution.getJobExecution().getExecutionContext()获取Job范围的上下文
2. 在通过Spring配置Reader时,可以借助@JobStep或者@StepScope和SPEL来获取上下文,eg
```java
    @Bean("businessSinglePagingSingle-innerReader2")
    //获取Job范围的上下文
    @JobScope
    //在方法名中通过SPEL获获取 --> @Value("#{jobExecution}")获取JobExecution,@Value("#{jobExecution['ExecutionContext']}")获取上下文
    public Reader2 innerReader2(@Value("#{jobExecution}") JobExecution jobExecution){
        Reader2 reader = new Reader2();
        //在reader中通过成员变量接收,(注意,如果在spring中,默认单例,该处获取了上下文,那么其他Job使用到该类时,会读取到该类在这里注入的上下文)
        reader.setExecutionContext(jobExecution.getExecutionContext());
        return reader;
    }
```


