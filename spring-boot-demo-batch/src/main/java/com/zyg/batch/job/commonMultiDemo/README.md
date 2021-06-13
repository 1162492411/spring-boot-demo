# 自定义List全套处理
## 背景
通常情况下，我们的业务场景是分页无限循环以下逻辑
1. 按页读取原始数据
2. 对原始数据进行加工,eg. 从其他表或者其他服务的接口获取辅助数据后，与原始数据进行逻辑操作(填充进原始数据或者与原始数据求交集等)后产生最终数据
3. 将最终数据存储多张表/发送到其他服务
乍看起来Batch中的chunkStep非常适合这种场景,我们将1、2两步的逻辑写在组合Reader或者reader+processor中来准备最终数据,待最终数据每达到chunk条后执行writer逻辑。例如简单的分页读取我们可以使用MyBatisPagingItemReader，复杂的读取我们可以继承MyBatisPagingItemReader的父类AbstractPagingItemReader来进行分页的pageIndex的自增。
但是，这其中存在以下问题
* MyBatisPagingItemReader只能调用Mapper.xml中的方法,并且这种分页方式要求xml中的方法中分页参数名为_skipRows、_pageSize,在已有成熟业务的基础上再接入该插件需要额外修改之前的SQL，并且这种方式应对不了组合Reader,并且这样子不是调用Service层面的方法，若已有的业务系统需要对Service层统一执行一些逻辑或者采用了MyBatisPlus类似的动态生成XML的技术而不存在Mapper.xml，还需要额外考虑调用Mapper的这种入口
* 若继承AbstractPagingItemReader来实现组合Reader，将最终数据添加到this.results中。AbstractPagingItemReader的代码写的也不是特别健壮，子类拥有了this.results的操作权限，可以直接将其指向空,return next < this.results.size() ? this.results.get(next) : null 就存在抛出空指针的隐患。并且这种情况下，_skipRows的值需要开发人员手动添加一行代码 this.skipRows = this.getPage() * this.getPageSize()
鉴于此，开发一套reader、processor、writer，使得数据处理流程为 : 读取一页数据 - process(List) - write，开发人员无需关注List<T>,无需关注——skipRows的计算逻辑，无需调用及改写Mapper.xml

## 组件
com.zyg.batch.job.commonSupport.AbstractListItemReader<K>
com.zyg.batch.job.commonSupport.AbstractListItemProcessor<J,K>
com.zyg.batch.job.commonSupport.AbstractListItemWriter<K>

## 使用
开发人员只需要继承这几个类，就可以直接将已有的业务读取逻辑放在reader中，简单的转换逻辑放在process中，写入逻辑放在writer中，Step的chunk配置为1即可
### 读取逻辑reader
```java
public class BusinessReader<K> extends AbstractListItemReader<User> {

    @Autowired
    private IUserService userService;

    //继承该方法,实现业务逻辑
    @Override
    protected List<User> doReadPage() {
        int idLeft = (int) getParameterValues().get("leftId");
        int idRight = (int) getParameterValues().get("rightId");
        return userService.selectByIdRange(idLeft,idRight,getSkipRows(),getPageSize());
    }

}
```

### 转换逻辑processor
若无需转换，直接使用com.zyg.batch.job.commonSupport.ListPassThroughItemProcessor即可
若需要转换,继承AbstractListItemProcessor，实现doProcess方法
```java
public class BusinessProcessor<T,K> extends AbstractListItemProcessor<User, Teacher> {
   //继承该方法,实现业务逻辑
    @Override
    protected Teacher doProcess(User currentItem) {
        Teacher teacher = new Teacher();
        BeanUtils.copyProperties(currentItem,teacher);
        return teacher;
    }

}
```
### 写入逻辑writer
```java
public class BusinessWriter<K> extends AbstractListItemWriter<Teacher> {
    @Autowired
    private ITeacherService teacherService;
    //继承该方法,实现业务逻辑
    @Override
    protected void doWrite(List<? extends Teacher> item) {
        teacherService.saveBatch((Collection<Teacher>) item);
    }
}
```
### 配置Job
```java
@Configuration("commonMultiDemo-JobConfig")
@Slf4j
public class JobConfig {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @StepScope
    @Bean("commonMultiDemo-Reader")
    public ItemReader<User> reader(@Value("#{jobParameters[leftId]}")Long leftId,@Value("#{jobParameters[rightId]}")Long rightId){
        BusinessReader reader = new BusinessReader();
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("leftId",leftId == null ? 0 : leftId.intValue());
        paramMap.put("rightId",rightId == null ? 0 : rightId.intValue());
        reader.setParameterValues(paramMap);
        reader.setPageSize(4);
        return reader;
    }

    @Bean("commonMultiDemo-Writer")
    public ItemWriter<Teacher> writer(){
        return new BusinessWriter();
    }

    /**
     * 编排 - 定义Step,将ItemReader、ItemProcess、ItemWriter编排到一起
     */
    @Bean("commonMultiDemo-Step")
    public Step mybatisPagingStep(){
        return  stepBuilderFactory.get("commonMultiDemo-Step")
            .chunk(1)
            .reader(reader(null,null))
            .processor(new Processor())
            .writer(writer())
            .build();
    }

    /**
     * 编排 - 定义Job,将Step编排到一起
     */
    @Bean("commonMultiDemo-Job")
    public Job mybatisPagingJob(){
        return jobBuilderFactory.get("commonMultiDemo-Job")
            .start(mybatisPagingStep())
            .build();
    }
}
```

### 运行Job
```java
class XXController {
        Job job = SpringBeanUtil.getBean("commonMultiDemo-Job",Job.class);
        JobParameters jobParameters = new JobParametersBuilder()
            .addLong("leftId", 11L)
            .addLong("rightId", 17L)
            .toJobParameters();
        JobExecution jobExecution = jobLauncher.run(job, jobParameters);
}
```
