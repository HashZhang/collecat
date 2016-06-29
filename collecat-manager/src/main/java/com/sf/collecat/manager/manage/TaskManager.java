package com.sf.collecat.manager.manage;

import com.sf.collecat.common.mapper.TaskMapper;
import com.sf.collecat.common.model.Task;
import com.sf.collecat.manager.exception.task.TaskAddException;
import com.sf.collecat.manager.exception.task.TaskDeleteException;
import com.sf.collecat.manager.exception.task.TaskModifyException;
import com.sf.collecat.manager.exception.task.TaskSearchException;
import com.sf.collecat.manager.schedule.DefaultScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Task管理类，管理所有关于task的操作
 * 涉及到数据库的操作都是数据库优先，保证数据库修改成功再修改缓存
 *
 * @author 862911 Hash Zhang
 * @version 1.0.0
 * @time 2016/6/27
 */
@Service
public class TaskManager {
    private final static Map<Integer, Task> taskMap = new ConcurrentHashMap<>();
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private DefaultScheduler defaultScheduler;

    /**
     * 初始化方法，从数据库载入所有的task
     * @throws TaskSearchException
     * @throws TaskAddException
     */
    public void init() throws TaskSearchException, TaskAddException {
        List<Task> taskList = null;
        try {
            taskList = taskMapper.selectAll();
        } catch (Exception e) {
            throw new TaskSearchException(e);
        }
        if (taskList != null) {
            return;
        }
        for (Task task : taskList) {
            addTask(task);
        }
    }

    /**
     * 动态添加Task
     * 分为三步：在配置数据库中添加task，本地缓存记录task，调度task
     *
     * @param task 要添加的task
     * @throws TaskAddException
     */
    public void addTask(Task task) throws TaskAddException {
        try {
            taskMapper.insert(task);
        } catch (Exception e) {
            throw new TaskAddException(e);
        }
        taskMap.put(task.getId(), task);
        defaultScheduler.scheduleTask(task);
    }

    /**
     * 动态修改Task
     * 分为四步：在配置数据库中修改task，停止当前task调度
     * 更新到缓存中（因为调度是根据本地缓存中的task属性决定），重新调度task
     *
     * @param task 要修改的task
     * @throws TaskModifyException
     */
    public void modifyTask(Task task) throws TaskModifyException {
        try {
            taskMapper.updateByPrimaryKey(task);
        } catch (Exception e) {
            throw new TaskModifyException(e);
        }
        defaultScheduler.cancelTask(task);
        taskMap.put(task.getId(), task);
        defaultScheduler.scheduleTask(task);
    }

    /**
     * 动态删除task
     * 分为三步：在配置库中删除task，停止当前task调度，在缓存中删除task
     *
     * @param task 要移除的task
     * @throws TaskDeleteException
     */
    public void removeTask(Task task) throws TaskDeleteException {
        try {
            taskMapper.deleteByPrimaryKey(task.getId());
        } catch (Exception e) {
            throw new TaskDeleteException(e);
        }
        defaultScheduler.cancelTask(task);
        taskMap.remove(task.getId());
    }

}

