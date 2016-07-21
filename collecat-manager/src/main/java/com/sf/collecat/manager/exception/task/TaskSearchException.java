package com.sf.collecat.manager.exception.task;

import com.sf.collecat.common.exception.CollecatException;

/**
 * Task获取异常
 *
 * @author 862911 Hash Zhang
 * @version 1.0.0
 * @time 2016/6/27
 */
public class TaskSearchException extends CollecatException{
    public TaskSearchException() {
        super();
    }

    public TaskSearchException(String message) {
        super(message);
    }

    public TaskSearchException(Throwable cause) {
        super(cause);
    }
}
