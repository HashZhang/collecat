package com.sf.collecat.manager.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by 862911 on 2016/6/23.
 */
@Component
public class ResetJob implements Runnable {
    @Autowired
    private ScheduleCat scheduleCat;

    @Override
    public void run() {
        scheduleCat.resetAllExceptionJob();
    }
}
