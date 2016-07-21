package com.sf.collecat.manager.exception.task;

import com.sf.collecat.common.exception.CollecatException;

/**
 * Task删除异常
 *
 * @author 862911 Hash Zhang
 * @version 1.0.0
 * @time 2016/6/27
 */
public class TaskDeleteException extends CollecatException{
    public TaskDeleteException() {
        super();
    }

    public TaskDeleteException(String message) {
        super(message);
    }

    public TaskDeleteException(Throwable cause) {
        super(cause);
    }
}
