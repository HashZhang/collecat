package com.sf.collecat.manager.task;

import com.sf.collecat.common.mapper.TaskMapper;
import com.sf.collecat.common.model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by 862911 on 2016/6/16.
 */
@Component
public class TaskLoader {
    @Autowired
    private TaskMapper taskMapper;

    public List<Task> getActiveTasks(){
        return taskMapper.selectAll();
    }
}
