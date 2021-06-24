package com.zyg.batch.job.commonSupport;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.ClassUtils.getShortName;

/**
 * 一次性填充业务List,一次性弹出业务List,子类继承该类,在doReadPage()中返回业务List即可
 *  该类实现了锁用于多线程reader，实现了InitializingBean以便解决该类被Spring托管的问题，实现了ItemStream的相关方法
 * @author zyg
 */
public abstract class AbstractPeekWholeListReader<T> extends AbstractItemCountingItemStreamItemReader<List<T>> implements InitializingBean {
    protected Log logger = LogFactory.getLog(getClass());

    /**
     * 作用是防止重复打开ItemStream
     */
    private volatile boolean initialized = false;
    private volatile boolean hasFilled = false;

    /**
     * 作用是加锁(可替换为轻量级的ReentrantLock)
     */
    private Object lock = new Object();

    public AbstractPeekWholeListReader() {
        setName(getShortName(AbstractPeekWholeListReader.class));
    }

    @Override
    protected List<T> doRead() throws JsonProcessingException {
        synchronized (lock) {
            if(hasFilled){
                hasFilled = false;
                return null;
            }
            logger.debug("read data");
            List<T> list = doReadPage();
            if(logger.isDebugEnabled()){
                logger.debug("return data : " + new ObjectMapper().writeValueAsString(list));
            }
            hasFilled = true;
            return CollectionUtils.isEmpty(list) ? null : list;
        }
    }

    /**
     * 扩展方法,子类在该方法中将业务数据返回
     *
     * @return List<T> 读取到的业务数据
     */
    abstract protected List<T> doReadPage();

    @Override
    protected void doOpen() {
        Assert.state(!initialized, "Cannot open an already opened ItemReader, call close first");
        initialized = true;
    }

    @Override
    protected void doClose() {
        synchronized (lock) {
            initialized = false;
        }
    }

    @Override
    protected void jumpToItem(int itemIndex) throws Exception {
        //无需跳转,因为该类只有一条list
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }
}
