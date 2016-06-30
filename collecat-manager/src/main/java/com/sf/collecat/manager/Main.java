package com.sf.collecat.manager;

import com.sf.collecat.common.model.Task;
import com.sf.collecat.manager.config.mycat.util.ConfigUtil;
import com.sf.collecat.manager.exception.job.JobRemoveException;
import com.sf.collecat.manager.exception.task.TaskAddException;
import com.sf.collecat.manager.exception.task.TaskDeleteException;
import com.sf.collecat.manager.exception.task.TaskSearchException;
import com.sf.collecat.manager.exception.validate.ValidateJDBCException;
import com.sf.collecat.manager.exception.validate.ValidateKafkaException;
import com.sf.collecat.manager.exception.validate.ValidateSQLException;
import com.sf.collecat.manager.manage.JobManager;
import com.sf.collecat.manager.manage.TaskManager;
import com.sf.collecat.manager.schedule.DefaultScheduler;
import com.sf.collecat.manager.util.PropertyLoader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by 862911 on 2016/6/21.
 */
public class Main {
    public static void main(String[] args) throws IOException, ParseException, InterruptedException, TaskSearchException, TaskDeleteException, JobRemoveException, TaskAddException, ClassNotFoundException, SQLException, ValidateJDBCException, ValidateSQLException, ValidateKafkaException {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath*:spring.xml");
        JobManager jobManager = (JobManager) applicationContext.getBean("jobManager");
        TaskManager taskManager = (TaskManager) applicationContext.getBean("taskManager");
        Properties prop = new Properties();
        List<Task> tasks = null;
        if (args.length > 0) {
            switch (args[0]) {
                case "clear-tasks":
                    taskManager.clearTasks();
                    System.exit(0);
                    break;
                case "clear-jobs":
                    jobManager.clearJobs();
                    System.exit(0);
                    break;
                case "clear-all":
                    taskManager.clearTasks();
                    jobManager.clearJobs();
                    System.exit(0);
                    break;
                default:
                    prop.load(ConfigUtil.class.getClassLoader().getResourceAsStream(args[0]));
                    tasks = PropertyLoader.getTasks(prop);
                    for (Task task : tasks) {
                        taskManager.addTask(task);
                    }
                    break;
            }
        }
//        if (tasks != null) {
//            try {
//                Validator.validateKafKaConnection(tasks);
//            } catch (Exception e) {
//                System.out.println("Wrong KafKa configuration!");
//                e.printStackTrace();
//                System.exit(-1);
//            }
//            try {
//                Validator.validateJDBCConnections(tasks);
//            } catch (Exception e) {
//                System.out.println("Wrong MyCat configuration!");
//                e.printStackTrace();
//                System.exit(-1);
//            }
//        }
        System.out.println("ColleCat-Manager is running now! ");
        while (true) {
            TimeUnit.DAYS.sleep(1L);
        }
    }
}
