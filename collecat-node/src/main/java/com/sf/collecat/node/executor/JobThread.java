package com.sf.collecat.node.executor;

import com.sf.collecat.common.Constants;
import com.sf.collecat.common.model.Job;
import com.sf.collecat.node.jdbc.JDBCConnection;
import com.sf.collecat.node.jdbc.JDBCConnectionPool;
import com.sf.collecat.node.jdbc.exception.GetJDBCCConnectionException;
import com.sf.collecat.node.kafka.KafkaConnection;
import com.sf.collecat.node.kafka.KafkaConnectionPool;
import com.sf.collecat.node.zk.CuratorClient;
import com.sf.kafka.check.KafkaCheckFailException;
import com.sf.kafka.exception.KafkaException;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

/**
 * Created by 862911 on 2016/6/16.
 */
public class JobThread implements Runnable {
    private final static Logger LOGGER = LoggerFactory.getLogger(JobThread.class);
    private final Job job;
    private JDBCConnectionPool jdbcConnectionPool;
    private KafkaConnectionPool kafkaConnectionPool;
    private CuratorClient curatorClient;

    public JobThread(Job job, JDBCConnectionPool jdbcConnectionPool, KafkaConnectionPool kafkaConnectionPool, CuratorClient curatorClient) {
        this.job = job;
        this.jdbcConnectionPool = jdbcConnectionPool;
        this.kafkaConnectionPool = kafkaConnectionPool;
        this.curatorClient = curatorClient;
    }

    @Override
    public void run() {
        KafkaConnection kafkaConnection = null;
        try {
            JDBCConnection jdbcConnection = jdbcConnectionPool.getConnection(job);
            String message[] = null;
            message = jdbcConnection.executeJob();
            kafkaConnection = kafkaConnectionPool.getKafkaConnection(job);
            if (kafkaConnection == null) {
                throw new KafkaException("Can't get KafKa connection!");
            }
            for (int i = 0; i < message.length; i++) {
                kafkaConnection.send(message[i]);
            }
            complete(job);
        } catch (SQLException e) {
            LOGGER.error("Caught exception when execute SQL:", e);
            setException(job);
        } catch (GetJDBCCConnectionException e) {
            LOGGER.error("Caught exception when try to get JDBCConnection:", e);
            setException(job);
        } catch (KafkaException e) {
            LOGGER.error("Caught exception when writing into KafKa:", e);
            setException(job);
            if (kafkaConnection != null) {
                kafkaConnection.setAborted(true);
            }
        } catch (KafkaCheckFailException e) {
            LOGGER.error("Caught exception when writing into KafKa:", e);
            setException(job);
            if (kafkaConnection != null) {
                kafkaConnection.setAborted(true);
            }
        }
    }

    private void complete(Job job) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Constants.JOB_PATH).append("/").append(job.getId());
        InterProcessSemaphoreMutex interProcessSemaphoreMutex = new InterProcessSemaphoreMutex(curatorClient.getClient(), stringBuilder.toString());
        try {
            interProcessSemaphoreMutex.acquire();
            curatorClient.setData(stringBuilder.toString(), Constants.JOB_FINISHED);
        } catch (Exception e) {
            LOGGER.error("ZK exception:", e);
        } finally {
            try {
                interProcessSemaphoreMutex.release();
            } catch (Exception e) {
                LOGGER.error("ZK exception:", e);
            }
        }

    }

    private void setException(Job job) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Constants.JOB_PATH).append("/").append(job.getId());
        curatorClient.setData(stringBuilder.toString(), Constants.JOB_EXCEPTION);
    }
}
