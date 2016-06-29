package com.sf.collecat.manager.exception.task;

import com.sf.collecat.common.exception.CollecatException;

/**
 * Task修改异常
 *
 * @author 862911 Hash Zhang
 * @version 1.0.0
 * @time 2016/6/27
 */
public class TaskModifyException extends CollecatException {
    public TaskModifyException() {
        super();
    }

    public TaskModifyException(String message) {
        super(message);
    }

    public TaskModifyException(Throwable cause) {
        super(cause);
    }
}
