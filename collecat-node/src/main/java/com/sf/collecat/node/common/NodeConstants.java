package com.sf.collecat.node.common;

import com.sf.collecat.common.Constants;

/**
 * Created by 862911 on 2016/6/23.
 */
public interface NodeConstants extends Constants {
    String WELCOME = "ColleCat-Node is running now! ";
    String POOL_SIZE_MESSAGE = "ColleCat-Node worker thread pool size: ";
    String ROOT_SPRING_CONFIG = "classpath:spring-config-service.xml";
    String CANNOT_GET_IPADDRESS = "Cannot get localhost ip address!";
}
