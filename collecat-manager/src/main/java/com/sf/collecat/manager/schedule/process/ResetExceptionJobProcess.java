package com.sf.collecat.manager.schedule.process;

import com.sf.collecat.common.model.Job;
import com.sf.collecat.manager.manage.JobManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Hash Zhang
 * @version 1.0.0
 * @date 2016/8/3
 */
@Component
public class ResetExceptionJobProcess implements Runnable {
    private final static Logger log = LoggerFactory.getLogger(CheckJobProcess.class);
    @Autowired
    private JobManager jobManager;

    @Override
    public void run() {
        while (true) {
            try {
                List<Job> jobList = jobManager.selectAllExceptionJob();
                for (Job job : jobList) {
                    jobManager.resetJob(job);
                }
                TimeUnit.DAYS.sleep(1);
            } catch (Throwable e) {
                log.error("", e);
            }
        }
    }
}
