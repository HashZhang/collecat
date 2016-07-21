package com.sf.collecat.manager.exception.subtask;

import com.sf.collecat.common.exception.CollecatException;

/**
 * Task修改异常
 *
 * @author 862911 Hash Zhang
 * @version 1.0.0
 * @time 2016/6/27
 */
public class SubtaskModifyException extends CollecatException {
    public SubtaskModifyException() {
        super();
    }

    public SubtaskModifyException(String message) {
        super(message);
    }

    public SubtaskModifyException(Throwable cause) {
        super(cause);
    }
}
