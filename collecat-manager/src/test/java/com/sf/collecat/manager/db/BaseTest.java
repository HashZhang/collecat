package com.sf.collecat.manager.db;

import com.sf.collecat.common.Constants;
import com.sf.collecat.common.mapper.JobMapper;
import com.sf.collecat.common.mapper.NodeMapper;
import com.sf.collecat.common.mapper.TaskMapper;
import com.sf.collecat.common.model.Job;
import com.sf.collecat.common.model.Node;
import com.sf.collecat.common.model.Task;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

/**
 * Created by 862911 on 2016/6/16.
 */
@ContextConfiguration(locations = {"/spring.xml"})
@TestExecutionListeners(value = {TransactionalTestExecutionListener.class})
public class BaseTest extends AbstractJUnit4SpringContextTests {
    @Autowired
    NodeMapper nodeMapper;
    @Autowired
    TaskMapper taskMapper;
    @Autowired
    JobMapper jobMapper;

    @Test
    public void testNode() {
        Node node = new Node();
        System.out.println(nodeMapper.insert(node));
        System.out.println(nodeMapper.selectAll());
        node.setIp("10.202.4.39");
        System.out.println(nodeMapper.updateByPrimaryKey(node));
        System.out.println(nodeMapper.selectAll());
        Node node2 = nodeMapper.selectByPrimaryKey(node.getId());
        Assert.assertEquals(node.getIp(),node2.getIp());
        System.out.println(nodeMapper.deleteByPrimaryKey(node.getId()));
        System.out.println(nodeMapper.selectAll());
    }

    @Test
    public void testTask() {
        Task task = new Task();
        System.out.println(taskMapper.insert(task));
        System.out.println(taskMapper.selectAll());
        task.setInitialSql("select * from aaaa");
        System.out.println(taskMapper.updateByPrimaryKey(task));
        System.out.println(taskMapper.selectAll());
        Task task2 = taskMapper.selectByPrimaryKey(task.getId());
        Assert.assertEquals(task.getInitialSql(),task2.getInitialSql());
        System.out.println(taskMapper.deleteByPrimaryKey(task.getId()));
        System.out.println(taskMapper.selectAll());
    }

    @Test
    public void testJob() {
        Job job = new Job();
        System.out.println(jobMapper.insert(job));
        System.out.println(jobMapper.selectAllJob());
        job.setStatus(Constants.JOB_EXCEPTION_VALUE);
        System.out.println(jobMapper.updateByPrimaryKey(job));
        Job job2 = jobMapper.selectByPrimaryKey(job.getId());
        Assert.assertEquals(job.getStatus(),job2.getStatus());
        System.out.println(jobMapper.selectAllExceptionJob());
        System.out.println(jobMapper.deleteByPrimaryKey(job.getId()));
        System.out.println(jobMapper.selectAllJob());
    }
}