package com.sf.collecat.node.zk.listener;

import com.sf.collecat.common.Constants;
import com.sf.collecat.node.zk.CuratorClient;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Created by 862911 on 2016/6/17.
 */
public class NodeListener implements TreeCacheListener {
    private CuratorClient curatorClient;
    private static final Logger LOGGER = LoggerFactory.getLogger(CuratorClient.class);

    public NodeListener(CuratorClient curatorClient){
        this.curatorClient = curatorClient;
    }

    @Override
    public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent) throws Exception {
        switch (treeCacheEvent.getType()) {
            case NODE_UPDATED: {
                nodeUpdated(new String(treeCacheEvent.getData().getData(), Constants.STRING_ENCODING));
                break;
            }
            default:
                break;
        }
    }

    private void nodeUpdated(String data)  {
        if(data.indexOf(Constants.COMMON_SEPARATOR)<0) {
            return;
        }
        String id = data.substring(data.indexOf(Constants.COMMON_SEPARATOR) + 1);
        curatorClient.setNodeID(id);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Node[").append(id).append("]").append("Time[").append(new Date().getTime()).append("]");
        File file = new File(stringBuilder.toString());
        try {
            file.createNewFile();
        } catch (IOException e) {
            LOGGER.error("cannot create file! ",e);
        }
    }
}
