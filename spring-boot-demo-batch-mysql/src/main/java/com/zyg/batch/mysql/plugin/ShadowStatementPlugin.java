package com.zyg.batch.mysql.plugin;//package com.zyg.batch.mysql.plugin;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.zyg.batch.mysql.util.TableNameParser;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author pengpeng
 */
@Component
@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
@Slf4j
public class ShadowStatementPlugin implements Interceptor {
    private static final String TABLE_SUFFIX = "_shadow";

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        String shadowParams = null;
        try {
            shadowParams = ShadowThreadLocalParams.getShadowParams();
            //判断准入条件 : 仅当当前线程存在ShadowParams才进行SQL修改
            if (StringUtils.isNotBlank(shadowParams)) {
                StatementHandler statementHandler = realTarget(invocation.getTarget());
                MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
                doTable(statementHandler, metaObject);
            }
            return invocation.proceed();
        } catch (Exception e) {
            if (StringUtils.isBlank(shadowParams)) {
                return invocation.proceed();
            } else {
                log.error("ShareStatementPlugin error", e);
                throw e;
            }
        }
    }

    /**
     * pp的代码
     * @param handler
     * @param metaStatementHandler
     * @throws JSQLParserException
     */
    private void doTable(StatementHandler handler, MetaObject metaStatementHandler) throws JSQLParserException {
        BoundSql boundSql = handler.getBoundSql();
        String originalSql = boundSql.getSql();
        if (StringUtils.isNotBlank(originalSql)) {
            log.info("修改前sql：{}", originalSql);
            String changeSql = changeTable(originalSql);
            log.info("修改后sql:{}",changeSql);
            metaStatementHandler.setValue("delegate.boundSql.sql", changeSql);
        }
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {
    }

    /**
     * 获得真正的处理对象,可能多层代理
     *
     * @param target
     * @param <T>
     * @return
     */
    public static <T> T realTarget(Object target) {
        if (Proxy.isProxyClass(target.getClass())) {
            MetaObject metaObject = SystemMetaObject.forObject(target);
            return realTarget(metaObject.getValue("h.target"));
        }
        return (T) target;
    }

    /**
     * 将SQL中所有表名添加上固定的后缀,该代码摘取自MyBatisPlus的动态表名插件com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor
     * @param sql
     * @return
     */
    protected String changeTable(String sql) {
        //从原始SQL获取所有表名
        TableNameParser parser = new TableNameParser(sql);
        List<TableNameParser.SqlToken> names = new ArrayList<>();
        //fixme : 目前无法正确解析 from a force index(xx),b这种情况
        parser.accept(names::add);

        StringBuilder builder = new StringBuilder();
        int last = 0;
        for (TableNameParser.SqlToken name : names) {
            int start = name.getStart();
            if (start != last) {
                builder.append(sql, last, start);
                String value = name.getValue();
                builder.append(value).append(TABLE_SUFFIX);
            }
            last = name.getEnd();
        }
        if (last != sql.length()) {
            builder.append(sql.substring(last));
        }
        return builder.toString();
    }

}
