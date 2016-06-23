package com.sf.collecat.manager.zk;

import com.sf.collecat.common.Constants;
import com.sf.collecat.manager.zk.listener.JobListener;
import com.sf.collecat.manager.zk.listener.NodeListener;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by 862911 on 2016/6/16.
 */
public class CuratorClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(CuratorClient.class);
    private final CuratorFramework client;
    private final TreeCache nodeCache;
    private final TreeCache jobCache;

    private JobListener jobListener;
    private NodeListener nodeListener;

    private final String zkAddress;

    public CuratorClient(String zkAddress) {
        this.zkAddress = zkAddress;
        this.client = CuratorFrameworkFactory.newClient(zkAddress, new ExponentialBackoffRetry(1000, 3));
        this.client.start();
        try {
            if (this.client.checkExists().forPath(Constants.NODE_PATH) == null) {
                this.client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT)
                        .forPath(Constants.NODE_PATH);
            }
            if (this.client.checkExists().forPath(Constants.JOB_PATH) == null) {
                this.client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT)
                        .forPath(Constants.JOB_PATH);
            }
            if (this.client.checkExists().forPath(Constants.JOB_DETAIL_PATH) == null) {
                this.client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT)
                        .forPath(Constants.JOB_DETAIL_PATH);
            }
        } catch (Exception e) {
            LOGGER.error("ZK exception:", e);
        }

        nodeCache = new TreeCache(client, Constants.NODE_PATH);
        jobCache = new TreeCache(client, Constants.JOB_PATH);
    }

    public void setData(String path, String data) {
        try {
            this.client.setData().forPath(path, data.getBytes(Constants.STRING_ENCODING));
        } catch (Exception e) {
            LOGGER.error("ZK exception:", e);
        }
    }

    public void removePath(String path){
        InterProcessSemaphoreMutex interProcessSemaphoreMutex = new InterProcessSemaphoreMutex(client, path);
        try {
            interProcessSemaphoreMutex.acquire();//删除了就是释放锁了
            this.client.delete().deletingChildrenIfNeeded().forPath(path);
        } catch (Exception e) {
            LOGGER.error("ZK exception:", e);
        }
    }



    public void createPath(String path, String data) {
        try {
            this.client.create().withMode(CreateMode.PERSISTENT).forPath(path, data.getBytes(Constants.STRING_ENCODING));
        } catch (Exception e) {
            LOGGER.error("ZK exception:", e);
        }
    }

    public JobListener getJobListener() {
        return jobListener;
    }

    public void setJobListener(JobListener jobListener) {
        this.jobListener = jobListener;
        try {
            jobCache.getListenable().addListener(jobListener);
            jobCache.start();
        } catch (Exception e) {
            LOGGER.error("ZK exception:", e);
        }
    }

    public String getData(String path) {
        String rusult = null;
        try {
            rusult = new String(this.client.getData().forPath(path), Constants.STRING_ENCODING);
        } catch (Exception e) {
            LOGGER.error("ZK exception:", e);
        }
        return rusult;
    }

    public List<String> getChildren(String path){
        List<String> result = null;
        try {
            result = this.client.getChildren().forPath(path);
        } catch (Exception e) {
            LOGGER.error("ZK exception:", e);
        }
        return result;
    }

    public NodeListener getNodeListener() {
        return nodeListener;
    }

    public void setNodeListener(NodeListener nodeListener) {
        this.nodeListener = nodeListener;
        try {
            nodeCache.getListenable().addListener(nodeListener);
            nodeCache.start();
        } catch (Exception e) {
            LOGGER.error("ZK exception:", e);
        }
    }
}
