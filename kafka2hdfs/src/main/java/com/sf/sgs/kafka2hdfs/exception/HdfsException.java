/* 
 * Copyright (c) 2015, S.F. Express Inc. All rights reserved.
 */
package com.sf.sgs.kafka2hdfs.exception;

/**
 * 描述：自定义异常, 遇到此种异常程序应终止
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
public class HdfsException extends RuntimeException {

	private static final long serialVersionUID = 6380267366559288587L;
	
	public HdfsException() {
		super();
	}

	public HdfsException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public HdfsException(String message, Throwable cause) {
		super(message, cause);
	}

	public HdfsException(String message) {
		super(message);
	}

	public HdfsException(Throwable cause) {
		super(cause);
	}
	
}
