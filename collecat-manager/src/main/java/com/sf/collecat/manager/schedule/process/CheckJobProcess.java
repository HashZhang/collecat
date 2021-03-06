package com.sf.collecat.manager.schedule.process;

import com.sf.collecat.manager.exception.job.JobAssignException;
import com.sf.collecat.manager.exception.job.JobCompleteException;
import com.sf.collecat.manager.exception.job.JobResetException;
import com.sf.collecat.manager.exception.job.JobSearchException;
import com.sf.collecat.manager.exception.node.NodeSearchException;
import com.sf.collecat.manager.manage.JobManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Job轮询检查类
 *
 * @author 862911 Hash Zhang
 * @version 1.0.0
 * @time 2016/6/21
 */
@Component
public class CheckJobProcess implements Runnable {
    private final static Logger log = LoggerFactory.getLogger(CheckJobProcess.class);
    @Autowired
    private JobManager jobManager;

    @Override
    public void run() {
        while (true) {
            try {
                jobManager.checkJobs();
                TimeUnit.SECONDS.sleep(1);
            } catch (Throwable e) {
                log.error("", e);
            }
        }
    }
}
