package com.zyg.batch.job.commonSupport;

import com.zyg.batch.job.commonSupport.AbstractListItemProcessor;

/**
 * @author zyg
 */
public class ListPassThroughItemProcessor<T> extends AbstractListItemProcessor<T,T> {
    @Override
    protected T doProcess(T currentItem) {
        return currentItem;
    }
}
