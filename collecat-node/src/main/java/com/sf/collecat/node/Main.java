package com.sf.collecat.node;

import com.sf.collecat.node.common.NodeConstants;
import com.sf.collecat.node.executor.WorkerPool;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.text.ParseException;
import java.util.concurrent.TimeUnit;

/**
 * Node启动类
 *
 * @author 862911 Hash Zhang
 * @version 1.0.0
 * @date 2016/6/23.
 */
public class Main {
    public static void main(String[] args) throws IOException, ParseException, InterruptedException {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(NodeConstants.ROOT_SPRING_CONFIG);
        WorkerPool workerPool = applicationContext.getBean(WorkerPool.class);
        System.out.println(NodeConstants.POOL_SIZE_MESSAGE + workerPool.getPoolSize());
        System.out.println(NodeConstants.WELCOME);
        while (true) {
            TimeUnit.DAYS.sleep(1L);
        }
    }
}
