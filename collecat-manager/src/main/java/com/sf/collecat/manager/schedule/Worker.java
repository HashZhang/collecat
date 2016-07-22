package com.sf.collecat.manager.schedule;

import com.sf.collecat.common.model.Subtask;
import com.sf.collecat.manager.exception.subtask.SubtaskModifyException;
import com.sf.collecat.manager.manage.SubtaskManager;
import it.sauronsoftware.cron4j.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 工作线程
 *
 * @author 862911 Hash Zhang
 * @version 1.0.0
 * @time 2016/6/21
 */
public class Worker implements Runnable {
    private final static Logger log = LoggerFactory.getLogger(Worker.class);
    private Subtask subtask;
    private Scheduler scheduler;
    private SubtaskManager subtaskManager;

    public Worker(Subtask subtask, Scheduler scheduler, SubtaskManager subtaskManager) {
        this.subtask = subtask;
        this.scheduler = scheduler;
        this.subtaskManager = subtaskManager;
    }

    /**
     * 运行过程：
     */
    @Override
    public void run() {
        if (!subtask.getIsActive()) {
            return;
        }
        try {
            if (log.isInfoEnabled()) {
                log.info("task start:{}", subtask.toString());
            }
            subtaskManager.scheduleNow(subtask);
        } catch (SubtaskModifyException e) {
            log.error("",e);
        }
    }

    public Scheduler getScheduler() {
        return scheduler;
    }
}
