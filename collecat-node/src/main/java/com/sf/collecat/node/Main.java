package com.sf.collecat.node;

import com.sf.collecat.node.executor.WorkerPool;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.text.ParseException;
import java.util.concurrent.TimeUnit;

/**
 * Created by 862911 on 2016/6/21.
 */
public class Main {
    public static void main(String[] args) throws IOException, ParseException, InterruptedException {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring-config-service.xml");
//        WorkerPool workerPool = (WorkerPool) applicationContext.getBean("workerPool");
        System.out.println("ColleCat-Node is running now! ");
        while (true) {
            TimeUnit.DAYS.sleep(1L);
        }
    }
}
