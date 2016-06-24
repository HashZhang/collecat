package com.sf.collecat.common.exception;

import com.sf.collecat.common.Constants;

/**
 * Created by 862911 on 2016/6/24.
 */
public class CollecatException extends Exception{
    public CollecatException() {
        super();
    }

    public CollecatException(String message) {
        super(Constants.SYSTEM_NAME.concat(Constants.COMMON_SEPARATOR).concat(message));
    }

    public CollecatException(Throwable cause) {
        super(cause);
    }
}
