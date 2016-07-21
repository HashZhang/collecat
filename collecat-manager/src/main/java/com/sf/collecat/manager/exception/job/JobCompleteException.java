package com.sf.collecat.manager.exception.job;

import com.sf.collecat.common.exception.CollecatException;

/**
 * job完成异常
 *
 * @author 862911 Hash Zhang
 * @version 1.0.0
 * @time 2016/6/27
 */
public class JobCompleteException extends CollecatException {
    public JobCompleteException() {
        super();
    }

    public JobCompleteException(String message) {
        super(message);
    }

    public JobCompleteException(Throwable cause) {
        super(cause);
    }
}
