package com.sf.collecat.manager.sql;

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

import java.sql.SQLSyntaxErrorException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by 862911 on 2016/6/15.
 */
public class SQLParser {
    private static final XMLSchemaLoader xmlSchemaLoader = new XMLSchemaLoader();
    //// TODO: 2016/6/21 修改为可配置型 
    private static final int TIME_SHIFT = 0;//服务器之间最大时间差
    public final static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static List<Job> parse(Task task, Date lastTT) throws SQLSyntaxErrorException, ParserException {
        List<Job> jobList = new ArrayList<>();
        String table = validate(task);
        List<String> datanodes = xmlSchemaLoader.getSchemas().get(task.getSchemaUsed()).getTables().get(table).getDataNodes();
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
            if (lastDate.getTime() != lastTT.getTime()) {
                Job job = getJob(table, task.getInitialSql(), task, stringBuilder.toString(), username, password, lastDate, lastTT);
                jobList.add(job);
            }
        }
        return jobList;
    }

    private static String validate(Task task) throws SQLSyntaxErrorException {
        MySqlStatementParser parser = new MySqlStatementParser(task.getInitialSql());
        SQLStatement statement = parser.parseStatement();
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        statement.accept(visitor);
        String table = null;
        if (visitor.getTables().keySet().size() > 1) {
            throw new SQLSyntaxErrorException("tables in select sql cannot be larger than 1!");
        }
        for (TableStat.Name tableStat : visitor.getTables().keySet()) {
            table = tableStat.getName().toUpperCase();
        }
        return table;
    }


    private static Job getJob(String table, String sql, Task task, String url, String username, String password, Date start, Date end) {
        Job job = new Job();
        preHandleJob(job, task, url, username, password, start, end);
        modifySQL(job, sql, table, task, start, end);
        return job;
    }

    private static void modifySQL(Job job, String sql, String table, Task task, Date start, Date end) {
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

    private static void preHandleJob(Job job, Task task, String url, String username, String password, Date start, Date end) {
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


    public static void main(String[] args) throws SQLSyntaxErrorException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("select * from ").append("table1").append(" where ").append("timefield").append("<'")
                .append(new Date().toString()).append("' and ").append("timefield").append(">'").append(new Date().toString())
                .append("' order by ").append("timefield");
        MySqlStatementParser parser1 = new MySqlStatementParser(stringBuilder.toString());
        SQLSelectStatement statement1 = (SQLSelectStatement) parser1.parseStatement();
        MySqlStatementParser parser2 = new MySqlStatementParser("select * from table1 where is_deleted=1 and wall=1 order by id");
        SQLSelectStatement statement2 = (SQLSelectStatement) parser2.parseStatement();
        SQLBinaryOpExpr sqlBinaryOpExpr = new SQLBinaryOpExpr();
        sqlBinaryOpExpr.setLeft(((MySqlSelectQueryBlock)statement1.getSelect().getQuery()).getWhere());
        sqlBinaryOpExpr.setRight(((MySqlSelectQueryBlock)statement2.getSelect().getQuery()).getWhere());
        sqlBinaryOpExpr.setOperator(SQLBinaryOperator.BooleanAnd);
        ((MySqlSelectQueryBlock)statement1.getSelect().getQuery()).setWhere(sqlBinaryOpExpr);
        ((MySqlSelectQueryBlock)statement1.getSelect().getQuery()).getOrderBy().getItems().addAll(((MySqlSelectQueryBlock)statement2.getSelect().getQuery()).getOrderBy().getItems());
        System.out.println(SQLUtils.toMySqlString(statement1));
        Task task = new Task();
        task.setInitialSql("select * from employee where i>1 and k>1 order by ll");
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
        task.setSchemaUsed("TESTDB");
        task.setTimeField("created_time");
        System.out.println(SQLParser.parse(task,new Date()));
    }
}
