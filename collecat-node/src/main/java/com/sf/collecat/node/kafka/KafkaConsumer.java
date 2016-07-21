package com.sf.collecat.node.kafka;

import com.alibaba.dubbo.common.utils.ConcurrentHashSet;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sf.kafka.api.consume.ConsumeConfig;
import com.sf.kafka.api.consume.IStringMessageConsumeListener;
import com.sf.kafka.api.consume.KafkaConsumeRetryException;
import com.sf.kafka.api.consume.KafkaConsumerRegister;
import com.sf.kafka.exception.KafkaException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by 862911 on 2016/6/16.
 */
public class KafkaConsumer {
    public static void main(String[] args) throws KafkaException {
        // 消费的主题，即消息类型
        String topic = "COLLECAT_TEST";
//        String topic1 = "SGS_TT_DEL_ORD_CHILD";
//        String topic2 = "SGS_TT_DEL_ORD_CONSIGN";
//        String topic3 = "SGS_TT_DEL_ORD_FEE";
//        String topic4 = "SGS_TT_DEL_ORD_SERVICE";
//        String topic5 = "SGS_TT_EXP_TASK_INFO";
//        String topic6 = "SGS_TT_ORDER_STATUS";
//        String topic7 = "SGS_TT_REC_ORD";
        // 消费任务并发数
        int consumeThreadCount = 5;
        // KAFKA连接地址
        String url = "http://10.202.34.30:8292/mom-mon/monitor/requestService.pub";
        // 集群名称
        String clusterName = "other";
        // 消费系统名称+分隔符（固定不变）+消费系统的校验码
        String systemIdToken = "COLLECAT_TEST:dZi$*QS4";
//        String systemIdToken2 = "SGS_TT_DEL_ORD_CHILD:oOh^A**7";
//        String systemIdToken3 = "SGS_TT_DEL_ORD_CONSIGN:Z55PE@x3";
//        String systemIdToken4 = "SGS_TT_DEL_ORD_FEE:@kT!$mV3";
//        String systemIdToken5 = "SGS_TT_DEL_ORD_SERVICE:K0q*P2@$";
//        String systemIdToken6 = "SGS_TT_EXP_TASK_INFO:D0*!244h";
//        String systemIdToken7 = "SGS_TT_ORDER_STATUS:^23$!bJ8";
//        String systemIdToken8= "SGS_TT_REC_ORD:0vqT!*eT";
        ConsumeConfig consumeConfig = new ConsumeConfig(systemIdToken, url, clusterName, topic, consumeThreadCount);
//        ConsumeConfig consumeConfig2 = new ConsumeConfig(systemIdToken2, url, clusterName, topic1, consumeThreadCount);
//        ConsumeConfig consumeConfig3 = new ConsumeConfig(systemIdToken3, url, clusterName, topic2, consumeThreadCount);
//        ConsumeConfig consumeConfig4 = new ConsumeConfig(systemIdToken4, url, clusterName, topic3, consumeThreadCount);
//        ConsumeConfig consumeConfig5 = new ConsumeConfig(systemIdToken5, url, clusterName, topic4, consumeThreadCount);
//        ConsumeConfig consumeConfig6 = new ConsumeConfig(systemIdToken6, url, clusterName, topic5, consumeThreadCount);
//        ConsumeConfig consumeConfig7 = new ConsumeConfig(systemIdToken7, url, clusterName, topic6, consumeThreadCount);
//        ConsumeConfig consumeConfig8 = new ConsumeConfig(systemIdToken8, url, clusterName, topic7, consumeThreadCount);

        // 注册消费任务，String方式, 简易模式
        KafkaConsumerRegister.registerStringConsumer(consumeConfig, new PrintMessageListener(topic));
//        KafkaConsumerRegister.registerStringConsumer(consumeConfig2, new PrintMessageListener(topic1));
//        KafkaConsumerRegister.registerStringConsumer(consumeConfig3, new PrintMessageListener(topic2));
//        KafkaConsumerRegister.registerStringConsumer(consumeConfig4, new PrintMessageListener(topic3));
//        KafkaConsumerRegister.registerStringConsumer(consumeConfig5, new PrintMessageListener(topic4));
//        KafkaConsumerRegister.registerStringConsumer(consumeConfig6, new PrintMessageListener(topic5));
//        KafkaConsumerRegister.registerStringConsumer(consumeConfig7, new PrintMessageListener(topic6));
//        KafkaConsumerRegister.registerStringConsumer(consumeConfig8, new PrintMessageListener(topic7));
    }

    private static class PrintMessageListener implements IStringMessageConsumeListener {
        //        static BufferedWriter bufferedWriter = null;
//
//        static {
//            try {
//                FileWriter fileWriter = new FileWriter("D:/log.csv");
//                bufferedWriter = new BufferedWriter(fileWriter);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        int timecount = 0;
        int totalcount = 0;
        Set<String> stringSet = new ConcurrentHashSet<>();

        private String topic;

        private PrintMessageListener(String topic) {
            this.topic = topic;
        }


        public void onMessage(List<String> list) throws KafkaConsumeRetryException {
//            totalcount += list.size();
//            System.out.println(topic + "[" + ++timecount + ":" + totalcount + "]");
//            for (String string : list) {
//                JSONArray jsonArray = JSON.parseArray(string);
//                for (Object object : jsonArray) {
//                    stringSet.add((String) ((JSONObject) object).get("id"));
//                }
//            }
//            System.out.println(stringSet.size());
            System.out.println(list);
        }
    }
}
