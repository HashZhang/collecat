package com.sf.collecat.db;

import com.sf.collecat.common.mapper.NodeMapper;
import com.sf.collecat.common.mapper.TaskMapper;
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

    @Test
    public void testBillKind() {
        System.out.println(taskMapper.selectAll());
    }
}