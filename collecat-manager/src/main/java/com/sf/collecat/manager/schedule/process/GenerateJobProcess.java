package com.sf.collecat.manager.schedule.process;

import com.sf.collecat.common.model.Subtask;
import com.sf.collecat.manager.exception.job.JobPulishException;
import com.sf.collecat.manager.manage.JobManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLSyntaxErrorException;

/**
 * @author Hash Zhang
 * @version 1.0.0
 * @date 2016/7/8
 */
@Slf4j
@AllArgsConstructor
public class GenerateJobProcess implements Runnable {
    private Subtask subtask;
    private JobManager jobManager;

    @Override
    public void run() {
        try {
            jobManager.publishJob(subtask);
        } catch (SQLSyntaxErrorException | JobPulishException e) {
            log.warn("",e);
        }
    }
}
