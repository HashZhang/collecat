/* 
 * Copyright (c) 2015, S.F. Express Inc. All rights reserved.
 */
package com.sf.sgs.kafka2hdfs.util;

/**
 * 描述：常量类
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
public interface HdfsConstants {
	
	public final static String HDFS_FILE_PATH = "hdfsFilePath";
	
	public final static String HDFS_DATA_ADDR = "hdfs.user.addr";
	
	public final static String STORE_NAME = "storeName";
	public final static String SYSTEM_NAME = "systemName";
	public final static String COLUMN_SPLIT = "columnSplit";
	public final static String NULL_SPLIT = "nullSplit";
	public final static String TABLE_NAME = "tableName";
	public final static String COMMENT = "comment";
	public final static String FILE_NAME = "fileName";
	public final static String SUFFIX = "suffix";
	public final static String TYPE = "type";
	public final static String NAME = "name";
	public final static String VALUE = "value";
	public final static String INDEX = "index";
	
	
	public final static String HDFS_PATH_REG = "/";
	
	/**
     * 年月日格式 yyyyMMdd，按天分区
     */
    public final static String YMD_FORMAT = "yyyyMMdd";
    
    public final static String CHARACTER = "character";
    
    public final static String UTF8 = "UTF-8";
    /**
     * 临时文件的后缀
     */
    public final static String FILE_TMP_SUFFIX = ".tmp";
    
    public final static String FILE_FIX_SUFFIX = ".fix";
	
    public final static String FILE_MAX_DATA_NUM = "hdfs.file_max_data_num";
    public final static String FILE_TIMEOUT = "hdfs.file_timeout";
    public final static int DEFAULT_FILE_MAX_DATA_NUM = 100000;
    public final static long DEFAULT_FILE_TIMEOUT = 1000 * 60 * 30;
    
    public final static char FIELD_SPLIT_CHAR = '\001';
    public final static String ROW_SPLIT_STR = "\n";
}
