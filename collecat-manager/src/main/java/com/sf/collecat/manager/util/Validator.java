package com.sf.collecat.manager.util;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.sf.collecat.common.model.Job;
import com.sf.collecat.common.model.Task;
import com.sf.collecat.common.utils.StrUtils;
import com.sf.collecat.manager.exception.validate.ValidateJDBCException;
import com.sf.collecat.manager.exception.validate.ValidateKafkaException;
import com.sf.collecat.manager.exception.validate.ValidateSQLException;
import com.sf.collecat.manager.sql.SQLParser;
import com.sf.kafka.api.produce.ProduceConfig;
import com.sf.kafka.api.produce.ProducerPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by 862911 on 2016/6/25.
 */
@Component
public class Validator {
    @Autowired
    private SQLParser sqlParser;

    /**
     * 验证SQL语句是否有问题
     *
     * @param task
     * @return
     * @throws SQLSyntaxErrorException
     */
    public String validateSQL(Task task) throws ValidateSQLException {
        MySqlStatementParser parser = new MySqlStatementParser(task.getInitialSql());
        MySqlSchemaStatVisitor visitor = null;
        try {
            SQLStatement statement = parser.parseStatement();
            visitor = new MySqlSchemaStatVisitor();
            statement.accept(visitor);
        } catch (Exception e) {
            throw new ValidateSQLException("SQLSyntaxException!");
        }
        String table = null;
        if (visitor.getTables().keySet().size() > 1) {
            throw new ValidateSQLException("tables in select sql cannot be larger than 1!");
        }
        for (TableStat.Name tableStat : visitor.getTables().keySet()) {
            table = tableStat.getName().toUpperCase();
        }
        return table;
    }

    public boolean validateKafKaConnection(Task task) throws ValidateKafkaException {
        ProducerPool kafkaProducer = null;
        try {
            ProduceConfig produceConfig = new ProduceConfig(10, task.getKafkaUrl(), task.getKafkaClusterName(), task.getKafkaTopicTokens());
            kafkaProducer = new ProducerPool(produceConfig);
        } catch (Exception e) {
            throw new ValidateKafkaException(e);
        } finally {
            if (kafkaProducer != null) {
                kafkaProducer.close();
            }
        }
        return true;
    }

    public boolean validateJDBCConnections(Task task) throws SQLException, ClassNotFoundException, ValidateJDBCException {
        Class.forName("com.mysql.jdbc.Driver");
        Set<String> stringSet = new HashSet<>();
        List<Job> jobs = sqlParser.parse(task, new Date(System.currentTimeMillis() - 1000));
        for (Job job : jobs) {
            Connection conn = null;
            try {
                if (stringSet.contains(job.getMysqlUrl() + job.getMysqlUsername() + job.getMysqlPassword())) {
                    continue;
                }
                stringSet.add(job.getMysqlUrl() + job.getMysqlUsername() + job.getMysqlPassword());
                System.out.println("Validating:" + job.getMysqlUrl());
                conn = DriverManager.getConnection(job.getMysqlUrl(), job.getMysqlUsername(), job.getMysqlPassword());
                Statement stmt = conn.createStatement();
                String table = validateSQL(task);
                String sql = StrUtils.makeString("select * from ",table," limit 1");
                stmt.execute(sql);
            } catch (Exception e) {
                throw new ValidateJDBCException(e);
            } finally {
                if (conn != null) {
                    conn.close();
                }
            }
        }
        return true;
    }
}
