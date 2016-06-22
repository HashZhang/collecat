package com.sf.collecat.node.jdbc.handler.impl;

import com.sf.collecat.node.jdbc.handler.ResultHandler;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Created by 862911 on 2016/6/16.
 */
public class ToCSVHandler implements ResultHandler {
    public String handle(ResultSet rs, int size) throws SQLException {
        StringBuilder stringBuilder = new StringBuilder();
        ResultSetMetaData rsm = rs.getMetaData(); //获得列集
        int col = rsm.getColumnCount();   //获得列的个数
        int i = 0;
        while (i < size && rs.next()) {
            for (int j = 0; j < col; j++) {
                stringBuilder.append("\"").append(rs.getString(j + 1)).append("\"");
                if (j != col - 1) {
                    stringBuilder.append(",");
                } else {
                    stringBuilder.append("\n");
                }
            }
            i++;
        }
        return stringBuilder.toString();
    }
}
