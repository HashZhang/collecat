package com.sf.sgs.kafka2hdfs.consumer;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sf.sgs.kafka2hdfs.model.KafkaTopic;
import com.sf.sgs.kafka2hdfs.util.KafkaConfigUtils;

public class CollecateSysBaseMsgConsumer extends AbstractKafkaConsumer {

	private static Logger logger = LoggerFactory.getLogger(CollecateSysBaseMsgConsumer.class);
	
	private String tableName = null;
	private String dataType = null;
	
	public CollecateSysBaseMsgConsumer(String tableName, String dataType) {
		if (StringUtils.isEmpty(tableName) || StringUtils.isEmpty(dataType)) {
			logger.error("tableName and dataType can't be empty");
			throw new IllegalArgumentException("tableName and dataType can't be empty");
		}
		this.tableName = tableName;
		this.dataType = dataType;
	}
	
	@Override
	protected String getTableName() {
		return this.tableName;
	}

	@Override
	protected Map<String, List<String[]>> convertMesgs(List<String> msgs) {
		if (KafkaTopic.TYPE_JSON_ARR.equalsIgnoreCase(this.dataType)) {
			return convertJsonArr(msgs);
		} else if (KafkaTopic.TYPE_JSON.equalsIgnoreCase(this.dataType)) {
			return convertJsonObj(msgs);
		} else if (KafkaTopic.TYPE_CSV.equalsIgnoreCase(this.dataType)) {
			return null;
		} else {
			logger.warn("unsupportted data type-->"+this.dataType);
			return null;
		}
	}

	@Override
	protected String getPartitionColumn() {
		return KafkaConfigUtils.getInstance().getKafkaConfig().get(1).getTopics().get(tableName).getPartitionColumn();
	}
}
