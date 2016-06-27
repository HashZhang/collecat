package com.sf.collecat.manager.util;

import com.sf.collecat.common.model.Job;
import com.sf.collecat.common.model.Task;
import com.sf.collecat.manager.sql.SQLParser;
import com.sf.kafka.api.produce.ProduceConfig;
import com.sf.kafka.api.produce.ProducerPool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by 862911 on 2016/6/25.
 */
public class Validator {
    public static boolean validateKafKaConnection(List<Task> tasks) throws Exception {
        for (Task task : tasks) {
            ProducerPool kafkaProducer = null;
            try {
                ProduceConfig produceConfig = new ProduceConfig(10, task.getKafkaUrl(), task.getKafkaClusterName(), task.getKafkaTopicTokens());
                kafkaProducer = new ProducerPool(produceConfig);
            } catch (Exception e) {
                throw new Exception(e);
            } finally {
                if (kafkaProducer != null) {
                    kafkaProducer.close();
                }
            }
        }
        return true;
    }

    public static boolean validateJDBCConnections(List<Task> tasks) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        Set<String> stringSet = new HashSet<>();
        for (Task task : tasks) {
            List<Job> jobs = SQLParser.parse(task, new Date(System.currentTimeMillis() - 1000));
            for (Job job : jobs) {
                Connection conn = null;
                try {
                    if(stringSet.contains(job.getMysqlUrl()+job.getMysqlUsername()+job.getMysqlPassword())){
                        continue;
                    }
                    stringSet.add(job.getMysqlUrl()+job.getMysqlUsername()+job.getMysqlPassword());
                    System.out.println("Validating:" + job.getMysqlUrl());
                    conn = DriverManager.getConnection(job.getMysqlUrl(), job.getMysqlUsername(), job.getMysqlPassword());
                    Statement stmt = conn.createStatement();
                    String sql = "select 1";
                    stmt.execute(sql);
                } catch (Exception e) {
                    throw new Exception(e);
                } finally {
                    if (conn != null) {
                        conn.close();
                    }
                }
            }
        }
        return true;
    }
}
