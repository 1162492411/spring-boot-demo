package com.zyg.batch.job.commonSupport;

import org.springframework.batch.item.ItemWriter;

import java.util.List;

/**
 * @author zyg
 */
public abstract class AbstractListItemWriter<K> implements ItemWriter<List<K>>{

    @Override
    public void write(List<? extends List<K>> items) {
        if(items.isEmpty()){
            return;
        }
        for (List<K> item : items) {
            doWrite(item);
        }
    }

    /**
     * 扩展点 - 留给子类实现具体的业务写入逻辑
     * @param item
     */
    abstract protected void  doWrite(List<? extends K> item);

}
