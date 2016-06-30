package com.sf.collecat.manager.zk.listener;

import com.sf.collecat.common.Constants;
import com.sf.collecat.common.mapper.JobMapper;
import com.sf.collecat.common.mapper.TaskMapper;
import com.sf.collecat.common.model.Job;
import com.sf.collecat.common.model.Task;
import com.sf.collecat.manager.exception.job.JobAssignException;
import com.sf.collecat.manager.exception.job.JobSearchException;
import com.sf.collecat.manager.manage.JobManager;
import com.sf.collecat.manager.zk.CuratorClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by 862911 on 2016/6/17.
 */
@Slf4j
@Component
public class JobListener implements TreeCacheListener {
    @Autowired
    private JobManager jobManager;

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
                try {
                    jobManager.setJobException(jobID);
                } catch (JobSearchException e) {
                    log.error("",e);
                }
                break;
            default:
                try {
                    jobManager.assignJob(jobID,data);
                } catch (JobSearchException e) {
                    log.error("",e);
                } catch (JobAssignException e) {
                    log.error("",e);
                }
                break;
        }
    }
}
