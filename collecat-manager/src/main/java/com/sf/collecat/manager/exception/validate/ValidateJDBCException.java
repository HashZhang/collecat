package com.sf.collecat.manager.exception.validate;

import com.sf.collecat.common.exception.CollecatException;

/**
 * JDBC验证异常
 *
 * @author 862911 Hash Zhang
 * @version 1.0.0
 * @time 2016/6/27
 */
public class ValidateJDBCException extends CollecatException {
    public ValidateJDBCException() {
        super();
    }

    public ValidateJDBCException(String message) {
        super(message);
    }

    public ValidateJDBCException(Throwable cause) {
        super(cause);
    }
}
