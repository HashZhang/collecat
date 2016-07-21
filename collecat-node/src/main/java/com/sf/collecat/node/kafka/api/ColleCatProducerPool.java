package com.sf.collecat.node.kafka.api;

import com.sf.collecat.common.Constants;
import com.sf.kafka.api.produce.*;
import com.sf.kafka.check.util.AuthUtil;
import com.sf.kafka.util.KafkaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by 862911 on 2016/6/27.
 */
public class ColleCatProducerPool extends BaseProducer {

    private Logger L;
    private BlockingQueue<CollecatKafkaProducer> queue;
    private int poolSize;
    private Set<String> authorizedTopics;

    public ColleCatProducerPool(ProduceConfig produceConfig) {
        this(produceConfig, (CollecatProduceOptionalConfig)null);
    }

    public ColleCatProducerPool(ProduceConfig produceConfig, CollecatProduceOptionalConfig optionalConfig) {
        this.L = LoggerFactory.getLogger(ProducerPool.class);
        if(!KafkaUtil.noCheckKafkaAuth()) {
            String brokers = AuthUtil.getBrokers(produceConfig.getClusterName(), produceConfig.getTopicTokens(), produceConfig.getMonitorUrl());
            this.authorizedTopics = AuthUtil.getTopicsFromTopicTokens(produceConfig.getTopicTokens());
            this.poolSize = produceConfig.getPoolSize();
            this.queue = new ArrayBlockingQueue(this.poolSize);

            for(int i = 0; i < this.poolSize; ++i) {
                this.putToQueue(new CollecatKafkaProducer(brokers, optionalConfig));
            }

        }
    }

    private void putToQueue(CollecatKafkaProducer producer) {
        try {
            this.queue.put(producer);
        } catch (InterruptedException var3) {
            throw new RuntimeException(var3);
        }
    }

    public void sendBytes(String topic, byte[] message) {
        this.checkAuthStatus(topic);
        CollecatKafkaProducer producer = null;

        try {
            producer = (CollecatKafkaProducer)this.queue.take();
            producer.sendBytes(topic, message);
        } catch (InterruptedException var8) {
            var8.printStackTrace();
        } finally {
            if(producer != null) {
                this.putToQueue(producer);
            }

        }

    }

    public void batchSendBytes(String topic, List<byte[]> messages) {
        this.checkAuthStatus(topic);
        CollecatKafkaProducer producer = null;

        try {
            producer = (CollecatKafkaProducer)this.queue.take();
            producer.batchSendBytes(topic, messages);
        } catch (InterruptedException var8) {
            var8.printStackTrace();
        } finally {
            if(producer != null) {
                this.putToQueue(producer);
            }

        }

    }

    private void checkAuthStatus(String topic) {
        if(!this.authorizedTopics.contains(topic)) {
            throw new IllegalArgumentException(String.format("topic %s is unAuthorized", new Object[]{topic}));
        }
    }

    public void sendString(String topic, String message,String id) {
        this.sendBytes(topic, this.toByte(message));
    }

    private byte[] toByte(String message) {
        try {
            return message.getBytes(Constants.STRING_ENCODING);
        } catch (UnsupportedEncodingException var3) {
            throw new RuntimeException(var3);
        }
    }

    public void close() {
        for(int i = 0; i < this.poolSize; ++i) {
            try {
                ((CollecatKafkaProducer)this.queue.take()).close();
            } catch (InterruptedException var3) {
                throw new RuntimeException(var3);
            }
        }

    }

}
