package com.sf.collecat.node.jdbc.handler.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sf.collecat.node.jdbc.handler.ResultHandler;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Created by 862911 on 2016/6/16.
 */
public class ToJsonHandler implements ResultHandler {
    public String handle(ResultSet rs, int size) throws SQLException {
        // json数组
        JSONArray array = new JSONArray();
        // 获取列数
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        int j = 0;
        // 遍历ResultSet中的每条数据
        while (j < size && rs.next()) {
            JSONObject jsonObj = new JSONObject();
            // 遍历每一列
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnLabel(i);
                String value = rs.getString(columnName);
                jsonObj.put(columnName, value);
            }
            array.add(jsonObj);
            j++;
        }

        return JSON.toJSONString(array);
    }
}
