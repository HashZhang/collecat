package com.sf.sgs.kafka2hdfs.consumer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sf.sgs.kafka2hdfs.model.Column;
import com.sf.sgs.kafka2hdfs.model.Table;
import com.sf.sgs.kafka2hdfs.util.HdfsConfigUtils;
import com.sf.sgs.kafka2hdfs.util.KafkaConfigUtils;

public class AppInfoMsgConsumer extends AbstractKafkaConsumer {
	
	private static Logger logger = LoggerFactory.getLogger(AppInfoMsgConsumer.class);

	private String tableName;
	
	private String dataType;
	
	public AppInfoMsgConsumer(String tableName, String dataType) {
		if (StringUtils.isEmpty(tableName) || StringUtils.isEmpty(dataType)) {
			logger.error("tableName and dataType can't be empty");
			throw new IllegalArgumentException("tableName and dataType can't be empty");
		}
		this.tableName = tableName;
		this.dataType = dataType;
	}
	
	@Override
	protected String getTableName() {
		return tableName;
	}

	@Override
	protected Map<String, List<String[]>> convertMesgs(List<String> mesgs) {
		Map<String, List<String[]>> datas = new HashMap<String, List<String[]>>();
    	Table t = HdfsConfigUtils.instance().getTable(getTableName());
    	JSONObject jsonObj = null;
    	Map<Integer, Column> cols = t.getColumnMap();
    	int colNum = cols.size();
    	String colName = null;
    	String partitionColValue = null;
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    	long partitionVal = 0l;
    	String partitionCol = getPartitionColumn();
    	for (String msg : mesgs) {
    		jsonObj = JSON.parseObject(msg);
    		partitionVal = jsonObj.getLong(partitionCol);
    		partitionColValue = sdf.format(new Date(partitionVal));
    		List<String[]> data = datas.get(partitionColValue);
    		if (data == null) {
    			data = new ArrayList<String[]>();
    		}
    		String[] row = new String[colNum];
    		for (int i = 0; i < colNum; i ++) {
    			colName = cols.get(i+1).getName();
    			row[i] = jsonObj.getString(colName);
    		}
    		data.add(row);
    		datas.put(partitionColValue, data);
    	}
    	return datas;
	}

	@Override
	protected String getPartitionColumn() {
		return KafkaConfigUtils.getInstance().getKafkaConfig().get(2).getTopics().get(tableName).getPartitionColumn();
	}

}
