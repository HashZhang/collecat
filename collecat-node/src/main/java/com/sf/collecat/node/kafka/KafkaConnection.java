package com.sf.collecat.node.kafka;

import com.sf.kafka.api.produce.IKafkaProducer;
import com.sf.kafka.api.produce.ProduceConfig;
import com.sf.kafka.api.produce.ProducerPool;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Created by 862911 on 2016/6/15.
 */
public class KafkaConnection {

    private final IKafkaProducer kafkaProducer;
    private final String topic;

    public KafkaConnection(int poolSize,String topic,String url, String clusterName, String topicTokens) {
        ProduceConfig produceConfig = new ProduceConfig(poolSize, url, clusterName, topicTokens);
        kafkaProducer = new ProducerPool(produceConfig);
        this.topic = topic;
    }

    public void send(String message) {
        kafkaProducer.sendString(topic, message);
    }
}
