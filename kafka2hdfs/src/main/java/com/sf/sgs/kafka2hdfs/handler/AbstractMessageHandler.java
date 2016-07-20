package com.sf.sgs.kafka2hdfs.handler;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sf.sgs.kafka2hdfs.hdfs.AbstractBaseHdfsWriter;

/**
 * KAFKA消息处理抽象类
 * @author 833901
 *
 */
public abstract class AbstractMessageHandler extends AbstractBaseHdfsWriter{
	
	private static Logger logger = LoggerFactory.getLogger(AbstractMessageHandler.class);
    
	/**
	 * 处理Kafka推送过来的消息
	 * @param mesgs
	 * @return
	 */
    public boolean handleMessages(List<String> msgs) {
    	Map<String, List<String[]>> datas = convertMesgs(msgs);
    	String tableName = getTableName();
    	if (StringUtils.isEmpty(tableName)) {
    		logger.error("tableName can't be null or empty!");
    		return false;
    	}
    	try {
    		for (String day : datas.keySet()) {
    			write(tableName, day, datas.get(day));
    		}
    		return true;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
        return false;
    }

    /**
     * 返回当前要处理的数据所在表的表名
     * @return
     */
	abstract protected String getTableName();

	/**
	 * 将Kafka中消费的消息格式进行转换。<br>
	 * 每一个String[]为数据库表中一行数据各个列值的字符串数组，数组元素顺序与建表的列顺序一致。<br>
	 * Map中的key表示数据记录所在的时间分区，如：20160507
	 * 每次处理的消息为若干行记录的集合。
	 * @param mesgs
	 * @return
	 */
	abstract protected Map<String,List<String[]>> convertMesgs(List<String> mesgs);
	
	/**
	 * 返回这个表是根据哪个时间字段进行分区的
	 * @return
	 */
	abstract protected String getPartitionColumn();

}
