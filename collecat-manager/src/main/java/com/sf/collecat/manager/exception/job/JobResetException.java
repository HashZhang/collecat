package com.sf.collecat.manager.exception.job;

import com.sf.collecat.common.exception.CollecatException;

/**
 * Job重置异常
 *
 * @author 862911 Hash Zhang
 * @version 1.0.0
 * @time 2016/6/27
 */
public class JobResetException extends CollecatException {
    public JobResetException() {
        super();
    }

    public JobResetException(String message) {
        super(message);
    }

    public JobResetException(Throwable cause) {
        super(cause);
    }
}
