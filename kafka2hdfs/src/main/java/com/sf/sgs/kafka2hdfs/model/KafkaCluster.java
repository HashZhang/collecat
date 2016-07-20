package com.sf.sgs.kafka2hdfs.model;

import java.util.Map;

public class KafkaCluster {
	
	private Integer id;
	
	private String name;
	
	private String systemUrl;
	
	private Integer messageGroupSize;
	
	private Integer consumerThreadCount;
	
	private Map<String, KafkaTopic> topics;
	
	private String consumer;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSystemUrl() {
		return systemUrl;
	}

	public void setSystemUrl(String systemUrl) {
		this.systemUrl = systemUrl;
	}

	public Integer getMessageGroupSize() {
		return messageGroupSize;
	}

	public void setMessageGroupSize(Integer messageGroupSize) {
		this.messageGroupSize = messageGroupSize;
	}

	public Integer getConsumerThreadCount() {
		return consumerThreadCount;
	}

	public void setConsumerThreadCount(Integer consumerThreadCount) {
		this.consumerThreadCount = consumerThreadCount;
	}

	public String getConsumer() {
		return consumer;
	}

	public void setConsumer(String consumer) {
		this.consumer = consumer;
	}

	public Map<String, KafkaTopic> getTopics() {
		return topics;
	}

	public void setTopics(Map<String, KafkaTopic> topics) {
		this.topics = topics;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

}
