package com.sf.collecat.manager.exception.task;

import com.sf.collecat.common.exception.CollecatException;

/**
 * Task添加异常
 *
 * @author 862911 Hash Zhang
 * @version 1.0.0
 * @time 2016/6/27
 */
public class TaskAddException extends CollecatException {
    public TaskAddException() {
        super();
    }

    public TaskAddException(String message) {
        super(message);
    }

    public TaskAddException(Throwable cause) {
        super(cause);
    }
}
