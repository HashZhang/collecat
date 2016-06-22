package com.sf.collecat.node.executor;

import com.sf.collecat.common.model.Job;
import com.sf.collecat.node.jdbc.JDBCConnectionPool;
import com.sf.collecat.node.kafka.KafkaConnectionPool;
import com.sf.collecat.node.zk.CuratorClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by 862911 on 2016/6/16.
 */
public class WorkerPool extends Thread {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerPool.class);
    private ExecutorService pool;
    @Autowired
    private CuratorClient curatorClient;
    @Autowired
    private JDBCConnectionPool jdbcConnectionPool;
    @Autowired
    private KafkaConnectionPool kafkaConnectionPool;
    private int poolSize;
    private int queueLength;

    public ArrayBlockingQueue<Job> getJobQueue() {
        return jobQueue;
    }

    private ArrayBlockingQueue<Job> jobQueue;


    @Override
    public void run() {
        Job job = null;
        while (true) {
            try {
                job = jobQueue.take();
            } catch (InterruptedException e) {
                LOGGER.error("Interrupted Exception:",e);
            }
            if (job != null) {
                JobThread jobThread = new JobThread(job, jdbcConnectionPool, kafkaConnectionPool,curatorClient);
                pool.submit(jobThread);
            }
        }
    }

    public int getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
        pool = Executors.newFixedThreadPool(poolSize);
    }

    public int getQueueLength() {
        return queueLength;
    }

    public void setQueueLength(int queueLength) {
        this.queueLength = queueLength;
        jobQueue = new ArrayBlockingQueue<Job>(queueLength);
    }
}
