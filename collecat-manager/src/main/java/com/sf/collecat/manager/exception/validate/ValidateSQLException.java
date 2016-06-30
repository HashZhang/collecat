package com.sf.collecat.manager.exception.validate;

import com.sf.collecat.common.exception.CollecatException;

/**
 * SQL语句验证异常
 *
 * @author 862911 Hash Zhang
 * @version 1.0.0
 * @time 2016/6/27
 */
public class ValidateSQLException extends CollecatException{
    public ValidateSQLException() {
        super();
    }

    public ValidateSQLException(String message) {
        super(message);
    }

    public ValidateSQLException(Throwable cause) {
        super(cause);
    }
}
