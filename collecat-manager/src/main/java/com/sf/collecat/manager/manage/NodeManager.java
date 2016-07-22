package com.sf.collecat.manager.manage;

import com.sf.collecat.common.Constants;
import com.sf.collecat.common.mapper.NodeMapper;
import com.sf.collecat.common.model.Node;
import com.sf.collecat.common.utils.StrUtils;
import com.sf.collecat.manager.exception.node.NodeAddException;
import com.sf.collecat.manager.exception.node.NodeRemoveException;
import com.sf.collecat.manager.exception.node.NodeSearchException;
import com.sf.collecat.manager.zk.CuratorClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Node管理类，管理所有关于Node的操作
 *
 * @author 862911 Hash Zhang
 * @version 1.0.0
 * @time 2016/6/29
 */
public class NodeManager {
    private final static Logger log = LoggerFactory.getLogger(NodeManager.class);
    @Autowired
    private NodeMapper nodeMapper;
    @Autowired
    private CuratorClient curatorClient;

    public void addNode(String path, String ip) throws NodeAddException {
        String currentNode = path.substring(path.lastIndexOf("/") + 1);
        Node node = new Node();
        node.setCurrentNodeId(currentNode);
        node.setIp(ip);
        try {
            nodeMapper.insert(node);
            int id = node.getId();
            curatorClient.setData(path, StrUtils.makeString(ip, Constants.COMMON_SEPARATOR, id));
        } catch (Exception e) {
            throw new NodeAddException(e);
        }
    }

    public void removeNode(String data) throws NodeRemoveException {
        try {
            int id = Integer.parseInt(data.substring(data.lastIndexOf(Constants.COMMON_SEPARATOR) + 1));
            nodeMapper.deleteByPrimaryKey(id);
        } catch (Exception e) {
            throw new NodeRemoveException(e);
        }
    }

    public void removeNode(Node node) throws NodeRemoveException {
        try {
            nodeMapper.deleteByPrimaryKey(node.getId());
        } catch (Exception e) {
            throw new NodeRemoveException(e);
        }
    }

    public void checkNodes() throws NodeSearchException, NodeRemoveException {
        List<Node> nodeListDB = getAllNodesFromDB();
        List<Integer> nodeListZK = getAllNodeFromZK();
        if(log.isInfoEnabled()){
            log.info("Checking Nodes:{}",nodeListZK);
        }
        for (Node node : nodeListDB) {
            boolean alive = false;
            for (int i : nodeListZK) {
                if (i == node.getId()) {
                    alive = true;
                    break;
                }
            }
            if(!alive){
                removeNode(node);
            }
        }
    }

    public List<Node> getAllNodesFromDB() throws NodeSearchException {
        try {
            return nodeMapper.selectAll();
        } catch (Exception e) {
            throw new NodeSearchException(e);
        }
    }

    public List<Integer> getAllNodeFromZK() throws NodeSearchException {
        try {
            List<String> children = curatorClient.getChildren(Constants.NODE_PATH);
            List<Integer> nodes = new ArrayList<>();
            for (String child : children) {
                String data = curatorClient.getData(StrUtils.makeString(Constants.NODE_PATH, Constants.ZK_SEPARATOR, child));
                if (data.contains(Constants.COMMON_SEPARATOR)) {
                    nodes.add(Integer.parseInt(data.substring(data.lastIndexOf(Constants.COMMON_SEPARATOR) + 1)));
                }else{
                    addNode(StrUtils.makeString(Constants.NODE_PATH, Constants.ZK_SEPARATOR, child), data);
                }
            }
            return nodes;
        } catch (Exception e) {
            throw new NodeSearchException(e);
        }
    }
}
