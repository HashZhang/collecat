package com.sf.sgs.kafka2hdfs.model;

public class KafkaTopic {
	
	public static final String TYPE_JSON_ARR = "JSON_ARR";
	
	public static final String TYPE_JSON = "JSON_OBJ";
	
	public static final String TYPE_CSV = "CSV";

	private String topicName;
	
	private String tableName;
	
	private String partitionColumn;
	
	private String dataType;
	
	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getPartitionColumn() {
		return partitionColumn;
	}

	public void setPartitionColumn(String partitionColumn) {
		this.partitionColumn = partitionColumn;
	}

}
