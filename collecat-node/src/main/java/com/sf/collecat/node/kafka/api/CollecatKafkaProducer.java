package com.sf.collecat.node.kafka.api;

import com.sf.kafka.api.produce.BaseProducer;
import com.sf.kafka.api.produce.KafkaProducer;
import com.sf.kafka.api.produce.KeyModPartitioner;
import com.sf.kafka.api.produce.ProduceOptionalConfig;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import kafka.serializer.DefaultEncoder;
import kafka.serializer.StringEncoder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * Created by 862911 on 2016/6/27.
 */
public class CollecatKafkaProducer extends BaseProducer {
    private Producer<String, byte[]> producer;
    private CollecatProduceOptionalConfig extraConfig;
    private long partKey = 0L;

    public CollecatKafkaProducer(String brokers, CollecatProduceOptionalConfig extraConfig) {
        this.extraConfig = extraConfig == null?CollecatProduceOptionalConfig.defaultConfig:extraConfig;
        Properties props = new Properties();
        props.put("metadata.broker.list", brokers);
        props.put("serializer.class", DefaultEncoder.class.getName());
        props.put("key.serializer.class", StringEncoder.class.getName());
        props.put("partitioner.class", KeyModPartitioner.class.getName());
        props.put("request.required.acks", String.valueOf(this.extraConfig.getRequestRequiredAck().getValue()));
        props.put("request.timeout.ms", String.valueOf(this.extraConfig.getRequestTimeoutMs()));
        props.put("compression.codec", "snappy");
        this.producer = new Producer(new ProducerConfig(props));
    }

    public void sendBytes(String topic, byte[] message) {
        long partKey = this.randomPartKey();
        this.producer.send(new KeyedMessage(topic, (Object)null, Long.valueOf(partKey), message));
    }

    public void batchSendBytes(String topic, List<byte[]> messages) {
        ArrayList keyedMessages = new ArrayList();
        long partKey = this.randomPartKey();
        Iterator i$ = messages.iterator();

        while(i$.hasNext()) {
            byte[] message = (byte[])i$.next();
            keyedMessages.add(new KeyedMessage(topic, (Object)null, Long.valueOf(partKey), message));
        }

        this.producer.send(keyedMessages);
    }

    public void batchSendBytes(String topic, List<byte[]> messages,List<Long> ids) {
        ArrayList keyedMessages = new ArrayList();
        Iterator i$ = messages.iterator();
        int count = 0;
        while(i$.hasNext()) {
            byte[] message = (byte[])i$.next();
            keyedMessages.add(new KeyedMessage(topic, (Object)null, ids.get(count), message));
            count ++;
        }

        this.producer.send(keyedMessages);
    }

    private long randomPartKey() {
        return (long)(this.partKey++);
    }

    public void close() {
        this.producer.close();
    }
}
