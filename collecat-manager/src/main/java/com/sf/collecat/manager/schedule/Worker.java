package com.sf.collecat.manager.schedule;

import com.alibaba.fastjson.JSON;
import com.sf.collecat.common.Constants;
import com.sf.collecat.common.mapper.JobMapper;
import com.sf.collecat.common.mapper.TaskMapper;
import com.sf.collecat.common.model.Job;
import com.sf.collecat.common.model.Task;
import com.sf.collecat.common.utils.StrUtils;
import com.sf.collecat.manager.exception.job.JobPulishException;
import com.sf.collecat.manager.exception.task.TaskModifyException;
import com.sf.collecat.manager.manage.JobManager;
import com.sf.collecat.manager.manage.TaskManager;
import com.sf.collecat.manager.sql.SQLParser;
import com.sf.collecat.manager.zk.CuratorClient;
import it.sauronsoftware.cron4j.Scheduler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLSyntaxErrorException;
import java.util.Date;
import java.util.List;

/**
 * 工作线程
 *
 * @author 862911 Hash Zhang
 * @version 1.0.0
 * @time 2016/6/21
 */
@Slf4j
@AllArgsConstructor
public class Worker implements Runnable {
    @NonNull
    private Task task;
    @Getter
    @NonNull
    private Scheduler scheduler;
    @NonNull
    private JobManager jobManager;
    @NonNull
    private TaskManager taskManager;
    /**
     * 运行过程：
     */
    @Override
    public void run() {
        if (!task.getIsActive()) {
            return;
        }
        try {
            if (log.isInfoEnabled()) {
                log.info("task start:{}", task.toString());
            }
            jobManager.publishJob(task);
        } catch (SQLSyntaxErrorException e) {
            log.error("SQL syntax error for collecat!", e);
            task.setIsActive(false);
            try {
                taskManager.modifyTask(task);
            } catch (Exception e1) {
                log.error("",e1);
            }
        } catch (JobPulishException e) {
            log.error("",e);
        }
    }
}
