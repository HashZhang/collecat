package com.sf.collecat.node.kafka;

import com.sf.kafka.api.produce.IKafkaProducer;
import com.sf.kafka.api.produce.ProduceConfig;
import com.sf.kafka.api.produce.ProducerPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by 862911 on 2016/6/15.
 */
public class KafkaConnection {
    private final static Logger LOGGER = LoggerFactory.getLogger(KafkaConnection.class);
    private final int poolSize;
    private final String url;
    private final String clusterName;
    private final String topicTokens;
    private IKafkaProducer kafkaProducer;
    private final String topic;
    private volatile boolean aborted = false;
    private final ReentrantLock reentrantLock = new ReentrantLock();

    public KafkaConnection(int poolSize, String topic, String url, String clusterName, String topicTokens) {
        this.poolSize = poolSize;
        this.url = url;
        this.clusterName = clusterName;
        this.topicTokens = topicTokens;
        ProduceConfig produceConfig = new ProduceConfig(poolSize, url, clusterName, topicTokens);
        kafkaProducer = new ProducerPool(produceConfig);
        this.topic = topic;
    }

    public void send(String message) throws Exception {
        try {
            kafkaProducer.sendString(topic, message);
        } catch (Exception e) {
            LOGGER.warn("",e);
            int count = 0;
            while (count < 3) {
                LOGGER.warn("Caught Exception while send kafka message, retry {} times!", count);
                try {
                    reentrantLock.lock();
                    kafkaProducer.close();
                    ProduceConfig produceConfig = new ProduceConfig(poolSize, url, clusterName, topicTokens);
                    kafkaProducer = new ProducerPool(produceConfig);
                    kafkaProducer.sendString(topic, message);
                    return;
                } catch (Exception ee) {
                    count++;
                } finally {
                    reentrantLock.unlock();
                }
            }
            throw new Exception("Caught Exception while send kafka message, retried 3 times but still failed!");
        }
    }

    public boolean isAborted() {
        return aborted;
    }

    public void setAborted(boolean aborted) {
        this.aborted = aborted;
    }
}
