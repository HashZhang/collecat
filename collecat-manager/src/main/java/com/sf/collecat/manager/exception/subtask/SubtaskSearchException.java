package com.sf.collecat.manager.exception.subtask;

import com.sf.collecat.common.exception.CollecatException;

/**
 * Task获取异常
 *
 * @author 862911 Hash Zhang
 * @version 1.0.0
 * @time 2016/6/27
 */
public class SubtaskSearchException extends CollecatException{
    public SubtaskSearchException() {
        super();
    }

    public SubtaskSearchException(String message) {
        super(message);
    }

    public SubtaskSearchException(Throwable cause) {
        super(cause);
    }
}
