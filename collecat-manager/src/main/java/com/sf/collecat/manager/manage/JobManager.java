package com.sf.collecat.manager.manage;

import com.alibaba.fastjson.JSON;
import com.sf.collecat.common.Constants;
import com.sf.collecat.common.mapper.JobMapper;
import com.sf.collecat.common.mapper.NodeMapper;
import com.sf.collecat.common.mapper.SubtaskMapper;
import com.sf.collecat.common.model.Job;
import com.sf.collecat.common.model.Node;
import com.sf.collecat.common.model.Subtask;
import com.sf.collecat.common.utils.StrUtils;
import com.sf.collecat.manager.exception.job.*;
import com.sf.collecat.manager.exception.node.NodeSearchException;
import com.sf.collecat.manager.sql.SQLParser;
import com.sf.collecat.manager.zk.CuratorClient;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.sql.SQLSyntaxErrorException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Job管理类，管理所有关于Job的操作
 * 涉及到数据库的操作都是数据库优先，保证数据库修改成功再修改缓存
 *
 * @author 862911 Hash Zhang
 * @version 1.0.0
 * @time 2016/6/27
 */
@Slf4j
public class JobManager {
    @Autowired
    private SubtaskMapper subtaskMapper;
    @Autowired
    private JobMapper jobMapper;
    @Autowired
    private NodeMapper nodeMapper;
    @Autowired
    private CuratorClient curatorClient;
    @Autowired
    private SQLParser sqlParser;

    @Value("${job.pool.size}")
    private int jobPoolSize;
    private AtomicBoolean poolLock = new AtomicBoolean(false);

    public boolean hasException(Subtask subtask) {
        if (jobMapper.countExceptionJobsWithSubtaskId(subtask.getId()) > 0) {
            return true;
        } else {
            return false;
        }
    }

    public void clearJobs() throws JobRemoveException {
        List<String> children = getALLJobsFromZK();
        for (String child : children) {
            removeJob(child);
        }
    }

    public void checkJobs() throws JobCompleteException, JobSearchException, JobResetException, JobAssignException, NodeSearchException {
        List<String> children = getALLJobsFromZK();
        if (log.isInfoEnabled()) {
            log.info("Checking Jobs:{}", children);
        }
        for (String child : children) {
            String data = curatorClient.getData(StrUtils.getZKJobPath(child));
            switch (data) {
                case Constants.JOB_FINISHED:
                    completeJob(child);
                    break;
                case Constants.JOB_EXCEPTION:
                    setJobException(child);
                    break;
                case Constants.JOB_INIT:
                    break;
                default:
                    assignJob(child, data);
                    checkJobAssigned(child);
                    break;
            }
        }
    }

    public void checkJobAssigned(String jobId) throws JobSearchException, JobAssignException, NodeSearchException, JobResetException {
        if (log.isInfoEnabled()) {
            log.info(StrUtils.makeString("Job:", jobId, " is being checked!"));
        }
        int nodeAssigned = Integer.parseInt(curatorClient.getData(StrUtils.getZKJobPath(jobId)));
        Job job = null;
        try {
            job = jobMapper.selectByPrimaryKey(Integer.parseInt(jobId));
        } catch (Exception e) {
            throw new JobSearchException(e);
        }
        boolean alive = false;
        List<Node> nodeList = null;
        try {
            nodeList = nodeMapper.selectAll();

        } catch (Exception e) {
            throw new NodeSearchException(e);
        }
        for (Node node : nodeList) {
            if (node.getId() == nodeAssigned) {
                alive = true;
                break;
            }
        }
        if (!alive) {
            resetJob(job);
            if (log.isInfoEnabled()) {
                log.info(StrUtils.makeString("Job:", jobId, " is reset because its node assigned is down!"));
            }
        }
    }

    public List<String> getALLJobsFromZK() {
        return curatorClient.getChildren(Constants.JOB_PATH);
    }

    public void publishJob(@NonNull Subtask subtask) throws SQLSyntaxErrorException, JobPulishException {
        Job job = sqlParser.parse(subtask);
        try {
            if (job == null) {
                return;
            }
            if (persistJob(job)) {
                subtask.setLastTime(job.getTimeFieldEnd());
                subtaskMapper.updateByPrimaryKey(subtask);
            } else {
                log.info("Subtask-{}'s pool is full!", subtask.getId());
            }
        } catch (Exception e) {
            throw new JobPulishException(e);
        }
    }

    public void publishJob(@NonNull Job job) throws SQLSyntaxErrorException, JobPulishException {
        try {
            persistJob(job);
        } catch (Exception e) {
            throw new JobPulishException(e);
        }
    }

    private boolean persistJob(Job job) {
        try {
            while (poolLock.compareAndSet(false, true)) {
                Thread.currentThread().yield();
            }
            if (job.getSubtaskId() != null) {
                int countOfJobs = jobMapper.countJobsWithSubtaskId(job.getSubtaskId());
                if (countOfJobs >= jobPoolSize) {
                    return false;
                }
            }
            jobMapper.insert(job);
            int id = job.getId();
            curatorClient.createPath(StrUtils.getZKJobDetailPath(id), JSON.toJSONString(job));
            curatorClient.createPath(StrUtils.getZKJobPath(id), Constants.JOB_INIT);
            return true;
        } finally {
            poolLock.set(false);
        }
    }

    public void assignJob(@NonNull String jobId, String nodeId) throws JobSearchException, JobAssignException {
        Job job = null;
        try {
            job = jobMapper.selectByPrimaryKey(Integer.parseInt(jobId));
        } catch (Exception e) {
            throw new JobSearchException(e);
        }
        job.setNodeAssignedTo(Integer.parseInt(nodeId));
        try {
            jobMapper.updateByPrimaryKey(job);
        } catch (Exception e) {
            throw new JobAssignException(e);
        }
    }

    /**
     * 完成job
     *
     * @param jobId
     * @throws JobCompleteException
     * @throws JobSearchException
     */
    public void completeJob(@NonNull String jobId) throws JobCompleteException, JobSearchException {
        Job job = null;
        try {
            job = jobMapper.selectByPrimaryKey(Integer.parseInt(jobId));
        } catch (Exception e) {
            throw new JobSearchException(e);
        }
        if (job != null) {
            completeJob(job);
        } else {
            try {
                curatorClient.removePath(StrUtils.getZKJobDetailPath(jobId));
                curatorClient.removePath(StrUtils.getZKJobPath(jobId));
            } catch (Exception e) {
                throw new JobCompleteException(e);
            }
        }
    }

    /**
     * 完成job
     *
     * @param job
     * @throws JobCompleteException
     */
    public void completeJob(@NonNull Job job) throws JobCompleteException {
        job.setStatus(Constants.JOB_FINISHED_VALUE);
        try {
            jobMapper.deleteByPrimaryKey(job.getId());
            curatorClient.removePath(StrUtils.getZKJobDetailPath(job.getId()));
            curatorClient.removePath(StrUtils.getZKJobPath(job.getId()));
        } catch (Exception e) {
            throw new JobCompleteException(e);
        }
//        returnCap(1);
        if (log.isInfoEnabled()) {
            log.info(StrUtils.makeString("Job finished and removed:", job.getId()));
        }
    }

    public void resetJob(@NonNull String jobId) throws JobResetException, JobSearchException {
        Job job = null;
        try {
            job = jobMapper.selectByPrimaryKey(Integer.parseInt(jobId));
        } catch (Exception e) {
            throw new JobSearchException(e);
        }
        if (job.getStatus() == Constants.JOB_EXCEPTION_VALUE) {
            resetJob(job);
        }
    }

    public void resetJob(@NonNull Job job) throws JobResetException {
        job.setStatus(Constants.JOB_INIT_VALUE);
        try {
            jobMapper.updateByPrimaryKey(job);
            curatorClient.setData(StrUtils.getZKJobPath(job.getId()), Constants.JOB_INIT);
        } catch (Exception e) {
            throw new JobResetException(e);
        }
    }

    public void removeJob(@NonNull String JobId) throws JobRemoveException {
        try {
            jobMapper.deleteByPrimaryKey(Integer.parseInt(JobId));
            curatorClient.removePath(StrUtils.getZKJobDetailPath(JobId));
            curatorClient.removePath(StrUtils.getZKJobPath(JobId));
        } catch (Exception e) {
            throw new JobRemoveException(e);
        }
        if (log.isInfoEnabled()) {
            log.info(StrUtils.makeString("Job finished and removed:", JobId));
        }
    }

    public void setJobException(@NonNull String jobId) throws JobSearchException {
        Job job = null;
        try {
            job = jobMapper.selectByPrimaryKey(Integer.parseInt(jobId));
        } catch (Exception e) {
            throw new JobSearchException(e);
        }
        if (job != null) {
            setJobException(job);
        }
    }

    public void setJobException(@NonNull Job job) {
        job.setStatus(Constants.JOB_EXCEPTION_VALUE);
        jobMapper.updateByPrimaryKey(job);
//        returnCap(1);
        if (log.isInfoEnabled()) {
            log.info(StrUtils.makeString("Job exception recorded:", job.getId()));
        }
    }

    public List<Job> getAllJob() throws JobSearchException {
        try {
            return jobMapper.selectAllJob();
        } catch (Exception e) {
            throw new JobSearchException();
        }

    }

    public List<Job> getAllExceptionJob() throws JobSearchException {
        try {
            return jobMapper.selectAllExceptionJob();
        } catch (Exception e) {
            throw new JobSearchException();
        }
    }

//    private boolean tryGetCap(int size) {
//        if (atomicLock.compareAndSet(false, true)) {
//            try {
//                if ((currentSize.get() + size) > poolSize) {
//                    return false;
//                }
//                currentSize.addAndGet(size);
//                return true;
//            } finally {
//                atomicLock.set(false);
//            }
//        }
//        return false;
//    }
//
//    private void returnCap(int size) {
//        currentSize.addAndGet(-size);
//    }
}