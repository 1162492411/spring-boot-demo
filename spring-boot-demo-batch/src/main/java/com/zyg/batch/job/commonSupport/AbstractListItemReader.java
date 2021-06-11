package com.zyg.batch.job.commonSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author zyg
 */
public abstract class AbstractListItemReader<K> extends AbstractItemCountingItemStreamItemReader<List<K>> implements InitializingBean {
    protected Log logger = LogFactory.getLog(getClass());

    private volatile boolean initialized = false;

    private int pageSize = 10;

    private volatile int page = 0;

    private Map<String, Object> parameterValues;

    private volatile int skipRows = 0;

    protected volatile List<K> results;

    private Object lock = new Object();

    public AbstractListItemReader() {
        setName(ClassUtils.getShortName(AbstractListItemReader.class));
    }

    /**
     * The current page number.
     *
     * @return the current page
     */
    public int getPage() {
        return page;
    }


    public int getSkipRows(){
        return skipRows;
    }

    /**
     * The page size configured for this reader.
     *
     * @return the page size
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * The number of rows to retrieve at a time.
     *
     * @param pageSize the number of rows to fetch per page
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setParameterValues(Map<String, Object> parameterValues) {
        this.parameterValues = parameterValues;
    }

    public Map<String, Object> getParameterValues() {
        return parameterValues;
    }

    /**
     * Check mandatory properties.
     *
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.isTrue(pageSize > 0, "pageSize must be greater than zero");
        //改动点
        Assert.isTrue(pageSize < 5_0000, "pageSize must be less than 50000");
    }

    //改动点
    @Override
    protected List<K> doRead(){
        synchronized (lock) {
            if (logger.isDebugEnabled()) {
                logger.debug("Reading page " + getPage());
            }
            if (this.results == null) {
                this.results = new CopyOnWriteArrayList<>();
            } else {
                this.results.clear();
            }
            this.results.addAll(doReadPage());
            page++;
            skipRows += (results == null ? 0 : results.size());
            if(results == null || results.isEmpty()){
                return null;
            }else{
                return results;
            }
        }
    }

    /**
     * 扩展方法,子类在该方法中将业务数据返回
     */
    abstract protected List<K> doReadPage();

    @Override
    protected void doOpen() throws Exception {
        Assert.state(!initialized, "Cannot open an already opened ItemReader, call close first");
        initialized = true;
    }

    @Override
    protected void doClose() throws Exception {
        synchronized (lock) {
            initialized = false;
            page = 0;
            skipRows = 0;
            results = null;
        }
    }

    //改动点
    @Override
    protected void jumpToItem(int itemIndex) throws Exception {

        synchronized (lock) {
            page = itemIndex / pageSize;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Jumping to page " + getPage());
        }
    }

}
