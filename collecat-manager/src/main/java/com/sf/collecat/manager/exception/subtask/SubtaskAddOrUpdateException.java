package com.sf.collecat.manager.exception.subtask;

import com.sf.collecat.common.exception.CollecatException;

/**
 * Task添加异常
 *
 * @author 862911 Hash Zhang
 * @version 1.0.0
 * @time 2016/6/27
 */
public class SubtaskAddOrUpdateException extends CollecatException {
    public SubtaskAddOrUpdateException() {
        super();
    }

    public SubtaskAddOrUpdateException(String message) {
        super(message);
    }

    public SubtaskAddOrUpdateException(Throwable cause) {
        super(cause);
    }
}
