package com.sf.collecat.node.jdbc;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.google.common.base.Optional;
import com.sf.collecat.common.model.Job;
import com.sf.collecat.node.jdbc.exception.GetJDBCCConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by 862911 on 2016/6/15.
 */
@Component
public class JDBCConnectionPool {
    private final static Logger log = LoggerFactory.getLogger(JDBCConnection.class);
    private final ConcurrentHashMap<String, Optional<DruidDataSource>> connMap = new ConcurrentHashMap<>();
    @Value("${jdbc.connection.poolsize}")
    private int maxPoolSize;

    public ConcurrentHashMap<String, Optional<DruidDataSource>> getConnMap() {
        return this.connMap;
    }

    public JDBCConnection getConnection(Job job) throws GetJDBCCConnectionException {
        final String url = job.getMysqlUrl();

        try {
            JDBCConnection jdbcConnection = null;
            DruidDataSource druidDataSource = null;
            if (!connMap.containsKey(url) || (connMap.get(url) == null)) {
                druidDataSource = new DruidDataSource();
                druidDataSource.setUrl(job.getMysqlUrl());
                druidDataSource.setUsername(job.getMysqlUsername());
                druidDataSource.setPassword(job.getMysqlPassword());
                druidDataSource.setInitialSize(1);
                druidDataSource.setMinIdle(1);
                druidDataSource.setMaxActive(maxPoolSize);
                druidDataSource.setTimeBetweenEvictionRunsMillis(60000);
                druidDataSource.setMinEvictableIdleTimeMillis(300000);
                druidDataSource.init();
                connMap.put(url, Optional.of(druidDataSource));
                log.warn("Do not panic,new data source generated");
            }
            while (connMap.get(url) == null) {
                System.out.println(Thread.currentThread() + url);
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            druidDataSource = connMap.get(url).get();
            jdbcConnection = new JDBCConnection(druidDataSource, job);
            jdbcConnection.setJob(job);
            return jdbcConnection;
        } catch (SQLException e) {
            throw new GetJDBCCConnectionException(e);
        }
    }

    public static void main(String[] args) throws SQLException {
        JDBCConnection jdbcConnection = null;
        DruidDataSource druidDataSource = null;
        druidDataSource = new DruidDataSource();
        druidDataSource.setUrl("jdbc:mysql://10.202.44.206:8066/TESTDB");
        druidDataSource.setUsername("root");
        druidDataSource.setPassword("digdeep");
        druidDataSource.setInitialSize(1);
        druidDataSource.setMinIdle(1);
        druidDataSource.setMaxActive(1);
        druidDataSource.setTimeBetweenEvictionRunsMillis(60000);
        druidDataSource.setMinEvictableIdleTimeMillis(300000);
        druidDataSource.init();
        DruidPooledConnection connection = druidDataSource.getConnection();
        Statement statement = null;
        statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("/*select * from hotnews where container_no = 1*/select * from hotnews");
    }
}
