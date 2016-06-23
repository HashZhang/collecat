package com.sf.collecat.node;

import com.alibaba.fastjson.JSON;
import com.sf.collecat.common.model.Job;
import com.sf.collecat.node.jdbc.JDBCConnection;
import com.sf.collecat.node.jdbc.JDBCConnectionPool;
import com.sf.collecat.node.kafka.KafkaConnection;
import com.sf.collecat.node.kafka.KafkaConnectionPool;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

/**
 * Created by 862911 on 2016/6/16.
 */
@ContextConfiguration(locations = {"/spring-config-service.xml"})
public class TestJob extends AbstractJUnit4SpringContextTests {
    @Autowired
    JDBCConnectionPool jdbcConnectionPool;
    @Autowired
    KafkaConnectionPool kafkaConnectionPool;
    Job job = new Job();

    @Before
    public void init() {
        job.setMysqlUrl("jdbc:mysql://10.202.4.39:3307/incsgsmonitordb?zeroDateTimeBehavior=convertToNull");
        job.setMysqlUsername("root");
        job.setMysqlPassword("sf123456");
        job.setJobSql("select * from smp_multi_dim_diagram");
        job.setMessageFormat("csv");
        job.setKafkaClusterName("other");
        job.setKafkaTopic("COLLECAT_TEST");
        job.setKafkaTopicTokens("COLLECAT_TEST:@08f0Wp^");
        job.setKafkaUrl("http://10.202.34.30:8292/mom-mon/monitor/requestService.pub");
    }

    @Test
    public void testWholeJob() throws Exception {
        JDBCConnection jdbcConnection = jdbcConnectionPool.getConnection(job);
        String message[] = jdbcConnection.executeJob();
        KafkaConnection kafkaConnection = kafkaConnectionPool.getKafkaConnection(job);
        kafkaConnection.send(message.toString());
        jdbcConnection = jdbcConnectionPool.getConnection(job);
        message = jdbcConnection.executeJob();
        kafkaConnection = kafkaConnectionPool.getKafkaConnection(job);
        kafkaConnection.send(message.toString());
    }
    @Test
    public void testMultiThread() throws InterruptedException {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                JDBCConnection jdbcConnection = jdbcConnectionPool.getConnection(job);
                String message[] = null;
                try {
                    message = jdbcConnection.executeJob();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                KafkaConnection kafkaConnection = kafkaConnectionPool.getKafkaConnection(job);

                try {
                    kafkaConnection.send(message.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        Thread threads[] = new Thread[10];
        for (int i = 0; i < 10 ; i++) {
            threads[i] = new Thread(runnable);
            threads[i].start();
        }
        for (int i = 0; i < 10; i++) {
            threads[i].join();
        }
        System.out.println(jdbcConnectionPool.getConnMap().keySet().size());
    }

    @Test
    public void running() throws InterruptedException {
        while(true){
            TimeUnit.DAYS.sleep(1);
        }
    }
}
