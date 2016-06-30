package com.sf.collecat.manager.exception.job;

import com.sf.collecat.common.exception.CollecatException;

/**
 * Job分配异常，应该是数据库异常
 *
 * @author 862911 Hash Zhang
 * @version 1.0.0
 * @time 2016/6/27
 */
public class JobAssignException extends CollecatException {
    public JobAssignException() {
        super();
    }

    public JobAssignException(String message) {
        super(message);
    }

    public JobAssignException(Throwable cause) {
        super(cause);
    }
}
