package com.sf.collecat.manager.schedule.process;

import com.sf.collecat.manager.exception.job.JobAssignException;
import com.sf.collecat.manager.exception.job.JobCompleteException;
import com.sf.collecat.manager.exception.job.JobResetException;
import com.sf.collecat.manager.exception.job.JobSearchException;
import com.sf.collecat.manager.exception.node.NodeSearchException;
import com.sf.collecat.manager.manage.JobManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 重置异常工作类
 *
 * @author 862911 Hash Zhang
 * @version 1.0.0
 * @time 2016/6/21
 */
@Slf4j
@Component
public class CheckJobProcess implements Runnable {
    @Autowired
    private JobManager jobManager;

    @Override
    public void run() {
        try {
            jobManager.checkJobs();
        } catch (JobCompleteException e) {
            log.error("",e);
        } catch (JobSearchException e) {
            log.error("",e);
        } catch (JobResetException e) {
            log.error("",e);
        } catch (JobAssignException e) {
            log.error("",e);
        } catch (NodeSearchException e) {
            log.error("",e);
        } catch (Exception e){
            log.error("",e);
        }
    }
}
