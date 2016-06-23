package com.sf.collecat.manager.schedule;

import com.alibaba.fastjson.JSON;
import com.sf.collecat.common.Constants;
import com.sf.collecat.common.mapper.JobMapper;
import com.sf.collecat.common.mapper.TaskMapper;
import com.sf.collecat.common.model.Job;
import com.sf.collecat.common.model.Task;
import com.sf.collecat.manager.sql.SQLParser;
import com.sf.collecat.manager.zk.CuratorClient;
import it.sauronsoftware.cron4j.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLSyntaxErrorException;
import java.util.Date;
import java.util.List;

/**
 * Created by 862911 on 2016/6/17.
 */
public class Worker implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Worker.class);
    private Task task;
    private CuratorClient curatorClient;
    private JobMapper jobMapper;
    private TaskMapper taskMapper;
    private Scheduler scheduler;

    public Worker(Task task, CuratorClient curatorClient, JobMapper jobMapper, TaskMapper taskMapper, Scheduler scheduler) {
        this.task = task;
        this.curatorClient = curatorClient;
        this.jobMapper = jobMapper;
        this.taskMapper = taskMapper;
        this.scheduler = scheduler;
    }

    @Override
    public void run() {
        if (!task.getIsActive()) {
            return;
        }
        try {
            LOGGER.info("task start:{}",task.toString());
            Date date = new Date();
            List<Job> jobs = SQLParser.parse(task, date);
            for (Job job : jobs) {
                jobMapper.insert(job);
                int id = job.getId();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(Constants.JOB_DETAIL_PATH).append("/").append(id);
                curatorClient.createPath(stringBuilder.toString(), JSON.toJSONString(job));
                stringBuilder = new StringBuilder();
                stringBuilder.append(Constants.JOB_PATH).append("/").append(id);
                curatorClient.createPath(stringBuilder.toString(), Constants.JOB_INIT);
            }
            if (!jobs.isEmpty()) {
                task.setLastTime(date);
                taskMapper.updateByPrimaryKey(task);
            }
        } catch (SQLSyntaxErrorException e) {
            LOGGER.error("SQL syntax error for collecat!", e);
            task.setIsActive(false);
            taskMapper.updateByPrimaryKey(task);
        } catch (Exception e) {
            LOGGER.error("", e);
            task.setIsActive(false);
            taskMapper.updateByPrimaryKey(task);
        }
    }

    public Scheduler getScheduler() {
        return scheduler;
    }
}
