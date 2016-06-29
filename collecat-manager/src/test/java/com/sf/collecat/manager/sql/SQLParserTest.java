package com.sf.collecat.manager.sql;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.sf.collecat.common.model.Job;
import com.sf.collecat.common.model.Task;
import com.sf.collecat.manager.BaseSpringTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;

import java.sql.SQLSyntaxErrorException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by 862911 on 2016/6/25.
 */
public class SQLParserTest extends BaseSpringTest {
    @Autowired
    private SQLParser sqlParser;
    @Autowired
    private CacheManager cacheManager;

    @Test
    public void testParse() throws SQLSyntaxErrorException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("select * from ").append("tt_delivery_order").append(" where ").append("timefield").append("<'")
                .append(new Date().toString()).append("' and ").append("timefield").append(">'").append(new Date().toString())
                .append("' order by ").append("timefield");
        MySqlStatementParser parser1 = new MySqlStatementParser(stringBuilder.toString());
        SQLSelectStatement statement1 = (SQLSelectStatement) parser1.parseStatement();
        MySqlStatementParser parser2 = new MySqlStatementParser("select * from table1 where is_deleted=1 and wall=1 order by id");
        SQLSelectStatement statement2 = (SQLSelectStatement) parser2.parseStatement();
        SQLBinaryOpExpr sqlBinaryOpExpr = new SQLBinaryOpExpr();
        sqlBinaryOpExpr.setLeft(((MySqlSelectQueryBlock) statement1.getSelect().getQuery()).getWhere());
        sqlBinaryOpExpr.setRight(((MySqlSelectQueryBlock) statement2.getSelect().getQuery()).getWhere());
        sqlBinaryOpExpr.setOperator(SQLBinaryOperator.BooleanAnd);
        ((MySqlSelectQueryBlock) statement1.getSelect().getQuery()).setWhere(sqlBinaryOpExpr);
        ((MySqlSelectQueryBlock) statement1.getSelect().getQuery()).getOrderBy().getItems().addAll(((MySqlSelectQueryBlock) statement2.getSelect().getQuery()).getOrderBy().getItems());
        System.out.println(SQLUtils.toMySqlString(statement1));
        Task task = new Task();
        task.setInitialSql("select * from tt_delivery_order where i>1 and k>1 order by ll");
        task.setIsActive(true);
        task.setKafkaClusterName("ab");
        task.setKafkaMessageSize(100);
        task.setKafkaTopic("topic1");
        task.setKafkaTopicTokens("topic:token");
        task.setKafkaUrl("url");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, -10000);
        task.setLastTime(calendar.getTime());
        task.setMessageFormat("csv");
        task.setRoutineTime(100);
        task.setSchemaUsed("exp");
        task.setTimeField("created_time");
        Date date = new Date();
        List<Job> parse = sqlParser.parse(task, date);
        List<Job> parse2 = sqlParser.parse(task, date);
        System.out.println(parse.equals(parse2));
    }
}
