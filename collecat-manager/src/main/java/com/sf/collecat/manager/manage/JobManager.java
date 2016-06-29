package com.sf.collecat.manager.manage;

import com.alibaba.fastjson.JSON;
import com.sf.collecat.common.Constants;
import com.sf.collecat.common.mapper.JobMapper;
import com.sf.collecat.common.model.Job;
import com.sf.collecat.common.model.Task;
import com.sf.collecat.common.utils.StrUtils;
import com.sf.collecat.manager.exception.job.JobCompleteException;
import com.sf.collecat.manager.exception.job.JobPulishException;
import com.sf.collecat.manager.exception.job.JobResetException;
import com.sf.collecat.manager.exception.job.JobSearchException;
import com.sf.collecat.manager.sql.SQLParser;
import com.sf.collecat.manager.zk.CuratorClient;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.sql.SQLSyntaxErrorException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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
    private JobMapper jobMapper;
    @Autowired
    private CuratorClient curatorClient;
    @Autowired
    private SQLParser sqlParser;

    //当前job池大小
    private AtomicInteger currentSize = new AtomicInteger(0);
    @Getter
    @Value("${job.pool.size}")
    private int poolSize;
    private ConcurrentHashMap<Integer, Job> jobPool = new ConcurrentHashMap<>();
    private AtomicBoolean atomicLock = new AtomicBoolean(false);

    public int getCurrentSize() {
        return currentSize.get();
    }

    public void checkJobAssigned(String JobId) {

    }

    public List<String> getALLJobsFromZK() {
        return curatorClient.getChildren(Constants.JOB_PATH);
    }

    /**
     * 尝试发布task（一段时间的Job）,如果没获取锁发布失败
     * 则返回false
     *
     * @param task
     * @throws SQLSyntaxErrorException,JobPulishException
     */
    public boolean tryPublishJob(@NonNull Task task) throws SQLSyntaxErrorException, JobPulishException {
        List<Job> jobList = sqlParser.parse(task, new Date());
//        if (tryGetCap(jobList.size())) {
        try {
            for (Job job : jobList) {
                persistJob(job);
            }
        } catch (Exception e) {
//            returnCap(jobList.size());
            throw new JobPulishException(e);
        }
        return true;
//        } else {
//            return false;
//        }
    }

    /**
     * 尝试发布单个job
     * 发布顺序：先写入数据库，之后写入ZK Job Detail，最后写入ZK job
     *
     * @param job
     * @throws JobPulishException
     */
    public boolean tryPublishJob(@NonNull Job job) throws JobPulishException {
//        if (tryGetCap(1)) {
        try {
            persistJob(job);
        } catch (Exception e) {
//                returnCap(1);
            throw new JobPulishException(e);
        }
        return true;
//        } else {
//            return false;
//        }
    }

    /**
     * 重试获取锁，直到获取到并发布成功
     *
     * @param task
     * @throws SQLSyntaxErrorException,JobPulishException
     */
    public void publishJob(@NonNull Task task) throws SQLSyntaxErrorException, JobPulishException {
        List<Job> jobList = sqlParser.parse(task, new Date());
//        while (tryGetCap(jobList.size())) ;
        try {
            for (Job job : jobList) {
                persistJob(job);
            }
        } catch (Exception e) {
//            returnCap(jobList.size());
            throw new JobPulishException(e);
        }
    }

    /**
     * 重试获取锁，直到获取到并发布成功
     *
     * @param job
     * @throws SQLSyntaxErrorException,JobPulishException
     */
    public void publishJob(@NonNull Job job) throws SQLSyntaxErrorException, JobPulishException {
//        while (tryGetCap(1)) ;
        try {
            persistJob(job);
        } catch (Exception e) {
//            returnCap(1);
            throw new JobPulishException(e);
        }
    }

    /**
     * 内部持久化job方法
     *
     * @param job
     */
    private void persistJob(Job job) {
        jobMapper.insert(job);
        int id = job.getId();
        curatorClient.createPath(StrUtils.getZKJobDetailPath(id), JSON.toJSONString(job));
        curatorClient.createPath(StrUtils.getZKJobPath(id), Constants.JOB_INIT);
    }

    /**
     * 完成job
     *
     * @param jobId
     * @throws JobCompleteException
     */
    public void completeJob(int jobId) throws JobCompleteException {
        Job job = jobMapper.selectByPrimaryKey(jobId);
        completeJob(job);
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

    public void resetJob(@NonNull Job job) throws JobResetException {
        job.setStatus(Constants.JOB_INIT_VALUE);
        try {
            jobMapper.updateByPrimaryKey(job);
            curatorClient.setData(StrUtils.getZKJobPath(job.getId()), Constants.JOB_INIT);
        } catch (Exception e) {
            throw new JobResetException(e);
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