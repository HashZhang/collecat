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
import com.sf.collecat.common.model.Subtask;
import com.sf.collecat.common.model.Task;
import com.sf.collecat.common.utils.StrUtils;
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
    private int TIME_SHIFT = 2;//服务器之间最大时间差
    public final static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private boolean intervalLT1S(Date date1, Date date2) {
        if (formatter.format(date1).equals(formatter.format(date2))) {
            return false;
        } else {
            return true;
        }
    }
    @Cacheable("JobCache")
    public List<Job> parse(Task task, Date lastTT) throws SQLSyntaxErrorException, ParserException {
        List<Job> jobList = new ArrayList<>();
        String table = getTable(task);
        List<String> datanodes = null;
        try {
            datanodes = xmlSchemaLoader.getSchemas().get(task.getSchemaUsed()).getTables().get(table).getDataNodes();
        } catch (Exception e) {
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
            Date lastDate = new Date(task.getStartTime().getTime());
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

    @Override
    public Job parse(Subtask subtask) throws SQLSyntaxErrorException, ParserException {
        Date lastTime = subtask.getLastTime();
        Date currTime = subtask.getCurrTime();
        Date endTime = subtask.getEndTime();
        long routineTime = subtask.getRoutineTime();
        long scheduleTime = lastTime.getTime() + 1000 * routineTime;
        Job job = null;
        if (endTime != null && scheduleTime > endTime.getTime()) {
            if (currTime.getTime() < endTime.getTime()) {
                if (intervalLT1S(currTime, lastTime)) {
                    job = getJob(subtask, currTime);
                }
            } else {
                if (intervalLT1S(endTime, lastTime)) {
                    job = getJob(subtask, endTime);
                }
            }

        } else if (scheduleTime > currTime.getTime()) {
            if (intervalLT1S(lastTime, currTime)) {
                job = getJob(subtask, currTime);
            }
        } else {
            Date schedule = new Date(scheduleTime);
            if (intervalLT1S(lastTime, schedule)) {
                job = getJob(subtask, schedule);
            }
        }
        return job;
    }

    private Job getJob(Subtask subtask, Date endTime) {
        Job job = new Job();
        job.setKafkaClusterName(subtask.getKafkaClusterName());
        job.setKafkaMessageSize(subtask.getKafkaMessageSize());
        job.setKafkaTopic(subtask.getKafkaTopic());
        job.setKafkaTopicTokens(subtask.getKafkaTopicTokens());
        job.setKafkaUrl(subtask.getKafkaUrl());
        job.setMessageFormat(subtask.getMessageFormat());
        job.setMysqlPassword(subtask.getMysqlPassword());
        job.setMysqlUrl(subtask.getMysqlUrl());
        job.setMysqlUsername(subtask.getMysqlUsername());
        job.setStatus(Constants.JOB_INIT_VALUE);
        job.setSubtaskId(subtask.getId());
        job.setTimeField(subtask.getTimeField());
        job.setTimeFieldStart(new Date(subtask.getLastTime().getTime() - TIME_SHIFT * 1000));
        job.setTimeFieldEnd(endTime);
        modifySQL(job, subtask.getInitialSql(), getTable(subtask), subtask, job.getTimeFieldStart(), job.getTimeFieldEnd());
        return job;
    }

    private String getTable(Subtask subtask) {
        MySqlStatementParser parser = new MySqlStatementParser(subtask.getInitialSql());
        SQLStatement statement = parser.parseStatement();
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        statement.accept(visitor);
        String table = null;
        for (TableStat.Name tableStat : visitor.getTables().keySet()) {
            table = tableStat.getName().toUpperCase();
        }
        return table;
    }

    private void modifySQL(Job job, String sql, String table, Subtask subtask, Date start, Date end) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("select * from ").append(table).append(" where ").append(subtask.getTimeField()).append("<'")
                .append(formatter.format(end)).append("' and ").append(subtask.getTimeField()).append(">='").append(formatter.format(start))
                .append("' order by ").append(subtask.getTimeField());
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
    public List<Subtask> parse(Task task) throws SQLSyntaxErrorException {
        List<Subtask> subtasks = new ArrayList<>();
        String table = getTable(task);
        List<String> datanodes = null;
        try {
            datanodes = xmlSchemaLoader.getSchemas().get(task.getSchemaUsed()).getTables().get(table).getDataNodes();
        } catch (Exception e) {
            throw new SQLSyntaxErrorException("Schema is not provided or schema does not exist!");
        }
        for (String datanode : datanodes) {
            DBHostConfig dbHostConfig = xmlSchemaLoader.getDataHosts().get(xmlSchemaLoader.getDataNodes().get(datanode).getDataHost()).getWriteHosts()[0];
            String database = xmlSchemaLoader.getDataNodes().get(datanode).getDatabase();
            String url = StrUtils.makeString("jdbc:mysql://", dbHostConfig.getUrl(), "/", database, "?zeroDateTimeBehavior=convertToNull");
            String username = dbHostConfig.getUser();
            String password = dbHostConfig.getPassword();
            Subtask subtask = getSubtask(task, url, username, password);
            subtasks.add(subtask);
        }
        return subtasks;
    }

    private Subtask getSubtask(Task task, String url, String username, String password) {
        Subtask subtask = new Subtask();
        subtask.setAllocateRoutine(task.getAllocateRoutine());
        subtask.setInitialSql(task.getInitialSql());
        subtask.setIsActive(task.getIsActive());
        subtask.setKafkaClusterName(task.getKafkaClusterName());
        subtask.setKafkaMessageSize(task.getKafkaMessageSize());
        subtask.setKafkaTopic(task.getKafkaTopic());
        subtask.setKafkaTopicTokens(task.getKafkaTopicTokens());
        subtask.setKafkaUrl(task.getKafkaUrl());
        subtask.setLastTime(task.getStartTime());
        subtask.setEndTime(task.getEndTime());
        subtask.setMessageFormat(task.getMessageFormat());
        subtask.setMysqlPassword(password);
        subtask.setMysqlUrl(url);
        subtask.setMysqlUsername(username);
        subtask.setRoutineTime(task.getRoutineTime());
        subtask.setSchemaUsed(task.getSchemaUsed());
        subtask.setTaskId(task.getId());
        subtask.setTimeField(task.getTimeField());
        return subtask;
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