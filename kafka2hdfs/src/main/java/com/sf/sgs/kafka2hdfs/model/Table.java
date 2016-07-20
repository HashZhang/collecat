/* 
 * Copyright (c) 2015, S.F. Express Inc. All rights reserved.
 */
package com.sf.sgs.kafka2hdfs.model;

import java.util.Map;


/**
 * 描述：
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
public class Table {

	// 库名
	private String storeName;
	// 系统名
	private String systemName;
	// 空数据分隔符
	private String nullSplit;
	// 表名
	private String tableName;
	// 表备注
	private String comment;
	// 文件名
	private String fileName;
	// 表后缀
	private String suffix;

	Map<Integer, Column> columnMap;

	Map<Integer, Integer> indexColumnMap;

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	public String getNullSplit() {
		return nullSplit;
	}

	public void setNullSplit(String nullSplit) {
		this.nullSplit = nullSplit;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Map<Integer, Column> getColumnMap() {
		return columnMap;
	}

	public void setColumnMap(Map<Integer, Column> columnMap) {
		this.columnMap = columnMap;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public Map<Integer, Integer> getIndexColumnMap() {
		return indexColumnMap;
	}

	public void setIndexColumnMap(Map<Integer, Integer> indexColumnMap) {
		this.indexColumnMap = indexColumnMap;
	}

}
