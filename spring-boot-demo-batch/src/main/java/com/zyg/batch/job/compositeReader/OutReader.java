package com.zyg.batch.job.compositeReader;

import com.zyg.batch.entity.User;
import com.zyg.batch.mapper.UserMapper;
import lombok.SneakyThrows;
import org.springframework.batch.item.database.AbstractPagingItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class OutReader extends AbstractPagingItemReader<User> {

    @Autowired
    private UserMapper userMapper;
    private Integer leftAge;
    private Integer rightAge;

    public void setLeftAge(Integer leftAge) {
        this.leftAge = leftAge;
    }

    public void setRightAge(Integer rightAge) {
        this.rightAge = rightAge;
    }

    @SneakyThrows
    @Override
    protected void doReadPage(){
        if (this.results == null) {
            this.results = new CopyOnWriteArrayList<User>();
        } else {
            this.results.clear();
        }
        //填充数据到results
        //最终list没有数据时,为其塞入数据
        int skipRows = getPageSize() * (this.getPage() <= 0 ? 0 : this.getPage() - 1);
        List<User> userList = userMapper.selectByAgeRange(this.leftAge, this.rightAge, skipRows, getPageSize());
        if(CollectionUtils.isEmpty(userList)){
            return;
        }
        Integer minId = userList.stream().mapToInt(User::getAge).min().getAsInt();
        Integer maxId = userList.stream().mapToInt(User::getAge).max().getAsInt();
        this.results.addAll(userMapper.selectByIdRange(minId.intValue(),maxId.intValue(),skipRows,getPageSize()));
    }

    @Override
    protected void doJumpToPage(int i) {

    }


    @Override
    protected void doOpen() throws Exception {
        logger.info("进入了模拟器的");
        super.doOpen();
    }
}
