package com.sf.sgs.kafka2hdfs.consumer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sf.kafka.api.consume.IStringMessageConsumeListener;
import com.sf.kafka.api.consume.KafkaConsumeRetryException;
import com.sf.sgs.kafka2hdfs.handler.AbstractMessageHandler;
import com.sf.sgs.kafka2hdfs.model.Column;
import com.sf.sgs.kafka2hdfs.model.Table;
import com.sf.sgs.kafka2hdfs.util.HdfsConfigUtils;

/**
 * 
 * @author 833901
 *
 */
public abstract class AbstractKafkaConsumer extends AbstractMessageHandler implements IStringMessageConsumeListener {

	private static Logger logger = LoggerFactory.getLogger(AbstractKafkaConsumer.class);
	
	/**
	 * Kafka监听消息接口
	 */
	@Override
	public void onMessage(List<String> msgList) throws KafkaConsumeRetryException {
		handleMessages(msgList);
	}
	
	protected Map<String, List<String[]>> convertJsonArr(List<String> msgs) {
		Map<String, List<String[]>> datas = new HashMap<String, List<String[]>>();
		Table table = HdfsConfigUtils.instance().getTable(getTableName());
		if (table == null) {
			logger.warn("can't find table config: " + getTableName());
		}
		Map<Integer, Column> cols = table.getColumnMap();
		int colNum = cols.size();
		if (colNum == 0) {
			logger.warn("table must have 1 column at least -- " + getTableName());
		}
		String colName = null;
		int tArrSize = 0;
		JSONArray jsonArr = null;
		JSONObject jsonObj = null;
		String partitionColValue = null;
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		for (String msg : msgs) {
			jsonArr = JSON.parseArray(msg);
			tArrSize = jsonArr.size();
			for (int i = 0; i < tArrSize; i ++) {
				jsonObj = (JSONObject) jsonArr.get(i);
				partitionColValue = sdf.format(jsonObj.getDate(getPartitionColumn()));
				List<String[]> data = datas.get(partitionColValue);
				if (data == null) {
					data = new ArrayList<String[]>();
				}
				String[] row = new String[colNum];
				for (int j = 0; j < colNum; j ++) {
					colName = cols.get(j+1).getName();
					row[j] = jsonObj.getString(colName);
				}
				data.add(row);
				datas.put(partitionColValue, data);
			}
		}
		return datas;
	}

	protected Map<String, List<String[]>> convertJsonObj(List<String> msgs) {
		Map<String, List<String[]>> datas = new HashMap<String, List<String[]>>();
    	Table t = HdfsConfigUtils.instance().getTable(getTableName());
    	JSONObject jsonObj = null;
    	Map<Integer, Column> cols = t.getColumnMap();
    	int colNum = cols.size();
    	String colName = null;
    	String partitionColValue = null;
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    	for (String msg : msgs) {
    		jsonObj = JSON.parseObject(msg);
    		partitionColValue = sdf.format(jsonObj.getDate(getPartitionColumn()));
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
	
	protected List<String[]> convertCSV(List<String> msgs) {
		List<String[]> datas = new ArrayList<String[]>();
		
		return datas;
	}
}
