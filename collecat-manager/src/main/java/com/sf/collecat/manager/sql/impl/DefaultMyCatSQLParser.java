package com.sf.collecat.manager.sql.impl;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.stat.TableStat;
import com.sf.collecat.common.Constants;
import com.sf.collecat.common.model.Job;
import com.sf.collecat.common.model.Task;
import com.sf.collecat.manager.config.mycat.XMLSchemaLoader;
import com.sf.collecat.manager.config.mycat.model.DBHostConfig;
import com.sf.collecat.manager.sql.SQLParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.sql.SQLSyntaxErrorException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 默认的SQL解析器，针对MyCat配置
 *
 * @author 862911 Hash Zhang
 * @version 1.0.0
 * @time 2016/6/28
 */
@Component
public class DefaultMyCatSQLParser implements SQLParser {
    @Autowired
    private XMLSchemaLoader xmlSchemaLoader;
    @Value("${job.time.shift}")
    private int TIME_SHIFT = 0;//服务器之间最大时间差
    public final static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 将task拆分成job,分为四步
     * 提取table
     * 获取分片信息
     * 将task按照上次时间到截止时间拆成一个个时间长度为周期时间的job
     * 同时改写SQL，根据MyCat配置组装不同分片连接串的job
     *
     * @param task   传入task
     * @param lastTT task截止时间
     * @return
     * @throws SQLSyntaxErrorException
     * @throws ParserException
     */
    @Cacheable("JobCache")
    public List<Job> parse(Task task, Date lastTT) throws SQLSyntaxErrorException, ParserException {
        List<Job> jobList = new ArrayList<>();
        String table = getTable(task);
        List<String> datanodes = null;
        try {
            datanodes = xmlSchemaLoader.getSchemas().get(task.getSchemaUsed()).getTables().get(table).getDataNodes();
        }catch(Exception e){
            throw new SQLSyntaxErrorException("Schema is not provided!");
        }
        Integer routineTime = task.getRoutineTime();
        long now = lastTT.getTime();
        for (String datanode : datanodes) {
            String database = xmlSchemaLoader.getDataNodes().get(datanode).getDatabase();
            DBHostConfig dbHostConfig = xmlSchemaLoader.getDataHosts().get(xmlSchemaLoader.getDataNodes().get(datanode).getDataHost()).getWriteHosts()[0];
            String url = dbHostConfig.getUrl();
            String username = dbHostConfig.getUser();
            String password = dbHostConfig.getPassword();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("jdbc:mysql://").append(url).append("/").append(database).append("?zeroDateTimeBehavior=convertToNull");
            Date lastDate = new Date(task.getLastTime().getTime());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(lastDate);
            calendar.add(Calendar.SECOND, -TIME_SHIFT);
            lastDate = calendar.getTime();
            calendar.add(Calendar.SECOND, TIME_SHIFT);
            while (calendar.getTime().getTime() < now) {
                Job job = getJob(table, task.getInitialSql(), task, stringBuilder.toString(), username, password, lastDate, calendar.getTime());
                jobList.add(job);
                lastDate = calendar.getTime();
                calendar.add(Calendar.SECOND, routineTime);
            }
            if (lastDate.getTime() < lastTT.getTime() && (lastTT.getTime() - lastDate.getTime()) > 1000L) {
                Job job = getJob(table, task.getInitialSql(), task, stringBuilder.toString(), username, password, lastDate, lastTT);
                jobList.add(job);
            }
        }
        return jobList;
    }

    /**
     * 获取表名
     *
     * @param task
     * @return
     */
    private String getTable(Task task) {
        MySqlStatementParser parser = new MySqlStatementParser(task.getInitialSql());
        SQLStatement statement = parser.parseStatement();
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        statement.accept(visitor);
        String table = null;
        for (TableStat.Name tableStat : visitor.getTables().keySet()) {
            table = tableStat.getName().toUpperCase();
        }
        return table;
    }

    /**
     * 获取Job
     *
     * @param table
     * @param sql
     * @param task
     * @param url
     * @param username
     * @param password
     * @param start
     * @param end
     * @return
     */
    private Job getJob(String table, String sql, Task task, String url, String username, String password, Date start, Date end) {
        Job job = new Job();
        preHandleJob(job, task, url, username, password, start, end);
        modifySQL(job, sql, table, task, start, end);
        return job;
    }

    /**
     * 改写SQL，增加时间段
     *
     * @param job
     * @param sql
     * @param table
     * @param task
     * @param start
     * @param end
     */
    private void modifySQL(Job job, String sql, String table, Task task, Date start, Date end) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("select * from ").append(table).append(" where ").append(task.getTimeField()).append("<'")
                .append(formatter.format(end)).append("' and ").append(task.getTimeField()).append(">='").append(formatter.format(start))
                .append("' order by ").append(task.getTimeField());
        MySqlStatementParser parser1 = new MySqlStatementParser(stringBuilder.toString());
        SQLSelectStatement statement1 = (SQLSelectStatement) parser1.parseStatement();
        MySqlSelectQueryBlock block1 = (MySqlSelectQueryBlock) statement1.getSelect().getQuery();
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLSelectStatement statement = (SQLSelectStatement) parser.parseStatement();
        MySqlSelectQueryBlock block2 = (MySqlSelectQueryBlock) statement.getSelect().getQuery();
        if (block2.getWhere() == null) {
            block2.setWhere(block1.getWhere());
        } else {
            SQLBinaryOpExpr sqlBinaryOpExpr = new SQLBinaryOpExpr();
            sqlBinaryOpExpr.setLeft(block1.getWhere());
            sqlBinaryOpExpr.setRight(block2.getWhere());
            sqlBinaryOpExpr.setOperator(SQLBinaryOperator.BooleanAnd);
            block2.setWhere(sqlBinaryOpExpr);
        }
//        if (block2.getOrderBy() == null) {
//            block2.setOrderBy(block1.getOrderBy());
//        } else {
//            List<SQLSelectOrderByItem> items = block2.getOrderBy().getItems();
//            if (items == null) {
//                items = new ArrayList<>();
//            }
//            items.addAll(block1.getOrderBy().getItems());
//        }

        job.setJobSql(SQLUtils.toMySqlString(statement));
    }

    /**
     * 预处理Job，将一些task属性直接放入job
     *
     * @param job
     * @param task
     * @param url
     * @param username
     * @param password
     * @param start
     * @param end
     */
    private void preHandleJob(Job job, Task task, String url, String username, String password, Date start, Date end) {
        job.setCreatedTime(new Date());
        job.setStatus(Constants.JOB_INIT_VALUE);
        job.setKafkaClusterName(task.getKafkaClusterName());
        job.setKafkaMessageSize(task.getKafkaMessageSize());
        job.setKafkaTopic(task.getKafkaTopic());
        job.setKafkaTopicTokens(task.getKafkaTopicTokens());
        job.setKafkaUrl(task.getKafkaUrl());
        job.setMysqlUrl(url);
        job.setMysqlUsername(username);
        job.setMysqlPassword(password);
        job.setMessageFormat(task.getMessageFormat());
        job.setTimeField(task.getTimeField());
        job.setTimeFieldStart(start);
        job.setTimeFieldEnd(end);
    }

}