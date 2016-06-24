package com.sf.collecat.manager;

import com.sf.collecat.common.model.Job;
import com.sf.collecat.common.model.Task;
import com.sf.collecat.manager.config.mycat.XMLSchemaLoader;
import com.sf.collecat.manager.config.mycat.util.ConfigUtil;
import com.sf.collecat.manager.schedule.ScheduleCat;
import com.sf.collecat.manager.sql.SQLParser;
import com.sf.collecat.manager.util.PropertyLoader;
import com.sf.kafka.api.produce.ProduceConfig;
import com.sf.kafka.api.produce.ProducerPool;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by 862911 on 2016/6/21.
 */
public class Main {
    public static void main(String[] args) throws IOException, ParseException, InterruptedException {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring.xml");
        ScheduleCat scheduleCat = (ScheduleCat) applicationContext.getBean("scheduleCat");
        Properties prop = new Properties();
        List<Task> tasks = null;
        if (args.length > 0) {
            switch (args[0]) {
                case "clear-tasks":
                    scheduleCat.removeAllTask();
                    prop.load(ConfigUtil.class.getClassLoader().getResourceAsStream(args[1]));
                    tasks = PropertyLoader.getTasks(prop);
                    for (Task task : tasks) {
                        scheduleCat.insertTask(task);
                        scheduleCat.scheduletask(task);
                    }
                    break;
                case "restart-exception-tasks":
                    scheduleCat.resetAllExceptionJob();
                    break;
                default:
                    prop.load(ConfigUtil.class.getClassLoader().getResourceAsStream(args[0]));
                    tasks = PropertyLoader.getTasks(prop);
                    for (Task task : tasks) {
                        scheduleCat.insertTask(task);
                        scheduleCat.scheduletask(task);
                    }
                    break;
            }
        }
        if (tasks != null) {
            try {
                validateKafKaConnection(tasks);
            } catch (Exception e) {
                System.out.println("Wrong KafKa configuration!");
                e.printStackTrace();
                System.exit(-1);
            }
            try {
                validateJDBCConnections(tasks);
            } catch (Exception e) {
                System.out.println("Wrong MyCat configuration!");
                e.printStackTrace();
                System.exit(-1);
            }
        }
        System.out.println("ColleCat-Manager is running now! ");
        while (true) {
            TimeUnit.DAYS.sleep(1L);
        }
    }

    private static boolean validateKafKaConnection(List<Task> tasks) throws Exception {
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

    private static boolean validateJDBCConnections(List<Task> tasks) throws Exception {
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
