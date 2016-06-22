package com.sf.collecat.node.jdbc.handler;

import com.sf.collecat.node.jdbc.handler.impl.ToCSVHandler;
import com.sf.collecat.node.jdbc.handler.impl.ToJsonHandler;

/**
 * Created by 862911 on 2016/6/16.
 */
public class ResultHandlerFactory {
    private final static String TO_JSON = "json";
    private final static String TO_CSV = "csv";

    public static ResultHandler getResultHandler(String format) {
        if (TO_CSV.equalsIgnoreCase(format)) {
            return new ToCSVHandler();
        } else if (TO_JSON.equalsIgnoreCase(format)) {
            return new ToJsonHandler();
        }
        return null;
    }
}
