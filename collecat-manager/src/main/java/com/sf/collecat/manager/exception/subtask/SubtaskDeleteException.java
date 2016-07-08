package com.sf.collecat.manager.exception.subtask;

import com.sf.collecat.common.exception.CollecatException;

/**
 * Task删除异常
 *
 * @author 862911 Hash Zhang
 * @version 1.0.0
 * @time 2016/6/27
 */
public class SubtaskDeleteException extends CollecatException{
    public SubtaskDeleteException() {
        super();
    }

    public SubtaskDeleteException(String message) {
        super(message);
    }

    public SubtaskDeleteException(Throwable cause) {
        super(cause);
    }
}
