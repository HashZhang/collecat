package com.sf.collecat.manager.schedule;

import com.sf.collecat.common.Constants;
import com.sf.collecat.common.mapper.JobMapper;
import com.sf.collecat.common.mapper.NodeMapper;
import com.sf.collecat.common.model.Job;
import com.sf.collecat.common.model.Node;
import com.sf.collecat.manager.zk.CuratorClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 862911 on 2016/6/20.
 */
@Component
public class CleanJob implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Runnable.class);
    @Autowired
    private CuratorClient curatorClient;
    @Autowired
    private JobMapper jobMapper;
    @Autowired
    private NodeMapper nodeMapper;

    @Override
    public void run() {
        List<String> children = curatorClient.getChildren(Constants.JOB_PATH);
        List<String> nodes = getNodes();
        LOGGER.info(children.toString());
        for (String child : children) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(Constants.JOB_PATH).append("/").append(child);
            String path1 = stringBuilder.toString();
            stringBuilder = new StringBuilder();
            stringBuilder.append(Constants.JOB_DETAIL_PATH).append("/").append(child);
            String path2 = stringBuilder.toString();
            String data = curatorClient.getData(path1);
            switch (data) {
                case Constants.JOB_FINISHED:
                    Job job = jobMapper.selectByPrimaryKey(Integer.parseInt(child));
                    if (job != null) {
                        job.setStatus(Constants.JOB_FINISHED_VALUE);
                        jobMapper.deleteByPrimaryKey(job.getId());
                    }
                    curatorClient.removePath(path2);
                    curatorClient.removePath(path1);
                    break;
                case Constants.JOB_INIT:
                    break;
                case Constants.JOB_EXCEPTION:
                    break;
                default:
                    job = jobMapper.selectByPrimaryKey(Integer.parseInt(child));
                    int id = job.getNodeAssignedTo();
                    Node node = nodeMapper.selectByPrimaryKey(id);
                    if (node == null) {
                        curatorClient.setData(path1, Constants.JOB_INIT);
                    }
                    boolean isContained = false;
                    for (String n : nodes) {
                        if (n.contains(data)) {
                            isContained = true;
                        }
                    }
                    if (!isContained) {
                        curatorClient.setData(path1, Constants.JOB_INIT);
                    }
                    break;
            }
        }
    }

    private List<String> getNodes() {
        List<String> ids = new ArrayList<>();
        List<String> nodes = curatorClient.getChildren(Constants.NODE_PATH);
        for (String node : nodes) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(Constants.NODE_PATH).append("/").append(node);
            String Node = stringBuilder.toString();
            String data = curatorClient.getData(Node);
            if (data != null && data.contains(":")) {
                String id = data.substring(data.indexOf(":") + 1);
                ids.add(id);
            }
        }
        return ids;
    }
}
