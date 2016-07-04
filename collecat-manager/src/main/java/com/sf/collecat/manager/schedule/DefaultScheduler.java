package com.sf.collecat.manager.schedule;

import com.sf.collecat.common.mapper.JobMapper;
import com.sf.collecat.common.mapper.TaskMapper;
import com.sf.collecat.common.model.Task;
import com.sf.collecat.manager.manage.JobManager;
import com.sf.collecat.manager.manage.NodeManager;
import com.sf.collecat.manager.manage.TaskManager;
import com.sf.collecat.manager.schedule.process.CheckJobProcess;
import com.sf.collecat.manager.schedule.process.CheckNodeProcess;
import com.sf.collecat.manager.sql.SQLParser;
import com.sf.collecat.manager.zk.CuratorClient;
import it.sauronsoftware.cron4j.Scheduler;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
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
    private ExecutorService pool = Executors.newFixedThreadPool(10);
    private ExecutorService businessPool1 = Executors.newSingleThreadExecutor();
    private ExecutorService businessPool2 = Executors.newSingleThreadExecutor();
    private CheckNodeProcess checkNodeProcess;
    private CheckJobProcess checkJobProcess;
    @Setter
    private JobManager jobManager;
    @Setter
    private TaskManager taskManager;

    public void scheduleTask(final Task task) {
        final Scheduler scheduler = new Scheduler();
        task.setScheduler(scheduler);
        scheduler.schedule(task.getAllocateRoutine(), new Runnable() {
            public void run() {
                Worker worker = new Worker(task, scheduler ,jobManager, taskManager);
                pool.submit(worker);
            }
        });
        // Starts the scheduler.
        scheduler.start();
    }

    public void cancelTask(Task task) {

        task.getScheduler().stop();
    }

    public void setCheckNodeProcess(final CheckNodeProcess checkNodeProcess) {
        this.checkNodeProcess = checkNodeProcess;
        final Scheduler s = new Scheduler();
        s.schedule("* * * * *", new Runnable() {
            public void run() {
                businessPool1.submit(checkNodeProcess);
            }
        });
        // Starts the scheduler.
        s.start();
    }

    public void setCheckJobProcess(final CheckJobProcess checkJobProcess) {
        this.checkJobProcess = checkJobProcess;
        final Scheduler s = new Scheduler();
        s.schedule("* * * * *", new Runnable() {
            public void run() {
                businessPool2.submit(checkJobProcess);
            }
        });
        // Starts the scheduler.
        s.start();
    }
}
