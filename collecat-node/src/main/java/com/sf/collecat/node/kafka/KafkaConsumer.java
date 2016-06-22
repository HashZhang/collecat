package com.sf.collecat.node.kafka;

import com.sf.kafka.api.consume.ConsumeConfig;
import com.sf.kafka.api.consume.IStringMessageConsumeListener;
import com.sf.kafka.api.consume.KafkaConsumeRetryException;
import com.sf.kafka.api.consume.KafkaConsumerRegister;
import com.sf.kafka.exception.KafkaException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by 862911 on 2016/6/16.
 */
public class KafkaConsumer {
    public static void main(String[] args) throws KafkaException {
        // 消费的主题，即消息类型
        String topic = "COLLECAT_TEST";
        // 消费任务并发数
        int consumeThreadCount = 5;
        // KAFKA连接地址
        String url = "http://10.202.34.30:8292/mom-mon/monitor/requestService.pub";
        // 集群名称
        String clusterName = "other";
        // 消费系统名称+分隔符（固定不变）+消费系统的校验码
        String systemIdToken = "COLLECAT_TEST:dZi$*QS4";
        ConsumeConfig consumeConfig = new ConsumeConfig(systemIdToken, url, clusterName, topic, consumeThreadCount);

        // 注册消费任务，String方式, 简易模式
        KafkaConsumerRegister.registerStringConsumer(consumeConfig, new PrintMessageListener());

//        ThreadUtil.sleep(10000);

//        KafkaConsumerRegister.unregister(topic);

//        // 注册消费任务，String方式, 个性化参数模式
//        ConsumeOptionalConfig optionalConfig = new ConsumeOptionalConfig();
//        optionalConfig.setAutoOffsetResetMinute(80);
//        optionalConfig.setAutoOffsetReset(AutoOffsetReset.CUSTOM);
//        optionalConfig.setConsumeExceptionHandler(new IConsumeExceptionHandler() {
//
//			@Override
//			public boolean accept(Throwable e, int calcCount) {
//				// write logic code
//				return false;
//			}
//		});
//        KafkaConsumerRegister.registerStringConsumer(consumeConfig, new PrintMessageListener(), optionalConfig);

//        // 注册消费任务，byte[]方式, 简易模式
//        KafkaConsumerRegister.registerByteArrayConsumer(consumeConfig, new PrintByteArrayMessageListener());

//        // 注册消费任务，byte[]方式, 个性化参数模式
//        ConsumeOptionalConfig optionalConfig = new ConsumeOptionalConfig();
//        optionalConfig.setMessageGroupSize(20);
//        optionalConfig.setAutoOffsetReset(AutoOffsetReset.NOW);
//        KafkaConsumerRegister.registerByteArrayConsumer(consumeConfig, new PrintByteArrayMessageListener(),optionalConfig);
    }

    private static class PrintMessageListener implements IStringMessageConsumeListener {
        static BufferedWriter bufferedWriter = null;

        static {
            try {
                FileWriter fileWriter = new FileWriter("D:/log.csv");
                bufferedWriter = new BufferedWriter(fileWriter);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void onMessage(List<String> list) throws KafkaConsumeRetryException {
            try {
                for (String data : list) {
                    bufferedWriter.write(data);
                    System.out.println(data);
                }
                bufferedWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
