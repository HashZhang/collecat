package com.sf.collecat.manager.zk.listener;

import com.sf.collecat.common.Constants;
import com.sf.collecat.common.mapper.JobMapper;
import com.sf.collecat.common.mapper.NodeMapper;
import com.sf.collecat.common.model.Job;
import com.sf.collecat.common.model.Node;
import com.sf.collecat.manager.zk.CuratorClient;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by 862911 on 2016/6/17.
 */
@Component
public class NodeListener implements TreeCacheListener {
    @Autowired
    private NodeMapper nodeMapper;
    @Autowired
    private JobMapper jobMapper;

    @Autowired
    private CuratorClient curatorClient;

    @Override
    public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent) throws Exception {
        switch (treeCacheEvent.getType()) {
            case NODE_ADDED: {
                break;
            }
            case NODE_UPDATED:{
                addNode(treeCacheEvent.getData().getPath(),new String(treeCacheEvent.getData().getData(), Constants.STRING_ENCODING));
                break;
            }
            case NODE_REMOVED: {
                removeNode(new String(treeCacheEvent.getData().getData(), Constants.STRING_ENCODING));
                break;
            }
            default:
                break;
        }
    }

    private void addNode(String path,String ip) {
        if(Constants.NODE_PATH.equals(path)||ip.contains(Constants.COMMON_SEPARATOR)) {
            return;
        }
        String currentNode = path.substring(path.lastIndexOf("/") + 1);
        Node node = new Node();
        node.setCurrentNodeId(currentNode);
        node.setIp(ip);
        nodeMapper.insert(node);
        int id = node.getId();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(ip).append(Constants.COMMON_SEPARATOR).append(id);
        curatorClient.setData(path,stringBuilder.toString());
    }

    private void removeNode(String data){
        if(!data.contains(Constants.COMMON_SEPARATOR)) {
            return;
        }
        int id = Integer.parseInt(data.substring(data.lastIndexOf(Constants.COMMON_SEPARATOR) + 1));
        nodeMapper.deleteByPrimaryKey(id);
//        List<Job> jobList = jobMapper.selectAllUncompletedByNodeID(id);
//        for(Job job:jobList){
//            StringBuilder stringBuilder = new StringBuilder();
//            stringBuilder.append(Constants.JOB_PATH).append("/").append(job.getId());
//            curatorClient.setData(stringBuilder.toString(),Constants.JOB_INIT);
//        }
    }
}
