package com.sf.collecat.manager.schedule;

import com.sf.collecat.common.Constants;
import com.sf.collecat.common.mapper.JobMapper;
import com.sf.collecat.common.mapper.TaskMapper;
import com.sf.collecat.common.model.Job;
import com.sf.collecat.common.model.Task;
import com.sf.collecat.manager.zk.CuratorClient;
import it.sauronsoftware.cron4j.Scheduler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 调度类
 *
 * @author 862911 Hash Zhang
 * @version 1.0.0
 * @time 2016/6/21
 */
public class ScheduleCat {
    private TaskMapper taskMapper;
    private JobMapper jobMapper;
    private List<Task> tasks;
    private ExecutorService pool = Executors.newFixedThreadPool(10);
    private ExecutorService businessPool1 = Executors.newSingleThreadExecutor();
    private ExecutorService businessPool2 = Executors.newSingleThreadExecutor();
    private CuratorClient curatorClient;
    private CleanJob cleanJob;
    private ResetJob resetJob;

    public void init() {
        tasks = new ArrayList<>();
        List<Task> ttasks = taskMapper.selectAll();
        //// TODO: 2016/6/20 启动task
        for (Task task : ttasks) {
            scheduletask(task);
        }
    }

    public TaskMapper getTaskMapper() {
        return taskMapper;
    }

    public void setTaskMapper(TaskMapper taskMapper) {
        this.taskMapper = taskMapper;
    }

    public JobMapper getJobMapper() {
        return jobMapper;
    }

    public void setJobMapper(JobMapper jobMapper) {
        this.jobMapper = jobMapper;
    }

    public void createTask(String allocateRoutine, String initialSQL, String kafkaClusterName, int kafkaMessageSize,
                           String kafkaTopic, String kafkaTopicTokens, String kafkaUrl, Date lastTime, String messageFormat,
                           int routineTime, String schemaUsed, String field, boolean isActive) {
        Task task = new Task();
        task.setAllocateRoutine(allocateRoutine);
        task.setInitialSql(initialSQL);
        task.setIsActive(isActive);
        task.setKafkaClusterName(kafkaClusterName);
        task.setKafkaMessageSize(kafkaMessageSize);
        task.setKafkaTopic(kafkaTopic);
        task.setKafkaTopicTokens(kafkaTopicTokens);
        task.setKafkaUrl(kafkaUrl);
        task.setLastTime(lastTime);
        task.setMessageFormat(messageFormat);
        task.setRoutineTime(routineTime);
        task.setSchemaUsed(schemaUsed);
        task.setTimeField(field);
        scheduletask(task);
    }

    public void scheduletask(final Task task) {
        final Scheduler s = new Scheduler();
        task.setScheduler(s);
        tasks.add(task);
        s.schedule(task.getAllocateRoutine(), new Runnable() {
            public void run() {
                Worker worker = new Worker(task, curatorClient, jobMapper, taskMapper, s);
                pool.submit(worker);
            }
        });
        // Starts the scheduler.
        s.start();
    }

    public List<Job> getAllExceptionJob() {
        return jobMapper.selectAllExceptionJob();
    }

    public void resetAllExceptionJob() {
        List<Job> jobList = jobMapper.selectAllExceptionJob();
        for (Job job : jobList) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(Constants.JOB_PATH).append("/").append(job.getId());
            curatorClient.setData(stringBuilder.toString(), Constants.JOB_INIT);
            job.setStatus(Constants.JOB_INIT_VALUE);
            jobMapper.updateByPrimaryKey(job);
        }
    }

    public void removeAllTask() {
        for(Task task:tasks){
            task.getScheduler().stop();
            taskMapper.deleteByPrimaryKey(task.getId());
        }
        tasks = new ArrayList<>();
    }

    public void setCuratorClient(CuratorClient curatorClient) {
        this.curatorClient = curatorClient;
    }

    public void setCleanJob(final CleanJob cleanJob) {
        this.cleanJob = cleanJob;
        final Scheduler s = new Scheduler();
        s.schedule("* * * * *", new Runnable() {
            public void run() {
                businessPool1.submit(cleanJob);
            }
        });
        // Starts the scheduler.
        s.start();
    }

    public void insertTask(Task task) {
        taskMapper.insert(task);
    }

    public ResetJob getResetJob() {
        return resetJob;
    }

    public void setResetJob(final ResetJob resetJob) {
        this.resetJob = resetJob;
        final Scheduler s = new Scheduler();
        s.schedule("* * * * *", new Runnable() {
            public void run() {
                businessPool2.submit(resetJob);
            }
        });
        // Starts the scheduler.
        s.start();
    }
}
