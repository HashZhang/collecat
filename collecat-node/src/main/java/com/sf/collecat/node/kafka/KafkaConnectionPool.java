package com.sf.collecat.node.kafka;

import com.sf.collecat.common.model.Job;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 862911 on 2016/6/15.
 */
@Component
public class KafkaConnectionPool {
    @Value("${kafka.connection.poolsize}")
    private int poolSize;

    private final ConcurrentHashMap<String,KafkaConnection> connMap = new ConcurrentHashMap<>();

    public synchronized KafkaConnection getKafkaConnection(Job job){
        String key = toKey(job);
        if(connMap.contains(key)){
            if(key!=null){
                return connMap.get(key);
            } else{
                KafkaConnection kafkaConnection = new KafkaConnection(poolSize,job.getKafkaTopic(),job.getKafkaUrl(),job.getKafkaClusterName(),job.getKafkaTopicTokens());
                connMap.put(key,kafkaConnection);
                return kafkaConnection;
            }
        } else{
            KafkaConnection kafkaConnection = new KafkaConnection(poolSize,job.getKafkaTopic(),job.getKafkaUrl(),job.getKafkaClusterName(),job.getKafkaTopicTokens());
            connMap.put(key,kafkaConnection);
            return kafkaConnection;
        }
    }

    private String toKey(Job job){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(job.getKafkaUrl()).append(job.getKafkaTopicTokens()).append(job.getKafkaClusterName());
        return stringBuilder.toString();
    }
}
