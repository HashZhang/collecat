package com.sf.collecat.manager;

import com.sf.collecat.common.model.Job;
import com.sf.collecat.common.model.Task;
import com.sf.collecat.manager.config.mycat.util.ConfigUtil;
import com.sf.collecat.manager.schedule.ScheduleCat;
import com.sf.collecat.manager.sql.SQLParser;
import com.sf.collecat.manager.util.PropertyLoader;
import com.sf.collecat.manager.util.Validator;
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
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath*:spring.xml");
        ScheduleCat scheduleCat = (ScheduleCat) applicationContext.getBean("scheduleCat");
        Properties prop = new Properties();
        List<Task> tasks = null;
        if (args.length > 0) {
            switch (args[0]) {
                case "clear-tasks":
                    scheduleCat.removeAllTask();
                    System.exit(0);
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
                Validator.validateKafKaConnection(tasks);
            } catch (Exception e) {
                System.out.println("Wrong KafKa configuration!");
                e.printStackTrace();
                System.exit(-1);
            }
            try {
                Validator.validateJDBCConnections(tasks);
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
}
