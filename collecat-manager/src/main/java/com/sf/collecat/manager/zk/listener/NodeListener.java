package com.sf.collecat.manager.zk.listener;

import com.sf.collecat.common.Constants;
import com.sf.collecat.manager.exception.node.NodeAddException;
import com.sf.collecat.manager.exception.node.NodeRemoveException;
import com.sf.collecat.manager.manage.NodeManager;
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
public class NodeListener implements TreeCacheListener {
    @Autowired
    private NodeManager nodeManager;

    @Override
    public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent) throws Exception {
        switch (treeCacheEvent.getType()) {
            case NODE_ADDED: {
                break;
            }
            case NODE_UPDATED: {
                addNode(treeCacheEvent.getData().getPath(), new String(treeCacheEvent.getData().getData(), Constants.STRING_ENCODING));
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

    private void addNode(String path, String ip) {
        if (Constants.NODE_PATH.equals(path) || ip.contains(Constants.COMMON_SEPARATOR)) {
            return;
        }
        try {
            nodeManager.addNode(path, ip);
        } catch (NodeAddException e) {
            log.error("", e);
        }
    }

    private void removeNode(String data) {
        if (!data.contains(Constants.COMMON_SEPARATOR)) {
            return;
        }
        try {
            nodeManager.removeNode(data);
        } catch (NodeRemoveException e) {
            log.error("", e);
        }
    }
}
