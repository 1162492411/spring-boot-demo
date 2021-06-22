package com.zyg.batch.mysql.batchSupport;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.ClassUtils.getShortName;

/**
 * 一次性填充业务List,一次性弹出业务List,子类继承该类,在doReadPage()中返回业务List即可
 *
 * @author zyg
 */
public abstract class AbstractPeekWholeSingleListReader<T> extends AbstractItemCountingItemStreamItemReader<List<T>> implements InitializingBean {
    protected Log logger = LogFactory.getLog(getClass());

//    protected volatile List<T> results;

    /**
     * 作用是防止重复打开ItemStream
     */
    private volatile boolean initialized = false;
    /**
     * 作用是加锁(可替换为轻量级的ReentrantLock)
     */
    private Object lock = new Object();

    public AbstractPeekWholeSingleListReader() {
        setName(getShortName(AbstractPeekWholeSingleListReader.class));
    }

    @Override
    protected List<T> doRead() throws JsonProcessingException {
        synchronized (lock) {
            logger.debug("read data");
            List<T> list = doReadPage();
            logger.error("return data : " + new ObjectMapper().writeValueAsString(list));
            return list;
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
//            results = null;
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
