package com.sf.collecat.node.jdbc;

import com.alibaba.druid.pool.DruidDataSource;
import com.sf.collecat.common.model.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by 862911 on 2016/6/15.
 */
@Component
public class JDBCConnectionPool {
    private final static Logger LOGGER = LoggerFactory.getLogger(JDBCConnection.class);
    private final ConcurrentHashMap<String, DruidDataSource> connMap = new ConcurrentHashMap<>();
    private final ReentrantLock reentrantLock = new ReentrantLock();
    @Value("${jdbc.connection.poolsize}")
    private int maxPoolSize;

    public ConcurrentHashMap<String, DruidDataSource> getConnMap() {
        return this.connMap;
    }

    public JDBCConnection getConnection(Job job) {
        final String url = job.getMysqlUrl();

        try {
            JDBCConnection jdbcConnection = null;
            DruidDataSource druidDataSource = null;
            if (!connMap.containsKey(url) || ((druidDataSource = connMap.get(url)) == null)) {
                if (reentrantLock.tryLock()) {
                    try {
                        druidDataSource = new DruidDataSource();
                        druidDataSource.setUrl(job.getMysqlUrl());
                        druidDataSource.setUsername(job.getMysqlUsername());
                        druidDataSource.setPassword(job.getMysqlPassword());
                        druidDataSource.setInitialSize(1);
                        druidDataSource.setMinIdle(1);
                        druidDataSource.setMaxActive(maxPoolSize);
                        druidDataSource.init();
                        connMap.put(job.getMysqlUrl(), druidDataSource);
                    } finally {
                        reentrantLock.unlock();
                    }
                } else {
                    while (!connMap.containsKey(url)) ;
                }
            }
            druidDataSource = connMap.get(url);
            jdbcConnection = new JDBCConnection(druidDataSource, job);
            jdbcConnection.setJob(job);
            return jdbcConnection;
        } catch (SQLException e) {
            LOGGER.error("Caught exception when creating new JDBC connection: [url:],exception:", url, e);
        }
        return null;
    }
}
