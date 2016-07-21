package com.sf.collecat.manager.exception.validate;

import com.sf.collecat.common.exception.CollecatException;

/**
 * Kafka验证异常
 *
 * @author 862911 Hash Zhang
 * @version 1.0.0
 * @time 2016/6/27
 */
public class ValidateKafkaException extends CollecatException{
    public ValidateKafkaException() {
        super();
    }

    public ValidateKafkaException(String message) {
        super(message);
    }

    public ValidateKafkaException(Throwable cause) {
        super(cause);
    }
}
