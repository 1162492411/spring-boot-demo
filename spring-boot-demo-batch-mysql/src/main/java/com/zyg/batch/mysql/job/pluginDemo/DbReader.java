package com.zyg.batch.mysql.job.pluginDemo;

import com.zyg.batch.mysql.batchSupport.AbstractPeekWholeSingleListReader;
import com.zyg.batch.mysql.entity.User;
import com.zyg.batch.mysql.mapper.UserMapper;
import com.zyg.batch.mysql.plugin.ShadowThreadLocalParams;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class DbReader extends AbstractPeekWholeSingleListReader<User> {

    @Autowired
    private UserMapper userMapper;

    @Override
    protected List<User> doReadPage() {
        ShadowThreadLocalParams.setKey("batchTest");
        //测试1 : 简单select
//        return userMapper.selectSimple();
        //测试2 : 嵌套sql
//        return userMapper.selectNested();
        //测试3 : join
//        return userMapper.selectJoin();
        //测试4 : join + 别名
//        return userMapper.selectJoinAlias();
        //测试5 : join + 别名 + dual
//        return userMapper.selectJoinAliasDual();
        //测试6 : join + dual + 别名
//        return userMapper.selectJoinDualAlias();
        //fixme : 测试7 : 多个表 + 索引 --> 当前失败
//        return  userMapper.selectTableIndexAndMultiTable();
        //测试8 : 带有group by、 order by、having
//        return userMapper.selectJoinAliasDualIndex();

//        测试last : 配合测试writer,先返回模拟数据;
        List<User> innerList1 = new ArrayList<>();
        innerList1.add(User.builder().id(101).build());
        innerList1.add(User.builder().id(102).build());
        return innerList1;
    }
}
