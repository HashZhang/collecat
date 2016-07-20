/* 
 * Copyright (c) 2015, S.F. Express Inc. All rights reserved.
 */
package com.sf.sgs.kafka2hdfs.util;

import java.io.File;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sf.sgs.kafka2hdfs.exception.HdfsException;
import com.sf.sgs.kafka2hdfs.model.Column;
import com.sf.sgs.kafka2hdfs.model.Table;


/**
 * 描述：hdfs 配置管理类，加载配置文件
 * 
 * <pre>
 * HISTORY
 * ****************************************************************************
 *  ID   DATE           	PERSON          REASON
 *  1    2015年10月20日	     461246        Create
 * ****************************************************************************
 * </pre>
 * 
 * @author 461246
 * @since 1.0
 */
public class HdfsConfigUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(HdfsConfigUtils.class);
	/**
	 * 配置文件映射map
	 */
	private volatile Map<String, String> configs;
	
	private volatile Map<String , Table> tableMap;
	/**
	 * 服务器的索引
	 */
	private volatile String serverIndex;
	
	private volatile Configuration configuration;
	
	private static HdfsConfigUtils instance = new HdfsConfigUtils();
	
	private HdfsConfigUtils() {
		loadFromDefaultProperties();
	}
	
	public static HdfsConfigUtils instance() {
		return instance;
	}
	
	/**
	 * 加载默认的配置文件
	 */
	private void loadFromDefaultProperties() {
		InputStream ins = this.getClass().getResourceAsStream("/hdfs.properties");
		if (ins == null) {
			logger.warn("not found default config file: hdfs.properties");
			return;
		}
        try {  
        	Properties p = new Properties(); 
            p.load(ins);
			if (p.isEmpty()) {
				return;
			}
			configs = new HashMap<String, String>();
            Set<Entry<Object, Object>> entrySet = p.entrySet();
			for (Entry<Object, Object> entry : entrySet) {
				configs.put((String) entry.getKey(), (String) entry.getValue());
			}
        } catch (Exception e) {  
        	logger.warn(e.getMessage(), e);
        }  
	}
	
	
	
	/**
	 * 解析表的配置
	 * 
	 * @param hdfsTtableName
	 * @return
	 */
	private Map<String , Table> parseTableConfig() {

		Map<String , Table> tableMap = new HashMap<String,Table>();

		Table table = null;

		NodeList nodeList = null;
		try {
			nodeList = getTableNodeList(this.get(HdfsConstants.HDFS_FILE_PATH));
		} catch (Exception e) {
			logger.error("parse xml error" + e.getMessage(), e);
			throw new HdfsException("parse xml error");
		} 

		if (nodeList == null) {
			logger.error("not found table config...");
			return null;
		}

		int length = nodeList.getLength();
		for (int i = 0; i < length; i++) {
			Node item = nodeList.item(i);
			// 查找用户传入的表的配置
			table = new Table();
			NodeList childNodes = item.getChildNodes();
			Node node = item.getParentNode();
			table.setStoreName(getNodeValue(node, HdfsConstants.STORE_NAME));// 获取库名
			table.setSystemName(getNodeValue(node, HdfsConstants.SYSTEM_NAME));// 获取系统名
			table.setNullSplit(getNodeValue(node, HdfsConstants.NULL_SPLIT));// 空值的分隔符
			table.setTableName(getNodeValue(item, HdfsConstants.TABLE_NAME));// 表名
//			table.setComment(getNodeValue(item, HdfsConstants.COMMENT));// 表描述
			table.setSuffix(getNodeValue(item, HdfsConstants.SUFFIX));// 文件后缀
			table.setFileName(getNodeValue(item, HdfsConstants.FILE_NAME));// 文件名
			
			// 文件名
			Map<Integer, Column> columnMap = new TreeMap<Integer, Column>();
			Map<Integer, Integer> indexColumnMap = new TreeMap<Integer, Integer>();
			for (int j = 0, size1 = childNodes.getLength(); j < size1; j++) {
				if (childNodes.item(j).hasAttributes()) {
					// 表字段的信息
					Column column = new Column();
//					column.setComment(getNodeValue(childNodes.item(j),HdfsConstants.COMMENT));// 表字段的备注
//					column.setType(getNodeValue(childNodes.item(j), HdfsConstants.TYPE));// 表字段的类型
					column.setName(getNodeValue(childNodes.item(j), HdfsConstants.NAME));// 表字段的名称
					columnMap.put(Integer.valueOf(getNodeValue(childNodes.item(j), HdfsConstants.VALUE)), column);// 表字段的位置
					// 数组下标和表字段位置的对应关系
					int value = Integer.valueOf(getNodeValue(childNodes.item(j), HdfsConstants.VALUE));// 表字段的位置
					int index = Integer.valueOf(getNodeValue(childNodes.item(j), HdfsConstants.INDEX));// 数组下标的位置
					indexColumnMap.put(value, index);
				}
			}
			table.setColumnMap(columnMap); // 字段的信息
			table.setIndexColumnMap(indexColumnMap); // 下标对应字段的信息
			tableMap.put(getNodeValue(item, HdfsConstants.TABLE_NAME), table);
		}

		return tableMap;
	}
	
	/**
	 * 获取属性值
	 * 
	 * @param node
	 * @param name
	 * @return
	 */
	private String getNodeValue(Node node, String name) {
		if (node == null || name == null) {
			return null;
		}

		Node nodeChild = node.getAttributes().getNamedItem(name);

		if (nodeChild == null) {
			return null;
		}

		return nodeChild.getNodeValue();
	}
	
	/**
	 * 得到表节点的集合
	 * 
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	private NodeList getTableNodeList(String filePath)
			throws Exception {
		if (StringUtils.isEmpty(filePath)) {
			throw new HdfsException("hdfsFilePath is null");
		}
		URL resource = getClass().getResource(filePath);
		File file = new File(resource.getFile());
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
		Document document = documentBuilder.parse(file);
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		// 直接从table的节点开始找
		XPathExpression expression = xpath.compile("config/dataStore/table");
		NodeList nodeList = (NodeList) expression.evaluate(document,
				XPathConstants.NODESET);
		return nodeList;
	}
	
	/**
	 * 如果项目有自己的配置文件加载方式，可以调用本方法覆盖配置
	 * @param privateMapping
	 */
	public void replaceConfigMapping(Map<String, String> privateMapping) {
		if (privateMapping == null || privateMapping.isEmpty()) {
			return;
		}
		configs = new HashMap<String, String>(privateMapping);
	}
	
	/**
	 * 根据 key 获取
	 * @param key
	 * @return
	 */
	public String get(String key) {
		if (key == null ) {
			return null;
		}
		if (configs == null) {
			logger.error("config mapping is null");
			return null;
		}
		return configs.get(key);
	}
	
	public String getRootUri() {
		if (configs == null) {
			logger.error("config mapping is null");
			return null;
		}
		if ("true".equals(this.get("hdfs.ha"))) {
			return getConfiguration().get("fs.defaultFS") + get(HdfsConstants.HDFS_DATA_ADDR);
		} else {
			return get(HdfsConstants.HDFS_DATA_ADDR);
		}
	}
	
	public Configuration getConfiguration() {
		if (configuration == null) {
			synchronized (instance) {
				if ("true".equals(this.get("hdfs.ha"))) {//高可用模式
					Configuration cfg = new Configuration(true);
					String strArray = this.get("hdfs.ha.filePath");
					if (StringUtils.isBlank(strArray)) {
						throw new HdfsException("hdfs.ha.url is blank");
					}
					for (String str : strArray.split(",")) {//加载配置文件
						cfg.addResource(str);
					}
					configuration = cfg;
				} else {
					Configuration cfg = new Configuration(false);
					cfg.setBoolean("fs.hdfs.impl.disable.cache", true);
					cfg.setBoolean("dfs.support.append", true);
					configuration = cfg;
				}
				//设置写hdfs的用户
				String hdfsUserName = this.get("hdfs.user.name");
				if (!StringUtils.isBlank(hdfsUserName)) {
					logger.info("set HADOOP_USER_NAME to {}", hdfsUserName);
					System.setProperty("HADOOP_USER_NAME", hdfsUserName);
				}
			}
		}
		return configuration;
	}
	
	/**
	 * 获取表的配置信息
	 * @param tableName
	 * @return
	 */
	public Table getTable(String tableName){
		if (StringUtils.isEmpty(tableName)) {
			return null;
		}
		if (tableMap == null) {
			synchronized (instance) {
				if (tableMap == null) {
					tableMap = this.parseTableConfig();
				}
			}
		}
		return tableMap.get(tableName);
	}
	
	public List<Table> getAllTable() {
		if (tableMap == null) {
			synchronized (instance) {
				if (tableMap == null) {
					tableMap = this.parseTableConfig();
				}
			}
		}
		return tableMap == null ? null : new ArrayList<Table>(tableMap.values());
	}
	
	/**
	 * 获取服务的索引名
	 * @return
	 */
	public String getServerIndex() {
		if (this.serverIndex == null) {
			synchronized (instance) {
				initServerIndex();
			}
		}
		return this.serverIndex;
	}
	
	/**
	 * 获取ip的映射
	 * @return
	 */
	private void initServerIndex() {
		//获取ip映射
		Map<String, String> maps = new HashMap<String, String>();
		String serverIndexMapping = this.get("hdfs.serverIPMapping");
		if (StringUtils.isBlank(serverIndexMapping)) {
			logger.error("hdfs.serverIPMapping is null");
			throw new HdfsException("hdfs.serverIPMapping is null");
		}
		String[] ipMaps = serverIndexMapping.split(",");
		for (String ipMap : ipMaps) {
			String[] map = ipMap.split("-");
			maps.put(map[0], map[1]);
		}
		String ipAddress = "";
		try {
			ipAddress = InetAddress.getLocalHost().getHostAddress();
			logger.info("ip:{}", ipAddress);
		} catch (UnknownHostException e) {
			logger.error("Unknown Host "+e.getMessage(), e);
		}
		if (maps.containsKey(ipAddress)) {
			this.serverIndex = maps.get(ipAddress);
		}
		if (this.serverIndex == null) {
			logger.error("serverIndex is null, ip:"+ipAddress);
			throw new HdfsException("serverIndex is null, ip:"+ipAddress);
		}
		//设置写hdfs的用户
		String hdfsUserName = this.get("hdfs.user.name");
		if (!StringUtils.isBlank(hdfsUserName)) {
			logger.info("set HADOOP_USER_NAME to {}", hdfsUserName);
			System.setProperty("HADOOP_USER_NAME", hdfsUserName);
		}
	}

}
