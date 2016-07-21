package com.sf.collecat.manager.schedule.process;

import com.sf.collecat.manager.exception.node.NodeRemoveException;
import com.sf.collecat.manager.exception.node.NodeSearchException;
import com.sf.collecat.manager.manage.NodeManager;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Component
public class CheckNodeProcess implements Runnable {
    @Autowired
    private NodeManager nodeManager;

    @Override
    public void run() {
        while (true) {
            try {
                nodeManager.checkNodes();
                TimeUnit.SECONDS.sleep(1);
            } catch (NodeSearchException e) {
                log.error("", e);
            } catch (NodeRemoveException e) {
                log.error("", e);
            } catch (Throwable e) {
                log.error("", e);
            }
        }
    }

}
