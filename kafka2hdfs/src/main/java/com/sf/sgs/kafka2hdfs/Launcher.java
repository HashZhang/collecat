package com.sf.sgs.kafka2hdfs;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sf.kafka.api.consume.ConsumeConfig;
import com.sf.kafka.api.consume.ConsumeOptionalConfig;
import com.sf.kafka.api.consume.ConsumeOptionalConfig.AutoOffsetReset;
import com.sf.kafka.api.consume.KafkaConsumerRegister;
import com.sf.kafka.exception.KafkaException;
import com.sf.sgs.kafka2hdfs.consumer.AppInfoMsgConsumer;
import com.sf.sgs.kafka2hdfs.fix.HdfsDataFixExecutor;
import com.sf.sgs.kafka2hdfs.model.KafkaCluster;
import com.sf.sgs.kafka2hdfs.model.KafkaTopic;
import com.sf.sgs.kafka2hdfs.util.HdfsConfigUtils;
import com.sf.sgs.kafka2hdfs.util.KafkaConfigUtils;

/**
 * kafka消费者启动类
 * 
 * @author 833901
 *
 */
public class Launcher {

	private static Logger logger = LoggerFactory.getLogger(Launcher.class);

	public static final Launcher instance = new Launcher();

	private Launcher() {
	}

	public static void main(String[] args) {
		try {
			HdfsConfigUtils.instance();
			Launcher.instance.start();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	private void start() throws Exception {
		initKafka();
		new HdfsDataFixExecutor().start();
	}

	private void initKafka() throws KafkaException {

		logger.info("###### start initialize kafka configuration ######");
		System.setProperty("zookeeper.sasl.client", "false");
		
		Map<Integer, KafkaCluster> kafkaConfigs = KafkaConfigUtils.getInstance().getKafkaConfig();
		
		KafkaCluster kafkaCluster = kafkaConfigs.get(2);
		ConsumeOptionalConfig consumeOptionalConfig = new ConsumeOptionalConfig();
		consumeOptionalConfig.setMessageGroupSize(kafkaCluster.getMessageGroupSize());
		consumeOptionalConfig.setAutoOffsetReset(AutoOffsetReset.BEGIN);

		String clusterName = kafkaCluster.getName();
		Integer consumeThreadCount = kafkaCluster.getConsumerThreadCount();
		String systemUrl = kafkaCluster.getSystemUrl();
		String systemIdToken = kafkaCluster.getConsumer();
		Map<String, KafkaTopic> topics = kafkaCluster.getTopics();
		for (KafkaTopic topic : topics.values()) {
			ConsumeConfig consumeConfig = new ConsumeConfig(systemIdToken, systemUrl, clusterName, topic.getTopicName(),
					consumeThreadCount);
			AppInfoMsgConsumer consumer = new AppInfoMsgConsumer(topic.getTableName(),
					topic.getDataType());
			KafkaConsumerRegister.registerStringConsumer(consumeConfig, consumer, consumeOptionalConfig);
		}

		logger.info("###### initialize kafka configuration success ######");
	}

}
