/* 
 * Copyright (c) 2015, S.F. Express Inc. All rights reserved.
 */
package com.sf.sgs.kafka2hdfs.fix;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sf.sgs.kafka2hdfs.model.Table;
import com.sf.sgs.kafka2hdfs.util.DateTimeUtils;
import com.sf.sgs.kafka2hdfs.util.HdfsConfigUtils;
import com.sf.sgs.kafka2hdfs.util.HdfsConstants;
import com.sf.sgs.kafka2hdfs.util.HdfsUtils;

/**
 * 描述：数据修复执行器
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
public class HdfsDataFixExecutor extends Thread {
	
	private static final Logger logger = LoggerFactory.getLogger(HdfsDataFixExecutor.class);
	
	/**
	 * 服务器索引名
	 */
	private String serverIndex;
	
	private List<String> middleFilePaths;
	
	/**
	 * 修复指定时间之前创建的数据，默认1分钟前的数据
	 */
	private long tmStandard;
	
	private long interval = 10 * 60 * 1000l;
	
	
	public HdfsDataFixExecutor() {
		this.serverIndex = HdfsConfigUtils.instance().getServerIndex();
		this.setName("HdfsDataFixExecutor - "+serverIndex);
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				List<Table> allTable = HdfsConfigUtils.instance().getAllTable();
				if (allTable == null) {
					sleep(500L);
					return;
				}
				resetConfig();
				
				for (Table table : allTable) {
					this.fix(table);
					sleep(200L);
				}
				logger.info("sleep {} ms", this.interval);
				sleep(this.interval);
			} catch (Exception e) {
				logger.warn(e.getMessage(), e);
				try {
					sleep(500L);
				} catch (InterruptedException e1) {
				}
			}
		}
		
	}
	
	public void test() {
		try {
			List<Table> allTable = HdfsConfigUtils.instance().getAllTable();
			if (allTable == null) {
				sleep(500L);
				return;
			}
			resetConfig();
			
			for (Table table : allTable) {
				this.fix(table);
				sleep(200L);
			}
			logger.info("sleep {} ms", this.interval);
			sleep(this.interval);
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
			try {
				sleep(500L);
			} catch (InterruptedException e1) {
			}
		}
	}
	
	/**
	 * 初始化设置
	 */
	private void resetConfig() {
		List<String> middleFilePaths = new ArrayList<String>(4);
		String fixMiddleFilePath = HdfsConfigUtils.instance().get("hdfs.fixMiddleFilePath");
		if (!StringUtils.isEmpty(fixMiddleFilePath)) {
			middleFilePaths.add(fixMiddleFilePath);
		} else {
			int intFixDayRang = 3;
			String fixDayRang = HdfsConfigUtils.instance().get("hdfs.fixDayRang");
			if (!StringUtils.isEmpty(fixDayRang)) {
				try {
					intFixDayRang = Integer.valueOf(fixDayRang);
					intFixDayRang = intFixDayRang > 0 ? intFixDayRang : 3;
				} catch (Exception e) {
					intFixDayRang = 3;
				}
			}
			for (int i = intFixDayRang - 1; i >= 0; i--) {
				Date d = DateTimeUtils.addTimes(new Date(), -i, DateTimeUtils.TIME_TYPE_DATE);
				middleFilePaths.add(HdfsUtils.formatDate(d));
			}
		}
		this.middleFilePaths = middleFilePaths;
		//修复指定时间之前创建的数据，默认20分钟前的数据
		String tmStandardStr = HdfsConfigUtils.instance().get("hdfs.tmStandard");
		this.tmStandard = System.currentTimeMillis() - (20 * DateTimeUtils.MILLIS_OF_MINUTE);//
		if (!StringUtils.isEmpty(tmStandardStr)) {
			try {
				int v = Integer.valueOf(tmStandardStr);
				v = v > 0 ? v : 20;
				logger.info("fix before {} min files", v);
				tmStandard = System.currentTimeMillis() - (v * DateTimeUtils.MILLIS_OF_MINUTE);
			} catch (Exception e) {
			}
		}
		
		String strInterval = HdfsConfigUtils.instance().get("hdfs.fixInterval");
		if (!StringUtils.isEmpty(strInterval)) {
			try {
				int v = Integer.valueOf(strInterval);
				this.interval = v > 0 ? v * DateTimeUtils.MILLIS_OF_MINUTE : this.interval;
			} catch (Exception e) {
			}
		}
	}
	
	private void fix(Table table) {
		
		Configuration conf = HdfsConfigUtils.instance().getConfiguration();
		//文件的前缀
		String filePrefix = table.getFileName() + "_" + this.serverIndex;
		 
		for (String middleFilePath : middleFilePaths) {
			try {
				String fixDirectory = HdfsUtils.getFixDirectory(table, middleFilePath);
				logger.info("fixDirectory:{}", fixDirectory);
				FileStatus[] status = getFileStatus(fixDirectory, conf);
				if (status == null || status.length == 0) {
					continue;
				}
				for (FileStatus fileStatus : status) {
					String fileName = fileStatus.getPath().getName();
					if (fileName.startsWith(filePrefix)) {
						if (fileName.endsWith(HdfsConstants.FILE_TMP_SUFFIX)) {//需要修复的文件
							String[] arr = fileName.split("_");
							String lastStr = arr[arr.length-1].replaceAll(HdfsConstants.FILE_TMP_SUFFIX, "");
							if (Long.valueOf(lastStr) < this.tmStandard) {//指定时间之前的文件才修复
								fix(conf, table, middleFilePath, fileStatus);
							}
						} else if (fileName.endsWith(HdfsConstants.FILE_FIX_SUFFIX)) {
							//修复的临时文件，直接删除
							deleteFixTempFile(conf, fileStatus);
						}
						
					}
				}
			} catch (Exception e) {
				logger.warn(e.getMessage(), e);
			}
		}
		
		logger.info("fix: {} done ...", table.getTableName());
	}
	
	/**
	 * 删除修复的临时文件
	 * @param conf
	 * @param fileStatus
	 */
	private void deleteFixTempFile(Configuration conf, FileStatus fileStatus) {
		String filename = fileStatus.getPath().toString();
		logger.info("delete fix temp file:{}", filename);
		FileSystem fs = null;
		try {
			fs = FileSystem.get(URI.create(filename), conf);
			fs.delete(new Path(filename), Boolean.FALSE);
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
		} finally {
			HdfsUtils.close(fs);
		}
	}
	
	
	/**
	 * 修复损坏的文件
	 */
	private void fix(Configuration conf, Table table,
			String middleFilePath, FileStatus fileStatus) throws IOException {
		// 输出文件
		FileSystem fs = null;
		FSDataOutputStream outputStream = null;
		FileSystem srcFs = null;
		FSDataInputStream inputStream = null;
		BufferedReader bufferedReader = null;
		String newFileName = HdfsUtils.getFixFullFileName(table,
				this.serverIndex, middleFilePath);
		try {
			//从源文件读出数据
			String srcFileName = fileStatus.getPath().toString();
			logger.info("fix temp file:{}", srcFileName);
			fs = FileSystem.get(URI.create(newFileName), conf);
			Path path = new Path(newFileName);
			boolean isExists = fs.exists(path);
			if (!isExists) {
				fs.create(path);
				fs.close();
				fs = FileSystem.get(URI.create(newFileName), conf);
			}
			outputStream = fs.append(path);
			String charSet = HdfsConfigUtils.instance().get(HdfsConstants.CHARACTER);
			if (StringUtils.isBlank(charSet)) {
				charSet = HdfsConstants.UTF8;
			}
			srcFs = FileSystem.get(URI.create(srcFileName), conf);
			inputStream = srcFs.open(new Path(srcFileName));
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
//				if (str.endsWith("\t")) {
//					outputStream.write(str.getBytes(charSet));
//					outputStream.write("\n".getBytes());
//				} else {
//					logger.info("illegal data:{}", str);
//				}
				outputStream.write(str.getBytes(charSet));
				outputStream.write("\n".getBytes());
			}
			String newName = newFileName+"."+table.getSuffix();
			//重命名为正式文件
			fs.rename(path, new Path(newName));
			fs.delete(new Path(srcFileName), Boolean.FALSE);//删除源临时文件
		} finally {
			HdfsUtils.close(bufferedReader);
			HdfsUtils.close(inputStream);
			HdfsUtils.close(srcFs);
			HdfsUtils.close(outputStream);
			HdfsUtils.close(fs);
		}
	}
	
	
	/**
	 * 获取所有文件
	 * @param fixDirectory
	 * @return
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private FileStatus[] getFileStatus(final String fixDirectory, Configuration conf)
			throws IOException, FileNotFoundException {
		FileSystem inFS = FileSystem.get(URI.create(fixDirectory), conf);
		Path path = new Path(fixDirectory);
		boolean isExists = inFS.exists(path);
		if (!isExists) {
			logger.info("fixDirectory not exist, {}", fixDirectory);
			return null;
		}
		// 拿到当前目录的所有文件
		FileStatus[] status = inFS.listStatus(path);
		return status;
	}
}
