/* 
 * Copyright (c) 2015, S.F. Express Inc. All rights reserved.
 */
package com.sf.sgs.kafka2hdfs.hdfs;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sf.sgs.kafka2hdfs.exception.HdfsException;
import com.sf.sgs.kafka2hdfs.model.Table;
import com.sf.sgs.kafka2hdfs.util.HdfsConfigUtils;
import com.sf.sgs.kafka2hdfs.util.HdfsConstants;
import com.sf.sgs.kafka2hdfs.util.HdfsUtils;

/**
 * 描述：写 hdfs 的基类
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
public abstract class AbstractBaseHdfsWriter {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	/**
	 * 用于记录本线程写的文件信息
	 * tableName --> partitionValue --> FileInfo
	 */
	private ThreadLocal<Map<String, Map<String,FileInfo>>> currentLocalInfo = new ThreadLocal<Map<String, Map<String,FileInfo>>>();
	
	/**
	 * 写入数据到hdfs
	 * @param tableName
	 * @param datas
	 * @throws HdfsException 遇到此异常时程序应终止处理
	 */
	protected void write(String tableName, String day, List<String[]> datas) throws HdfsException {
		if (datas == null || datas.isEmpty()) {
			return;
		}
		String serverIndex = this.getServerIndex();
		if (StringUtils.isBlank(serverIndex)) {
			throw new HdfsException("serverIndex is blank, ["+ tableName+"]");
		}
		Table table = HdfsConfigUtils.instance().getTable(tableName);
		if (table == null) {
			throw new HdfsException("not found table config : "+ tableName);
		}
		//将传入的数据凭借成字符串
		List<String> dataStrs = convertData2String(table, datas);
		
		//取出本地的信息
		Map<String, Map<String,FileInfo>> localFileInfo = currentLocalInfo.get();
		if (localFileInfo == null) {
			localFileInfo = new HashMap<String, Map<String,FileInfo>>(4);
			currentLocalInfo.set(localFileInfo);
		}
		Map<String,FileInfo> fileInfoMap = localFileInfo.get(tableName);
		if (fileInfoMap == null) {
			fileInfoMap = new HashMap<String, FileInfo>();
			localFileInfo.put(tableName, fileInfoMap);
		}
		FileInfo fileInfo = fileInfoMap.get(day);
		if (fileInfo == null) {
			fileInfo = getNewFileInfo(table, day, serverIndex);
			fileInfoMap.put(day, fileInfo);
		}
		//写数据到hdfs
		writeHdfs(fileInfo, dataStrs);
	}
	
	private void writeHdfs(FileInfo fileInfo, List<String> dataStrs) {
		FileSystem fs = null;
		FSDataOutputStream outputStream = null;
		try {
			Configuration conf = HdfsConfigUtils.instance().getConfiguration();
			fs = FileSystem.get(URI.create(fileInfo.fileName), conf);
			Path path = new Path(fileInfo.fileName);
			if (!fs.exists(path)) {// 不存在创建新的文件
				fs.create(path);
				fs.close();
				fs = FileSystem.get(URI.create(fileInfo.fileName), conf);
			}
			outputStream = fs.append(path);
			String charSet = HdfsConfigUtils.instance().get(HdfsConstants.CHARACTER);
			if (StringUtils.isBlank(charSet)) {
				charSet = HdfsConstants.UTF8;
			}
			for (String elem : dataStrs) {
				outputStream.write(elem.getBytes(charSet));
				outputStream.write(HdfsConstants.ROW_SPLIT_STR.getBytes(charSet));
			}
			int count = fileInfo.counter.addAndGet(dataStrs.size());
			logger.info("write file:{}, {}/{} ", new Object[]{fileInfo.fileName, dataStrs.size(), count});
			if (checkFile(fileInfo)) {
				//超过文件最大记录数 或超时,本地的所有文件都改为正式文件
				renameTempFile();
			}
		} catch (IOException e) {
			logger.error("write hdfs error, "+ e.getMessage(), e);
			currentLocalInfo.remove();
			throw new HdfsException(e);
		}  finally {
			HdfsUtils.close(outputStream);
			HdfsUtils.close(fs);
		}
	}
	
	
	/***
	 * 重命名临时文件
	 */
	public void renameTempFile() throws IOException {
		try {
			Map<String, Map<String,FileInfo>> map = currentLocalInfo.get();
			if (map == null || map.isEmpty()) {
				return;
			}
			Configuration conf = HdfsConfigUtils.instance().getConfiguration();
			//超过文件最大记录数 或超时,本地的所有文件都改为正式文件
			for (Entry<String, Map<String,FileInfo>> entry : map.entrySet()) {
				Map<String,FileInfo> tempFileInfoMap = entry.getValue();
				String tableName = entry.getKey();
				for (FileInfo tmpFileInfo : tempFileInfoMap.values()) {
					changeFileName(conf, tableName, tmpFileInfo.fileName, new Path(tmpFileInfo.fileName));
				}
			}
			currentLocalInfo.remove();
		} catch (IOException e) {
			logger.error("renameTempFile error, "+e.getMessage(), e);
			throw e;
		}
	}
	/**
	 * 获取本节点的唯一运行id、索引
	 * @return
	 */
	protected String getServerIndex() {
		return HdfsConfigUtils.instance().getServerIndex();
	}
	
	/**
	 * 修改文件名
	 */
	private void changeFileName(Configuration conf, String tableName, String fileName, Path oldPath) throws IOException {
		FileSystem fs = null;
		try {
			fs = FileSystem.get(URI.create(fileName), conf); 
			boolean isExists = fs.exists(oldPath);
			// 如果文件存在就把后缀名为.tmp的修改了
			if (isExists) {
				String newSuffix = "."+HdfsConfigUtils.instance().getTable(tableName).getSuffix();
				String newName = fileName.replace(HdfsConstants.FILE_TMP_SUFFIX, newSuffix);
				Path topath = new Path(newName); // 新的文件名
				fs.rename(oldPath, topath);
				logger.info("{}, rename file:{} -> {}", new Object[] { tableName, fileName, newName });
			}
		} finally {
			HdfsUtils.close(fs);
		}
		
	}
	
	/**
	 * 检查文件数据是否超过 检查创建时间是否超时
	 * @param fileInfo
	 * @return
	 */
	private boolean checkFile(FileInfo fileInfo) {
		if (fileInfo.counter.get() >= this.getFileMaxDataNum()) {
			return true;
		}
		if ((System.currentTimeMillis() - fileInfo.createTm) > this.getFileTimeout()) {
			return true;
		}
		return false;
	}
	
	/**
	 * 将传入的数据拼接成字符串
	 * @param table
	 * @param datas
	 * @return
	 */
	private List<String> convertData2String(Table table, List<String[]> datas) {
		int size = datas.size();
		
		List<String> result = new ArrayList<String>(size);
		
		String tableName = table.getTableName();
		String nullSplit = table.getNullSplit();
		int indexSize = table.getIndexColumnMap().size();
		for (int i = 0; i < size; i++) {
			String[] objArr = datas.get(i);
			
			if (objArr.length < indexSize) {
				String message = "配置的下标数大于数组的下标数（表：" + tableName + " 实际字段个数： "
						+ objArr.length + " 配置字段个数： " + indexSize + "）....";
				logger.error(message);
				throw new HdfsException(message);
			}
			StringBuilder sb = new StringBuilder();
			int mark = 1;
			// 根据配置拼接数据
			for (Entry<Integer, Integer> entry : table.getIndexColumnMap().entrySet()) {
				int index = entry.getValue();
				if (index > objArr.length - 1) {
					String message = "配置的下标数太大，取不到对应的值（表：" + tableName
							+ " 实际字段个数： " + objArr.length + " 配置字段个数： "
							+ indexSize + " 错误下标值： " + index + " ）....";
					logger.error(message);
					throw new HdfsException(message);
				}
				String temp = objArr[index];
				if (mark == indexSize) {//相等表明为最后一列
					sb.append(temp == null ? nullSplit : replaceLastSpecialStr(temp));
				} else {
					sb.append(temp == null ? nullSplit : replaceSpecialStr(temp));
					sb.append(HdfsConstants.FIELD_SPLIT_CHAR);
				}
				mark++;
			}
			result.add(sb.toString());
		}

		return result;
	}
	
	private String replaceSpecialStr(String str) {
		return str.replaceAll("\n", "|");
	}
	
	private String replaceLastSpecialStr(String str) {
		return str.replaceAll("\n", "|").replaceAll("\t", "#");
	}
	
	
	private FileInfo getNewFileInfo(Table table, String day, String serverIndex) {
		FileInfo fileInfo = new FileInfo();
		fileInfo.createTm = System.currentTimeMillis();
		fileInfo.fileName = getFullFileName(table, day, serverIndex);
		return fileInfo;
	}

	/**
	 * 获取要写入的文件全名，包括路径
	 * @param table
	 * @param serverIndex
	 * @return
	 */
	private String getFullFileName(Table table, String day, String serverIndex) {
		String middleFilePath = day;
		if (StringUtils.isEmpty(middleFilePath)) {
			middleFilePath = getDefaultMiddleFilePath();
		}
		return HdfsUtils.getFullFileName(table, serverIndex, middleFilePath);
	}
	
	/**
     * 休眠一段时间
     * 
     * @param millis
     *            毫秒
     */
	protected final void sleep(long millis) {
        if (millis > 0) {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
            	this.logger.warn(e.getMessage(), e);
            }
        }
    }
	
	
	/**
	 * 返回当前时间的 yyyyMMdd 格式 ，如20151020
	 * @return
	 */
	protected String getDefaultMiddleFilePath() {
		return HdfsUtils.formatDate(new Date());
	}

	/**
	 * 获取文件路径中的中间的一段，用于分组
	 * 例如：此方法返回的值为 “20151020”</br>
	 * 
	 * 如果返回的值为null，则取系统当前时间，按 YYYYMMDD 的方式分组
	 * @return
	 */
	protected String getMiddleFilePath(){
		return getDefaultMiddleFilePath();
	}
	
	/**
	 * 获取每个文件中的最大数据数，超过次数换一个文件写
	 * @return
	 */
	protected int getFileMaxDataNum(){
		String maxDataNumStr = HdfsConfigUtils.instance().get(HdfsConstants.FILE_MAX_DATA_NUM);
		return Integer.getInteger(maxDataNumStr, HdfsConstants.DEFAULT_FILE_MAX_DATA_NUM);
	}
	
	/**
	 * 获取文件的超时时长，超时换个文件写
	 * @return
	 */
	protected long getFileTimeout(){
		String fileTimeoutStr = HdfsConfigUtils.instance().get(HdfsConstants.FILE_TIMEOUT);
		return Long.getLong(fileTimeoutStr, HdfsConstants.DEFAULT_FILE_TIMEOUT);
	}
	
	/**
	 * 
	 * 数据文件信息类
	 */
	static class FileInfo {
		
		/**
		 * 文件的全名
		 */
		String fileName;
		/**
		 * 文件中已写入记录的数
		 */
		AtomicInteger counter = new AtomicInteger(0);
		
		/**
		 * 文件的创建时间
		 */
		long createTm;
	}
}
