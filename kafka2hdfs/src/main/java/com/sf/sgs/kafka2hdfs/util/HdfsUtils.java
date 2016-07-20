/* 
 * Copyright (c) 2015, S.F. Express Inc. All rights reserved.
 */
package com.sf.sgs.kafka2hdfs.util;

import java.io.BufferedReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sf.sgs.kafka2hdfs.model.Table;


/**
 * 描述：
 * 
 * <pre>
 * HISTORY
 * ****************************************************************************
 *  ID   DATE           	PERSON          REASON
 *  1    2015年10月21日	     461246        Create
 * ****************************************************************************
 * </pre>
 * 
 * @author 461246
 * @since 1.0
 */
public class HdfsUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(HdfsUtils.class);
	
	/**
	 * 判断对象值是否为空，不空时返回对象值, 否则返回""
	 * 
	 * @param obj
	 * @return
	 */
	public static String stringformatVal(String obj) {
		return StringUtils.isNotBlank(obj) ? obj : "" ;
	}

	public static String intToStringVal(Integer obj) {
		return null == obj ? "" : String.valueOf(obj);
	}

	public static String longToStringVal(Long obj) {
		return null == obj ? "" : String.valueOf(obj);
	}

	public static String doubleToStringVal(Double obj) {
		return null == obj ? "" : String.valueOf(obj);
	}
	
	/**
	 * 获取要写入的文件全名，包括路径
	 * @param table
	 * @param serverIndex
	 * @return
	 */
	public static String getFullFileName(Table table, String serverIndex,
			String middleFilePath) {
		StringBuffer sb = new StringBuffer();
		sb.append(HdfsConfigUtils.instance().getRootUri());
		//sb.append(table.getTableName()).append(HdfsConstants.HDFS_PATH_REG);
		sb.append(table.getTableName().toUpperCase()).append(HdfsConstants.HDFS_PATH_REG);
		sb.append(middleFilePath).append(HdfsConstants.HDFS_PATH_REG);
		sb.append(table.getFileName()).append("_");
		sb.append(serverIndex).append("_");
		sb.append(Thread.currentThread().getId()).append("_");
		sb.append(System.currentTimeMillis()).append(HdfsConstants.FILE_TMP_SUFFIX);
		return sb.toString();
	}
	
	/**
	 * 获取要写入的文件全名，包括路径
	 * @param table
	 * @param serverIndex
	 * @return
	 */
	public static String getFixFullFileName(Table table, String serverIndex,
			String middleFilePath) {
		StringBuffer sb = new StringBuffer();
		sb.append(HdfsConfigUtils.instance().getRootUri());
		sb.append(table.getTableName().toUpperCase()).append(HdfsConstants.HDFS_PATH_REG);
		sb.append(middleFilePath).append(HdfsConstants.HDFS_PATH_REG);
		sb.append(table.getFileName()).append("_");
		sb.append(serverIndex).append("_");
		sb.append(Thread.currentThread().getId()).append("_");
		sb.append(System.currentTimeMillis()).append(HdfsConstants.FILE_FIX_SUFFIX);
		return sb.toString();
	}
	
	/**
	 * 获取要修复的目录
	 * @param table
	 * @param middleFilePath
	 * @return
	 */
	public static String getFixDirectory(Table table, String middleFilePath) {
		StringBuffer sb = new StringBuffer();
		sb.append(HdfsConfigUtils.instance().getRootUri());
		sb.append(table.getTableName().toUpperCase()).append(HdfsConstants.HDFS_PATH_REG);
		sb.append(middleFilePath);
		return sb.toString();
	}
	
	
	public static void close(FileSystem fileSystem) {
		try {
			if (fileSystem != null) {
				fileSystem.close();
			}
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
		}
	}
	
	public static void close(FSDataOutputStream outputStream) {
		try {
			if (outputStream != null) {
				outputStream.close();
			}
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
		}
	}
	
	public static void close(FSDataInputStream inputStream) {
		try {
			if (inputStream != null) {
				inputStream.close();
			}
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
		}
	}
	
	public static void close(BufferedReader bufferedReader) {
		try {
			if (bufferedReader != null) {
				bufferedReader.close();
			}
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
		}
	}
	
	/**
	 * 返回当前时间的 yyyyMMdd 格式 ，如20151020
	 * @return
	 */
	public static String formatDate(Date date) {
		String format = "";
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(HdfsConstants.YMD_FORMAT);
			format = formatter.format(date);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return format;
	}
	

}
