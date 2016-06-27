package com.sf.collecat.manager.schedule;

import com.alibaba.fastjson.JSON;
import com.sf.collecat.common.Constants;
import com.sf.collecat.common.mapper.JobMapper;
import com.sf.collecat.common.mapper.TaskMapper;
import com.sf.collecat.common.model.Job;
import com.sf.collecat.common.model.Task;
import com.sf.collecat.common.utils.StrUtils;
import com.sf.collecat.manager.sql.SQLParser;
import com.sf.collecat.manager.zk.CuratorClient;
import it.sauronsoftware.cron4j.Scheduler;
import lombok.AllArgsConstructor;
import lombok.Generated;
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
    private Task task;
    @NonNull
    private CuratorClient curatorClient;
    @NonNull
    private JobMapper jobMapper;
    @NonNull
    private TaskMapper taskMapper;
    @Getter
    @NonNull
    private Scheduler scheduler;

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
            Date date = new Date();
            List<Job> jobs = SQLParser.parse(task, date);
            for (Job job : jobs) {
                jobMapper.insert(job);
                int id = job.getId();
                curatorClient.createPath(StrUtils.getZKJobDetailPath(id), JSON.toJSONString(job));
                curatorClient.createPath(StrUtils.getZKJobPath(id), Constants.JOB_INIT);
            }
            if (!jobs.isEmpty()) {
                task.setLastTime(date);
                taskMapper.updateByPrimaryKey(task);
            }
        } catch (SQLSyntaxErrorException e) {
            log.error("SQL syntax error for collecat!", e);
            task.setIsActive(false);
            taskMapper.updateByPrimaryKey(task);
        } catch (Exception e) {
            log.error("", e);
            task.setIsActive(false);
            taskMapper.updateByPrimaryKey(task);
        }
    }
}
