package com.sf.collecat.node.zk.listener;

import com.alibaba.fastjson.JSON;
import com.sf.collecat.common.Constants;
import com.sf.collecat.common.model.Job;
import com.sf.collecat.node.executor.WorkerPool;
import com.sf.collecat.node.zk.CuratorClient;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Created by 862911 on 2016/6/17.
 */
@Component
public class JobListener implements TreeCacheListener {
    @Autowired
    private CuratorClient curatorClient;
    @Autowired
    private WorkerPool workerPool;

    @Override
    public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent) throws Exception {
        switch (treeCacheEvent.getType()) {
            case NODE_ADDED:
            case NODE_UPDATED: {
                newJob(treeCacheEvent.getData().getPath(), new String(treeCacheEvent.getData().getData(), Constants.STRING_ENCODING));
                break;
            }
            default:
                break;
        }
    }

    private void newJob(String path, String s) throws Exception {
        if (Constants.JOB_INIT.equals(s)) {
            InterProcessSemaphoreMutex interProcessSemaphoreMutex = new InterProcessSemaphoreMutex(curatorClient.getClient(), path);
            if (interProcessSemaphoreMutex.acquire(0, TimeUnit.NANOSECONDS)) {
                try {
                    if (Constants.JOB_INIT.equals(s)) {
                        curatorClient.setData(path, curatorClient.getNodeID());
                        String id = path.substring(path.lastIndexOf("/") + 1);
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(Constants.JOB_DETAIL_PATH).append("/").append(id);
                        Job job = JSON.parseObject(curatorClient.getData(stringBuilder.toString()), Job.class);
                        while (workerPool.getJobQueue() == null) {
                        }
                        workerPool.getJobQueue().put(job);
                    }
                } finally {
                    interProcessSemaphoreMutex.release();
                }
            }
        }
    }
}
