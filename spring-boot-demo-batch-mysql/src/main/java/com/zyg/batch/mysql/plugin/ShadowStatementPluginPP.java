package com.zyg.batch.mysql.plugin;



import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.util.deparser.ExpressionDeParser;
import net.sf.jsqlparser.util.deparser.InsertDeParser;
import net.sf.jsqlparser.util.deparser.SelectDeParser;
import net.sf.jsqlparser.util.deparser.UpdateDeParser;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.Properties;

/**
 * @author pengpeng
 */
//@Component("shadowStatementPlugin")
//@Intercepts({
//    @Signature(type = StatementHandler.class, method = "prepare", args = {
//        Connection.class, Integer.class})})
@Slf4j
public class ShadowStatementPluginPP implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        String shadowParams = null;
        try {
            shadowParams = ShadowThreadLocalParams.getShadowParams();
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

    private void doTable(StatementHandler handler, MetaObject metaStatementHandler) throws JSQLParserException {
        BoundSql boundSql = handler.getBoundSql();
        String originalSql = boundSql.getSql();
        if (StringUtils.isNotBlank(originalSql)) {
            log.info("修改前sql：{}", originalSql);

            StringBuilder buffer = new StringBuilder();
            Statement statement = CCJSqlParserUtil.parse(originalSql);

            if(statement instanceof Select){
                ExpressionDeParser expressionDeParser = new ExpressionDeParser();
                SelectDeParser changeTableParser = new MySelectDeParser(expressionDeParser, buffer);
                expressionDeParser.setSelectVisitor(changeTableParser);
                expressionDeParser.setBuffer(buffer);


                ((Select) statement).getSelectBody().accept(changeTableParser);
            }
            else if(statement instanceof Insert){
                ExpressionDeParser deParser = new ExpressionDeParser();
                SelectDeParser selectDeParser = new MySelectDeParser(deParser,buffer);

                Insert insert = (Insert)statement;
                String originTableName = insert.getTable().getName();
                insert.getTable().setName(originTableName + "_bakkk");
                if(insert.getSelect() != null && insert.getSelect().getSelectBody() != null){
                    insert.getSelect().getSelectBody().accept(selectDeParser);
                }
            }else if(statement instanceof Update){
//                UpdateDeParser
//                Update update = (Update)statement;
//                update.getTable().accept(changeTableParser);
//                if(update.getSelect() != null && update.getSelect().getSelectBody() != null){
//                    update.getSelect().getSelectBody().accept(changeTableParser);
//                }
                //joins、startJoins是什么
            }else if(statement instanceof Delete){
//                Delete delete = (Delete) statement;
//                delete.getTable().accept(changeTableParser);
                //tables、joins是什么
            }
            metaStatementHandler.setValue("delegate.boundSql.sql", buffer.toString());
            log.info("修改后sql：{}", buffer);
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


    public static class MySelectDeParser extends SelectDeParser {

        public MySelectDeParser(ExpressionVisitor expressionVisitor, StringBuilder buffer) {
            super(expressionVisitor, buffer);
        }

        @Override
        public void visit(Table tableName) {
            final StringBuilder buffer = getBuffer();
            tableName.setName(tableName.getName() + "_bakk");
            buffer.append(tableName);
        }

    }


}
