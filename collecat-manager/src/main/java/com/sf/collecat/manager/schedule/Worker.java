package com.sf.collecat.manager.schedule;

import com.alibaba.fastjson.JSON;
import com.sf.collecat.common.Constants;
import com.sf.collecat.common.mapper.JobMapper;
import com.sf.collecat.common.mapper.TaskMapper;
import com.sf.collecat.common.model.Job;
import com.sf.collecat.common.model.Subtask;
import com.sf.collecat.common.model.Task;
import com.sf.collecat.common.utils.StrUtils;
import com.sf.collecat.manager.exception.job.JobPulishException;
import com.sf.collecat.manager.exception.subtask.SubtaskModifyException;
import com.sf.collecat.manager.exception.task.TaskModifyException;
import com.sf.collecat.manager.manage.JobManager;
import com.sf.collecat.manager.manage.SubtaskManager;
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
    private Subtask subtask;
    @Getter
    @NonNull
    private Scheduler scheduler;
    @NonNull
    private SubtaskManager subtaskManager;
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
}
