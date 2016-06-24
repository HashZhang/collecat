package com.sf.collecat.node.jdbc.exception;

import com.sf.collecat.common.exception.CollecatException;

/**
 * Created by 862911 on 2016/6/24.
 */
public class GetJDBCCConnectionException extends CollecatException{

    public GetJDBCCConnectionException() {
        super();
    }

    public GetJDBCCConnectionException(String message) {
        super(message);
    }

    public GetJDBCCConnectionException(Throwable cause) {
        super(cause);
    }
}
