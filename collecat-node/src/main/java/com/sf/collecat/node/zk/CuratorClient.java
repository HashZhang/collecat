package com.sf.collecat.node.zk;

import com.sf.collecat.common.Constants;
import com.sf.collecat.node.common.NodeConstants;
import com.sf.collecat.node.executor.WorkerPool;
import com.sf.collecat.node.zk.listener.JobListener;
import com.sf.collecat.node.zk.listener.NodeListener;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by 862911 on 2016/6/16.
 */
public class CuratorClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(CuratorClient.class);

    public CuratorFramework getClient() {
        return client;
    }

    private final CuratorFramework client;
    private final String em_path;
    private String nodeID;


    private TreeCache nodeCache;
    private NodeListener nodeListener;

    private TreeCache jobCache;
    private JobListener jobListener;
    private WorkerPool workerPool;

    public CuratorClient(String zkAddress) {

        this.client = CuratorFrameworkFactory.newClient(zkAddress, new ExponentialBackoffRetry(1000, 3));
        String em_path = null;
        this.client.start();
        try {
            em_path = this.client.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                    .forPath(Constants.TEM_NODE_PATH);

        } catch (UnknownHostException e) {
            LOGGER.error(NodeConstants.CANNOT_GET_IPADDRESS);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Unsupporting encoding!");
        } catch (Exception e) {
            LOGGER.error(NodeConstants.ZK_EXCEPTION, e);
        }
        this.em_path = em_path;
        this.nodeCache = new TreeCache(this.client, em_path);
        this.nodeCache.getListenable().addListener(new NodeListener(this));
        try {
            this.nodeCache.start();
            String ip = InetAddress.getLocalHost().getHostAddress();
            this.client.setData().forPath(em_path, ip.getBytes(Constants.STRING_ENCODING));
        } catch (Exception e) {
            LOGGER.error(NodeConstants.ZK_EXCEPTION, e);
        }
    }

    public void setData(String path, String data) {
        try {
            this.client.setData().forPath(path, data.getBytes(Constants.STRING_ENCODING));
        } catch (Exception e) {
            LOGGER.error(NodeConstants.ZK_EXCEPTION, e);
        }
    }

    public String getData(String path) {
        String rusult = null;
        try {
            rusult = new String(this.client.getData().forPath(path), Constants.STRING_ENCODING);
        } catch (Exception e) {
            LOGGER.error(NodeConstants.ZK_EXCEPTION, e);
        }
        return rusult;
    }

    public String getNodeID() {
        return nodeID;
    }

    public void setNodeID(String nodeID) {
        this.nodeID = nodeID;
        try {
            while (this.jobCache == null) {
                Thread.yield();
            }
            this.jobCache.start();
        } catch (Exception e) {
            LOGGER.error(NodeConstants.ZK_EXCEPTION, e);
        }
    }

    public NodeListener getNodeListener() {
        return nodeListener;
    }

    public void setNodeListener(NodeListener nodeListener) {

    }

    public JobListener getJobListener() {
        return jobListener;
    }

    public void setJobListener(JobListener jobListener) {
        this.jobListener = jobListener;
        this.jobCache = new TreeCache(this.client, Constants.JOB_PATH);
        this.jobCache.getListenable().addListener(jobListener);

    }

    public WorkerPool getWorkerPool() {
        return workerPool;
    }

    public void setWorkerPool(WorkerPool workerPool) {
        this.workerPool = workerPool;
        workerPool.start();
    }
}
