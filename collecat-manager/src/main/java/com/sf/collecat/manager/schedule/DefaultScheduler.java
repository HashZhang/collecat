package com.sf.collecat.manager.schedule;

import com.sf.collecat.common.model.Subtask;
import com.sf.collecat.manager.manage.SubtaskManager;
import com.sf.collecat.manager.schedule.process.CheckJobProcess;
import com.sf.collecat.manager.schedule.process.CheckNodeProcess;
import com.sf.collecat.manager.schedule.process.ResetExceptionJobProcess;
import com.sf.collecat.manager.schedule.process.RoundRobinTaskProcess;
import it.sauronsoftware.cron4j.Scheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 调度类
 *
 * @author 862911 Hash Zhang
 * @version 1.0.0
 * @time 2016/6/21
 */
public class DefaultScheduler {
    private ExecutorService schedulePool = Executors.newSingleThreadExecutor();
    private ExecutorService updatePool = Executors.newFixedThreadPool(10);
    private ExecutorService checkPool1 = Executors.newSingleThreadExecutor();
    private ExecutorService checkPool2 = Executors.newSingleThreadExecutor();
    private ExecutorService checkPool3 = Executors.newSingleThreadExecutor();
    private CheckNodeProcess checkNodeProcess;
    private CheckJobProcess checkJobProcess;
    private ResetExceptionJobProcess resetExceptionJobProcess;
    private RoundRobinTaskProcess roundRobinTaskProcess;
    private SubtaskManager subtaskManager;

    public void scheduleTask(final Subtask subtask) {
        final Scheduler scheduler = new Scheduler();
        subtask.setScheduler(scheduler);
        scheduler.schedule(subtask.getAllocateRoutine(), new Runnable() {
            public void run() {
                Worker worker = new Worker(subtask, scheduler ,subtaskManager);
                updatePool.submit(worker);
            }
        });
        // Starts the scheduler.
        scheduler.start();
    }

    public void cancelTask(Subtask subtask) {
        subtask.getScheduler().stop();
    }

    public void setCheckNodeProcess(final CheckNodeProcess checkNodeProcess) {
        this.checkNodeProcess = checkNodeProcess;
        checkPool2.submit(checkNodeProcess);
    }

    public void setCheckJobProcess(final CheckJobProcess checkJobProcess) {
        this.checkJobProcess = checkJobProcess;
        checkPool1.submit(checkJobProcess);
    }


    public void setRoundRobinTaskProcess(RoundRobinTaskProcess roundRobinTaskProcess) {
        this.roundRobinTaskProcess = roundRobinTaskProcess;
        schedulePool.submit(roundRobinTaskProcess);
    }

    public void setSubtaskManager(SubtaskManager subtaskManager) {
        this.subtaskManager = subtaskManager;
    }

    public void setResetExceptionJobProcess(ResetExceptionJobProcess resetExceptionJobProcess) {
        this.resetExceptionJobProcess = resetExceptionJobProcess;
        checkPool3.submit(resetExceptionJobProcess);
    }
}
