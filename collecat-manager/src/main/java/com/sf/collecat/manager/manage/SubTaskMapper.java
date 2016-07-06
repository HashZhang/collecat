package com.sf.collecat.manager.manage;

import com.sf.collecat.common.model.Task;
import com.sf.collecat.manager.schedule.DefaultScheduler;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Hash Zhang
 * @version 1.0.0
 * @date 2016/7/6
 */
public class SubTaskMapper {
    @Autowired
    private SubTaskMapper subTaskMapper;
    @Autowired
    private DefaultScheduler defaultScheduler;

    public void addSubTask(Task task){

    }
}
