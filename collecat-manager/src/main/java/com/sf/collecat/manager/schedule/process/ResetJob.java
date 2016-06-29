package com.sf.collecat.manager.schedule.process;

import com.sf.collecat.manager.schedule.DefaultScheduler;
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
public class ResetJob implements Runnable {
    @Autowired
    private DefaultScheduler defaultScheduler;

    @Override
    public void run() {
        try {
            defaultScheduler.resetAllExceptionJob();
        }catch(Exception e){
            log.error("Caught exception while resetting exception jobs!",e);
        }
    }
}
