package com.sf.collecat.zk;

import com.sf.collecat.manager.zk.CuratorClient;
import com.sf.collecat.manager.zk.listener.JobListener;
import com.sf.collecat.manager.zk.listener.NodeListener;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.util.concurrent.TimeUnit;

/**
 * Created by 862911 on 2016/6/17.
 */
@ContextConfiguration(locations = {"/spring.xml"})
public class ZKTest extends AbstractJUnit4SpringContextTests {
    @Autowired
    private CuratorClient curatorClient;
    @Test
    public void testzk() throws InterruptedException {

        while (true) {
            TimeUnit.DAYS.sleep(1);
        }
    }
}
