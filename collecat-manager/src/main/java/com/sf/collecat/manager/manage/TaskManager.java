package com.sf.collecat.manager.manage;

import com.sf.collecat.common.mapper.TaskMapper;
import com.sf.collecat.common.model.Task;
import com.sf.collecat.manager.exception.subtask.SubtaskAddOrUpdateException;
import com.sf.collecat.manager.exception.task.TaskAddException;
import com.sf.collecat.manager.exception.task.TaskDeleteException;
import com.sf.collecat.manager.exception.task.TaskModifyException;
import com.sf.collecat.manager.exception.task.TaskSearchException;
import com.sf.collecat.manager.exception.validate.ValidateJDBCException;
import com.sf.collecat.manager.exception.validate.ValidateKafkaException;
import com.sf.collecat.manager.exception.validate.ValidateSQLException;
import com.sf.collecat.manager.schedule.DefaultScheduler;
import com.sf.collecat.manager.util.Validator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;
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
@Slf4j
public class TaskManager {
    @Getter
    private final Map<Integer, Task> taskMap = new ConcurrentHashMap<>();
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private SubtaskManager subtaskManager;
    @Autowired
    private DefaultScheduler defaultScheduler;
    @Autowired
    private Validator validator;

    /**
     * 初始化方法，从数据库载入所有的task
     *
     * @throws TaskSearchException
     * @throws TaskAddException
     */
    public void init() throws TaskSearchException, TaskAddException, SQLException, ValidateJDBCException, ClassNotFoundException, ValidateKafkaException, ValidateSQLException, SubtaskAddOrUpdateException {
        List<Task> taskList = null;
        try {
            taskList = taskMapper.selectAll();
        } catch (Exception e) {
            throw new TaskSearchException(e);
        }
        if (taskList == null) {
            return;
        }
        for (Task task : taskList) {
            addTask(task);
        }
    }

    public Task getTask(int taskId) throws TaskSearchException {
        try {
            return taskMapper.selectByPrimaryKey(taskId);
        } catch (Exception e) {
            throw new TaskSearchException(e);
        }
    }

    public List<Task> getAllTasks() throws TaskSearchException {
        try {
            return taskMapper.selectAll();
        } catch (Exception e) {
            throw new TaskSearchException(e);
        }
    }

    /**
     * 动态添加Task
     *
     * @param task 要添加的task
     * @throws TaskAddException
     */
    public void addTask(Task task) throws TaskAddException, ClassNotFoundException, ValidateJDBCException, SQLException, ValidateSQLException, ValidateKafkaException, SubtaskAddOrUpdateException {
        validator.validateSQL(task);
        validator.validateJDBCConnections(task);
        validator.validateKafKaConnection(task);
        try {
            if (task.getId() == null || task.getId() <= 0) {
                taskMapper.insert(task);
            }
        } catch (Exception e) {
            throw new TaskAddException(e);
        }
        taskMap.put(task.getId(), task);
        subtaskManager.addOrUpdateSubTaskFromTask(task);
    }

    /**
     * 批量动态添加Task，若任一验证失败，都不添加
     *
     * @param tasks 要添加的tasks
     * @throws TaskAddException
     */
    public void batchAddTask(List<Task> tasks) throws TaskAddException, ClassNotFoundException, ValidateJDBCException, SQLException, ValidateSQLException, ValidateKafkaException, SubtaskAddOrUpdateException {
        for (Task task : tasks) {
            validator.validateSQL(task);
            validator.validateJDBCConnections(task);
            validator.validateKafKaConnection(task);
        }
        for (Task task : tasks) {
            try {
                if (task.getId() == null || task.getId() <= 0) {
                    taskMapper.insert(task);
                }
            } catch (Exception e) {
                throw new TaskAddException(e);
            }
            taskMap.put(task.getId(), task);
            subtaskManager.addOrUpdateSubTaskFromTask(task);
        }
    }

    /**
     * 动态修改Task
     * 分为四步：在配置数据库中修改task，停止当前task调度
     * 更新到缓存中（因为调度是根据本地缓存中的task属性决定），重新调度task
     *
     * @param task 要修改的task
     * @throws TaskModifyException
     */
    public void modifyTask(Task task) throws TaskModifyException, SubtaskAddOrUpdateException {
        try {
            taskMapper.updateByPrimaryKey(task);
        } catch (Exception e) {
            throw new TaskModifyException(e);
        }
        task.setSubtaskHashMap(taskMap.get(task.getId()).getSubtaskHashMap());
        subtaskManager.addOrUpdateSubTaskFromTask(task);
        taskMap.put(task.getId(), task);
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
            task.setSubtaskHashMap(taskMap.get(task.getId()).getSubtaskHashMap());
            subtaskManager.removeSubTasksFromTask(task);
            taskMap.remove(task.getId());
            taskMapper.deleteByPrimaryKey(task.getId());
        } catch (Exception e) {
            throw new TaskDeleteException(e);
        }

    }

    public void clearTasks() throws TaskSearchException, TaskDeleteException {
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
            removeTask(task);
        }
    }

}

