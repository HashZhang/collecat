package com.sf.collecat.manager;

import com.sf.collecat.common.mapper.TaskMapper;
import com.sf.collecat.common.model.Task;
import com.sf.collecat.manager.config.mycat.XMLSchemaLoader;
import com.sf.collecat.manager.config.mycat.util.ConfigUtil;
import com.sf.collecat.manager.schedule.ScheduleCat;
import com.sf.collecat.manager.util.PropertyLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Created by 862911 on 2016/6/21.
 */
public class Main {
    public static void main(String[] args) throws IOException, ParseException, InterruptedException {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring.xml");
        ScheduleCat scheduleCat = (ScheduleCat) applicationContext.getBean("scheduleCat");
        Properties prop = new Properties();
        if (args.length > 0) {
            switch (args[0]) {
                case "clear-tasks":
                    scheduleCat.removeAllTask();
                    prop.load(ConfigUtil.class.getClassLoader().getResourceAsStream(args[1]));
                    List<Task> tasks = PropertyLoader.getTasks(prop);
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
        System.out.println("ColleCat-Manager is running now! ");
        while (true) {
            TimeUnit.DAYS.sleep(1L);
        }
    }
}
