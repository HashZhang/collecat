package com.sf.collecat.node.jdbc.handler;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by 862911 on 2016/6/16.
 */
public interface ResultHandler {
    public String handle(ResultSet rs,int size) throws SQLException;
}
