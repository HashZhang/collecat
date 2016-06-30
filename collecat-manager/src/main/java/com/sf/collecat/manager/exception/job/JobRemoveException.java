package com.sf.collecat.manager.exception.job;

import com.sf.collecat.common.exception.CollecatException;

/**
 * job移除异常
 *
 * @author 862911 Hash Zhang
 * @version 1.0.0
 * @time 2016/6/27
 */
public class JobRemoveException extends CollecatException {
    public JobRemoveException() {
        super();
    }

    public JobRemoveException(String message) {
        super(message);
    }

    public JobRemoveException(Throwable cause) {
        super(cause);
    }
}
