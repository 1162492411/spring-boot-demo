# MyBatis分页读取插件示例
本例子展示如何使用MyBatis分页读取插件MyBatisPagingItemReader,使用该插件需要以下步骤
1. 准备XML文件
2. 配置插件的各参数
3. 将插件注册为SpringBean并配置到Job中
4. 运行Job并传递
## 准备XML文件
在UserMapper.xml中添加SQL代码
```xml
  <select id="selectByBatchPage" resultType="com.zyg.batch.entity.User">
    select
    <include refid="Base_Column_List" />
    from user ORDER BY id ASC LIMIT #{_skiprows}, #{_pagesize}
  </select>
```

## 配置插件的各参数
该插件需要配置以下参数
* (必选项)SqlSessionFactory : 作用是产生SqlSession
* (必选项)xml方法中的唯一限定id : 作用是调用该方法进行查询
* (可选项)Map<String, Object> : 如果有分页以外的参数需要传递,配置到该对象中
* (可选项)pageSize : 每页数据量,默认10

## 注册到Spring中及配置到Job中
```java
@Configuration("mybatisPagingDemo-JobConfig")
@Slf4j
public class MybatisPagingJobConfig {
    //注册到Spring
    @StepScope
    @Bean("mybatisPagingDemo-Reader")
    public ItemReader<User> userAgePagingReader(@Value("#{jobParameters[leftAge]}")Long leftAge){
        MyBatisPagingItemReader reader = new MyBatisPagingItemReader();
        reader.setSqlSessionFactory(sqlSessionFactory);
        reader.setQueryId("com.zyg.batch.mapper.UserMapper.selectByAgeLeft");
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("ageLeft",(leftAge == null ? 0 : leftAge.intValue()));
        reader.setParameterValues(paramMap);
        reader.setName("mybatisPagingDemo-Reader");
        reader.setPageSize(4);
        return reader;
    }

   //配置到Step中,再将Step配置到Job中
    /**
     * 编排 - 定义Step,将ItemReader、ItemProcess、ItemWriter编排到一起
     */
    @Bean("mybatisPagingDemo-Step")
    public Step mybatisPagingStep(){
        return  stepBuilderFactory.get("mybatisPagingDemo-Step")
            .chunk(2)
            .reader(userAgePagingReader(null))
            .processor(new SimpleProcessor())
            .writer(new SimpleWriter<>())
            .build();
    }

    /**
     * 编排 - 定义Job,将Step编排到一起
      */
    @Bean("mybatisPagingDemo-Job")
    public Job mybatisPagingJob(){
        return jobBuilderFactory.get("mybatisPagingDemo-Job")
            .start(mybatisPagingStep())
            .build();
    }
}
```

## 运行Job
```java
class XXController{
   JobParameters jobParameters = new JobParametersBuilder().addLong("leftAge", 12L).toJobParameters();
   Job job = SpringBeanUtil.getBean("mybatisPagingDemo-Job",Job.class);
   JobExecution jobExecution = jobLauncher.run(job, jobParameters);
}
```
