package com.sf.collecat.manager.zk.listener;

import com.sf.collecat.common.Constants;
import com.sf.collecat.common.mapper.JobMapper;
import com.sf.collecat.common.mapper.TaskMapper;
import com.sf.collecat.common.model.Job;
import com.sf.collecat.common.model.Task;
import com.sf.collecat.manager.zk.CuratorClient;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by 862911 on 2016/6/17.
 */
@Component
public class JobListener implements TreeCacheListener {
    @Autowired
    private JobMapper jobMapper;
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private CuratorClient curatorClient;

    @Override
    public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent) throws Exception {
        switch (treeCacheEvent.getType()) {
            case NODE_UPDATED: {
                handle(treeCacheEvent.getData().getPath(), new String(treeCacheEvent.getData().getData(), Constants.STRING_ENCODING));
                break;
            }
            default:
                break;
        }
    }

    private void handle(String path, String data) {
        String jobID = path.substring(path.lastIndexOf("/") + 1);
        Job job = jobMapper.selectByPrimaryKey(Integer.parseInt(jobID));
        switch (data) {
            case Constants.JOB_FINISHED:
//                job.setStatus(Constants.JOB_FINISHED_VALUE);
//                jobMapper.deleteByPrimaryKey(job.getId());
//                StringBuilder stringBuilder = new StringBuilder();
//                stringBuilder.append(Constants.JOB_DETAIL_PATH).append("/").append(jobID);
//                curatorClient.removePath(stringBuilder.toString());
//                curatorClient.removePath(path);
                break;
            case Constants.JOB_INIT:
                break;
            case Constants.JOB_EXCEPTION:
                job.setStatus(Constants.JOB_EXCEPTION_VALUE);
                jobMapper.updateByPrimaryKey(job);
                break;
            default:
                job.setNodeAssignedTo(Integer.parseInt(data));
                jobMapper.updateByPrimaryKey(job);
                break;
        }
    }
}
