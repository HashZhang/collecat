package com.sf.collecat.manager.exception.job;

import com.sf.collecat.common.exception.CollecatException;

/**
 * Job查询异常，应该是数据库异常
 *
 * @author 862911 Hash Zhang
 * @version 1.0.0
 * @time 2016/6/27
 */
public class JobSearchException extends CollecatException {
    public JobSearchException() {
        super();
    }

    public JobSearchException(String message) {
        super(message);
    }

    public JobSearchException(Throwable cause) {
        super(cause);
    }
}
