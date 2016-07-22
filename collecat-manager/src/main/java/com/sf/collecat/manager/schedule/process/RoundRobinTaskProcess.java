package com.sf.collecat.manager.schedule.process;

import com.sf.collecat.common.model.Subtask;
import com.sf.collecat.common.model.Task;
import com.sf.collecat.manager.manage.JobManager;
import com.sf.collecat.manager.manage.TaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Hash Zhang
 * @version 1.0.0
 * @date 2016/7/8
 */
@Component
public class RoundRobinTaskProcess implements Runnable {
    private final static Logger log = LoggerFactory.getLogger(RoundRobinTaskProcess.class);
    @Autowired
    private TaskManager taskManager;
    @Autowired
    private JobManager jobManager;
    private ExecutorService generateJobExecutorPool = Executors.newFixedThreadPool(10);

    @Override
    public void run() {
        while(taskManager==null){
            Thread.currentThread().yield();
        }
        while (true) {
            try {
                Map<Integer, Task> taskMap = taskManager.getTaskMap();
                for (Task task : taskMap.values()) {
                    if (task.getIsActive()) {
                        Map<Integer, Subtask> subtaskHashMap = task.getSubtaskHashMap();
                        for (Subtask subtask : subtaskHashMap.values()) {
                            if (subtask.getIsActive()) {
                                GenerateJobProcess generateJobProcess = new GenerateJobProcess(subtask, jobManager);
                                generateJobExecutorPool.submit(generateJobProcess);
                            }
                        }
                    }
                }
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                log.warn("",e);
            }
        }
    }
}
