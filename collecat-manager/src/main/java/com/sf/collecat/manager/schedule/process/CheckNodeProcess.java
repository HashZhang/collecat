package com.sf.collecat.manager.schedule.process;

import com.sf.collecat.manager.exception.node.NodeRemoveException;
import com.sf.collecat.manager.exception.node.NodeSearchException;
import com.sf.collecat.manager.manage.NodeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 清理工作类
 *
 * @author 862911 Hash Zhang
 * @version 1.0.0
 * @time 2016/6/21
 */
@Component
public class CheckNodeProcess implements Runnable {
    private final static Logger log = LoggerFactory.getLogger(CheckJobProcess.class);
    @Autowired
    private NodeManager nodeManager;

    @Override
    public void run() {
        while (true) {
            try {
                nodeManager.checkNodes();
                TimeUnit.SECONDS.sleep(1);
            } catch (Throwable e) {
                log.error("", e);
            }
        }
    }

}
