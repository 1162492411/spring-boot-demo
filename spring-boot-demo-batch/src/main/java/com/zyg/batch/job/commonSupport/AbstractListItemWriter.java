package com.zyg.batch.job.commonSupport;

import org.springframework.batch.item.ItemWriter;

import java.util.List;

/**
 * @author zyg
 */
public abstract class AbstractListItemWriter<K> implements ItemWriter<List<K>>{

    @Override
    public void write(List<? extends List<K>> items) {
        beforeWrite();
        if(items.isEmpty()){
            return;
        }
        for (List<K> item : items) {
            doWrite(item);
        }
        afterWrite();
    }

    /**
     * 扩展点 - 留给子类实现具体的业务写入逻辑
     * @param item
     */
    abstract protected void  doWrite(List<? extends K> item);

    /**
     * 扩展点 - 留给子类实现写入前的业务操作
     */
    protected void beforeWrite(){

    }

    /**
     * 扩展点 - 留给子类实现写入后的业务操作
     */
    protected void afterWrite(){

    }

}
