package com.sf.sgs.kafka2hdfs.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sf.sgs.kafka2hdfs.exception.ConfigException;
import com.sf.sgs.kafka2hdfs.model.KafkaCluster;
import com.sf.sgs.kafka2hdfs.model.KafkaTopic;

public class KafkaConfigUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(KafkaConfigUtils.class);
	
	/**
	 * 配置文件映射map
	 */
    private static KafkaConfigUtils instance = new KafkaConfigUtils();
    
    private Map<Integer, KafkaCluster> kafkaClusters = null;
    
    private KafkaConfigUtils() {
    	try {
			loadConfig();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public static KafkaConfigUtils getInstance() {
    	return instance;
    }
    
    public Map<Integer, KafkaCluster> getKafkaConfig() {
    	return kafkaClusters;
    }

    private void loadConfig() throws Exception  {
    	kafkaClusters = new HashMap<Integer, KafkaCluster>();
    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    	DocumentBuilder builder = factory.newDocumentBuilder();
    	InputStream xmlIs = this.getClass().getResourceAsStream("/kafkaConfig.xml");
    	Document doc = builder.parse(xmlIs);
    	NodeList clusterNodeList = doc.getElementsByTagName("cluster");
    	int clusterNum = clusterNodeList.getLength();
    	for (int i = 0; i < clusterNum; i ++) {
    		Element clusterElement = (Element)clusterNodeList.item(i);
    		KafkaCluster cluster = new KafkaCluster();
    		cluster.setName(clusterElement.getAttribute("name"));
    		cluster.setId(Integer.valueOf(clusterElement.getAttribute("id")));
    		// 1.读取properties配置
    		loadProperties(clusterElement, cluster);
    		// 2.读取topic配置
    		loadTopics(clusterElement, cluster);
    		// 3.读取consumer配置
    		loadConsumers(clusterElement, cluster);
    		kafkaClusters.put(cluster.getId(), cluster);
    	}
	}

	private void loadProperties(Element clusterElement, KafkaCluster cluster) {
		NodeList propertiesNodeList = clusterElement.getElementsByTagName("properties");
		if (propertiesNodeList != null && propertiesNodeList.getLength() == 1) {
			Element propertiesElement = (Element)propertiesNodeList.item(0);
			NodeList propertyNodeList = propertiesElement.getElementsByTagName("property");
			int propertyNum = propertyNodeList.getLength();
			for (int j = 0; j < propertyNum; j ++) {
				Element propertyElement = (Element) propertyNodeList.item(j);
				String propertyName = propertyElement.getAttribute("name");
				String propertyValue = propertyElement.getTextContent();
				if ("systemUrl".equals(propertyName)) {
					cluster.setSystemUrl(propertyValue);
				} else if ("messageGroupSize".equals(propertyName)) {
					cluster.setMessageGroupSize(Integer.valueOf(propertyValue));
				} else if ("consumeThreadCount".equals(propertyName)) {
					cluster.setConsumerThreadCount(Integer.valueOf(propertyValue));
				} else {
					logger.error("unsupported propertiy:"+propertyName);
					throw new ConfigException("unsupported propertiy:"+propertyName);
				}
			}
		}
    }
    
    private void loadTopics(Element clusterElement, KafkaCluster cluster) {
    	Map<String, KafkaTopic> topicMap = new HashMap<String, KafkaTopic>();
    	NodeList topicsNodeList = clusterElement.getElementsByTagName("topics");
    	if (topicsNodeList!=null && topicsNodeList.getLength() ==1 ) {
    		Element topicsElement = (Element)topicsNodeList.item(0);
    		NodeList topicNodeList = topicsElement.getElementsByTagName("topic");
    		int topicNum = topicNodeList.getLength();
    		for (int i = 0; i < topicNum; i ++) {
    			Element topicElement = (Element) topicNodeList.item(i);
    			KafkaTopic topic = new KafkaTopic();
    			String topicName = topicElement.getAttribute("name");
    			topic.setTopicName(topicName);
    			String tableName = topicElement.getElementsByTagName("tableName").item(0).getTextContent();
    			topic.setTableName(tableName);
    			String partitionColumn = topicElement.getElementsByTagName("partitionCol").item(0).getTextContent();
    			topic.setPartitionColumn(partitionColumn);
    			String dataType = topicElement.getElementsByTagName("dataType").item(0).getTextContent();
    			topic.setDataType(dataType);
    			topicMap.put(tableName, topic);
    		}
    		cluster.setTopics(topicMap);
    	}
	}
    
    private void loadConsumers(Element clusterElement, KafkaCluster cluster) {
    	NodeList consumersNodeList = clusterElement.getElementsByTagName("consumers");
    	if ( consumersNodeList != null && consumersNodeList.getLength() ==1) {
    		Element consumersElement = (Element) consumersNodeList.item(0);
    		NodeList consumerNodeList = consumersElement.getElementsByTagName("consumer");
    		String token = consumerNodeList.item(0).getTextContent();
    		cluster.setConsumer(token);
    	}
    }
    
}
