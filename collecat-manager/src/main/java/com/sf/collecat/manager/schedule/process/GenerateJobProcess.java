package com.sf.collecat.manager.schedule.process;

import com.sf.collecat.common.model.Subtask;
import com.sf.collecat.manager.exception.job.JobPulishException;
import com.sf.collecat.manager.manage.JobManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLSyntaxErrorException;

/**
 * @author Hash Zhang
 * @version 1.0.0
 * @date 2016/7/8
 */
public class GenerateJobProcess implements Runnable {
    private final static Logger log = LoggerFactory.getLogger(GenerateJobProcess.class);
    private Subtask subtask;
    private JobManager jobManager;

    public GenerateJobProcess(Subtask subtask, JobManager jobManager) {
        this.subtask = subtask;
        this.jobManager = jobManager;
    }

    @Override
    public void run() {
        try {
            jobManager.publishJob(subtask);
        } catch (SQLSyntaxErrorException | JobPulishException e) {
            log.warn("",e);
        }
    }
}
