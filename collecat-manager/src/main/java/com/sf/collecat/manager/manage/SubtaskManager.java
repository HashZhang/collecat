package com.sf.collecat.manager.manage;

import com.sf.collecat.common.mapper.SubtaskMapper;
import com.sf.collecat.common.model.Subtask;
import com.sf.collecat.common.model.Task;
import com.sf.collecat.manager.exception.subtask.SubtaskAddOrUpdateException;
import com.sf.collecat.manager.exception.subtask.SubtaskDeleteException;
import com.sf.collecat.manager.exception.subtask.SubtaskModifyException;
import com.sf.collecat.manager.exception.subtask.SubtaskSearchException;
import com.sf.collecat.manager.schedule.DefaultScheduler;
import com.sf.collecat.manager.sql.SQLParser;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLSyntaxErrorException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Hash Zhang
 * @version 1.0.0
 * @date 2016/7/6
 */
public class SubtaskManager {
    @Autowired
    private SubtaskMapper subtaskMapper;
    @Autowired
    private TaskManager taskManager;
    @Autowired
    private DefaultScheduler defaultScheduler;
    @Autowired
    private SQLParser sqlParser;

    public Subtask getSubtask(int subtaskId) throws SubtaskSearchException {
        try {
            return subtaskMapper.selectByPrimaryKey(subtaskId);
        } catch (Exception e) {
            throw new SubtaskSearchException(e);
        }
    }

    public List<Subtask> getSubtasks(int taskId) throws SubtaskSearchException {
        try {
            return subtaskMapper.selectByTaskId(taskId);
        } catch (Exception e) {
            throw new SubtaskSearchException(e);
        }
    }

    public void addOrUpdateSubTaskFromTask(Task task) throws SubtaskAddOrUpdateException {
        try {
            List<Subtask> subtasks = sqlParser.parse(task);
            for (Subtask subtask : subtasks) {
                Subtask subtask1 = subtaskMapper.selectByTaskIDAndDbUrl(subtask);
                if (subtask1 == null) {
                    subtaskMapper.insert(subtask);
                } else {
                    //保证subtask上次生成job时间还有当前调度时间不变
                    subtask.setLastTime(subtask1.getLastTime());
                    subtask.setCurrTime(subtask1.getCurrTime());
                    subtask.setId(subtask1.getId());
                    subtaskMapper.updateByPrimaryKeySelective(subtask);
                    if (task.getSubtaskHashMap().containsKey(subtask.getId())) {
                        defaultScheduler.cancelTask(task.getSubtaskHashMap().get(subtask.getId()));
                    }
                    subtask = subtaskMapper.selectByPrimaryKey(subtask.getId());
                }
                task.getSubtaskHashMap().put(subtask.getId(), subtask);
                defaultScheduler.scheduleTask(subtask);
            }
        } catch (SQLSyntaxErrorException e) {
            throw new SubtaskAddOrUpdateException(e);
        } catch (Exception e) {
            throw new SubtaskAddOrUpdateException(e);
        }
    }

    public void updateSubtask(Subtask subtask) throws SubtaskAddOrUpdateException {
        try {
            Map<Integer, Task> taskMap = taskManager.getTaskMap();
            Task task = null;
            if (taskMap.containsKey(subtask.getTaskId())) {
                task = taskMap.get(subtask.getTaskId());
            }
            if (task != null && task.getSubtaskHashMap().containsKey(subtask.getId())) {
                defaultScheduler.cancelTask(task.getSubtaskHashMap().get(subtask.getId()));
            }
            task.getSubtaskHashMap().put(subtask.getId(), subtask);
            subtaskMapper.updateByPrimaryKeySelective(subtask);
            defaultScheduler.scheduleTask(subtask);
        } catch (Exception e) {
            throw new SubtaskAddOrUpdateException(e);
        }
    }

    public void removeSubTasksFromTask(Task task) throws SubtaskDeleteException {
        try { List<Subtask> subtaskList = subtaskMapper.selectByTaskId(task.getId());
        for (Subtask subtask : subtaskList) {
            defaultScheduler.cancelTask(task.getSubtaskHashMap().get(subtask.getId()));
            subtaskMapper.deleteByPrimaryKey(subtask.getId());
        }} catch (Exception e) {
            throw new SubtaskDeleteException(e);
        }
    }

    public void scheduleNow(Subtask subtask) throws SubtaskModifyException {
        subtask.setCurrTime(new Date());
        try {
            subtaskMapper.updateByPrimaryKey(subtask);
        } catch (Exception e) {
            throw new SubtaskModifyException(e);
        }
    }
}
