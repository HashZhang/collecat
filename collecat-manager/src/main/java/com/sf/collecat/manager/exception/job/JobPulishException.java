package com.sf.collecat.manager.exception.job;

import com.sf.collecat.common.exception.CollecatException;

/**
 * job发布异常
 *
 * @author 862911 Hash Zhang
 * @version 1.0.0
 * @time 2016/6/27
 */
public class JobPulishException extends CollecatException {
    public JobPulishException() {
        super();
    }

    public JobPulishException(String message) {
        super(message);
    }

    public JobPulishException(Throwable cause) {
        super(cause);
    }
}
