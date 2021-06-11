package com.zyg.batch.job.commonSupport;

import org.springframework.batch.item.ItemProcessor;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractListItemProcessor<J,K> implements ItemProcessor<List<J>,List<K>> {

    protected volatile List<K> results;

    //该方法中不处理任何异常,doProcess中抛出任何异常时都要继续向外抛出
    @Override
    public List<K> process(List<J> itemList) {
        //当process方法返回null表示该次的数据被过滤掉,不会传递给ItemWriter(切忌不要返回空list)
        if(itemList.isEmpty()){
            return null;
        }
        results = new CopyOnWriteArrayList<>();
        for (J currentOriginItem : itemList) {
            K currentDestItem = doProcess(currentOriginItem);
            if (currentDestItem == null) {
                continue;
            }
            results.add(currentDestItem);
        }
        //当process方法返回null表示该次的数据被过滤掉,不会传递给ItemWriter(切忌不要返回空list)
        if(results.isEmpty()){
            return null;
        }
        return results;
    }

    abstract protected K doProcess(J currentItem);

}
