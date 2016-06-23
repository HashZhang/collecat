package com.sf.collecat.node.jdbc;

import com.alibaba.druid.pool.DruidDataSource;
import com.sf.collecat.common.model.Job;
import com.sf.collecat.node.jdbc.handler.ResultHandler;
import com.sf.collecat.node.jdbc.handler.ResultHandlerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by 862911 on 2016/6/15.
 */
public class JDBCConnection {
    private Connection connection;
    private Job job;
    private ResultHandler resultHandler;

    public JDBCConnection(DruidDataSource druidDataSource, Job job) throws SQLException {
        connection = druidDataSource.getConnection();
        resultHandler = ResultHandlerFactory.getResultHandler(job.getMessageFormat());
        this.job = job;
    }

    public String[] executeJob() throws SQLException {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(job.getJobSql());
            resultSet.last();
            int count = resultSet.getRow();
            int size = (count / job.getKafkaMessageSize()) + 1;
            String[] results = new String[size];
            resultSet.beforeFirst();
            for (int i = 0; i < size; i++) {
                results[i] = resultHandler.handle(resultSet, job.getKafkaMessageSize());
            }
            return results;
        } finally {
            if (statement != null) {
                statement.close();
            }
            returnConn();
        }
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
        if (job != null) {
            resultHandler = ResultHandlerFactory.getResultHandler(job.getMessageFormat());
        }
    }

    private void returnConn() throws SQLException {
        this.connection.close();
    }
}
