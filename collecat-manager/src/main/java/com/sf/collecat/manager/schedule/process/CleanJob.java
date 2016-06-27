package com.sf.collecat.manager.schedule.process;

import com.sf.collecat.common.Constants;
import com.sf.collecat.common.mapper.JobMapper;
import com.sf.collecat.common.mapper.NodeMapper;
import com.sf.collecat.common.model.Job;
import com.sf.collecat.common.model.Node;
import com.sf.collecat.common.utils.StrUtils;
import com.sf.collecat.manager.zk.CuratorClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 清理工作类
 *
 * @author 862911 Hash Zhang
 * @version 1.0.0
 * @time 2016/6/21
 */
@Slf4j
@Component
public class CleanJob implements Runnable {
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
        if (log.isInfoEnabled()) {
            log.info(StrUtils.makeString("Start cleaning! Current nodes:", children.toString()));
        }
        for (String child : children) {
            String path1 = StrUtils.getZKJobPath(child);
            String path2 = StrUtils.getZKJobDetailPath(child);
            String data = curatorClient.getData(path1);
            switch (data) {
                case Constants.JOB_FINISHED:
                    handleJobFinished(child, path1, path2);
                    break;
                case Constants.JOB_INIT:
                    break;
                case Constants.JOB_EXCEPTION:
                    break;
                default:
                    handleJobAllocated(child, path1, nodes, data);
                    break;
            }
        }
    }

    /**
     * 获取所有node，同时清理数据库中失效的Node（考虑manager挂掉时有node挂掉）
     *
     * @return 所有node id的集合
     */
    private List<String> getNodes() {
        List<Node> nodesInDB = nodeMapper.selectAll();
        List<String> ids = new ArrayList<>();
        List<String> nodes = curatorClient.getChildren(Constants.NODE_PATH);
        for (Node node : nodesInDB) {
            if (!nodes.contains(StrUtils.makeString(node.getId()))) {
                nodeMapper.deleteByPrimaryKey(node.getId());
            }
        }
        for (String node : nodes) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(Constants.NODE_PATH).append("/").append(node);
            String Node = stringBuilder.toString();
            String data = curatorClient.getData(Node);
            if (data != null && data.contains(Constants.COMMON_SEPARATOR)) {
                String id = data.substring(data.indexOf(Constants.COMMON_SEPARATOR) + 1);
                ids.add(id);
            }
        }
        return ids;
    }

    /**
     * 遇到Job ZK状态为finished，将Jobn清除，为了保证逻辑严密还有容灾，需要先删除数据库，再删除ZK对应的Job
     *
     * @param child job id
     * @param path1 job状态路径
     * @param path2 job明细路径
     */
    private void handleJobFinished(String child, String path1, String path2) {
        Job job = jobMapper.selectByPrimaryKey(Integer.parseInt(child));
        if (job != null) {
            job.setStatus(Constants.JOB_FINISHED_VALUE);
            jobMapper.deleteByPrimaryKey(job.getId());
        }
        curatorClient.removePath(path2);
        curatorClient.removePath(path1);
        if (log.isInfoEnabled()) {
            log.info(StrUtils.makeString("Job finished and removed:", job.getId()));
        }
    }

    /**
     * 遇到Job ZK状态为已分配node，需要判断node是否存活，如果没有存活，则删除
     *
     * @param child
     * @param path1
     * @param nodes
     * @param data
     */
    private void handleJobAllocated(String child, String path1, List<String> nodes, String data) {
        Job job = jobMapper.selectByPrimaryKey(Integer.parseInt(child));
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
            if (log.isInfoEnabled()) {
                log.info(StrUtils.makeString("Job allocated node is down, reset it, Job id:", job.getId()));
            }
            curatorClient.setData(path1, Constants.JOB_INIT);
        }
    }
}
