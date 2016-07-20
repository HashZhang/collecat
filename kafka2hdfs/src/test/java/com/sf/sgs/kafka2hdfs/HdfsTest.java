package com.sf.sgs.kafka2hdfs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.sf.sgs.kafka2hdfs.consumer.AppInfoMsgConsumer;
import com.sf.sgs.kafka2hdfs.consumer.CollecateSysBaseMsgConsumer;
import com.sf.sgs.kafka2hdfs.model.KafkaTopic;

public class HdfsTest {
	
	private List<String> initData() {
		String msg1 = "{\"zoneCode\":\"769AG\",\"waybillNo\":\"662804140666\",\"userName\":\"833901\",\"startTime\":1464771894957,\"completeTime\":1464771897958}";
		String msg2 = "{\"zoneCode\":\"769AG\",\"waybillNo\":\"664467978081\",\"userName\":\"833901\",\"startTime\":1464771908068,\"completeTime\":1464771915726}";
		String msg3 = "{\"zoneCode\":\"769AG\",\"waybillNo\":\"923687345317\",\"userName\":\"833901\",\"startTime\":1464771926960,\"completeTime\":1464771930321}";
		String msg4 = "{\"zoneCode\":\"769AG\",\"waybillNo\":\"664311741771\",\"userName\":\"833901\",\"startTime\":1464771942019,\"completeTime\":1464771950062}";
		List<String> kafkaMsg = new ArrayList<String>();
		kafkaMsg.add(msg1);
		kafkaMsg.add(msg2);
		kafkaMsg.add(msg3);
		kafkaMsg.add(msg4);
		return kafkaMsg;
	}
	
	@Test
	public void testWriteAppInfo() {
		List<String> msgs = initData();
		AppInfoMsgConsumer appInfoMsgConsumer = new AppInfoMsgConsumer("ops_app_stat_info",KafkaTopic.TYPE_JSON);
		boolean isOk = appInfoMsgConsumer.handleMessages(msgs);
		Assert.assertTrue(isOk);
	}
	
	@Test
	public void testWriteExpTaskInfo() throws IOException {
		List<String> msgs = new ArrayList<String>();
		InputStream inStream = getClass().getResourceAsStream("/test-data.txt");
		InputStreamReader inReader = new InputStreamReader(inStream);
		BufferedReader bufReader = new BufferedReader(inReader);
		String lineStr = null;
		while ( (lineStr = bufReader.readLine()) != null ) {
			msgs.add(lineStr);
		}
		CollecateSysBaseMsgConsumer consumer = new CollecateSysBaseMsgConsumer("exp_tt_express_task_info", KafkaTopic.TYPE_JSON_ARR);
		consumer.handleMessages(msgs);
	}
	
}
