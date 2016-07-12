package com.sf.collecat.manager.webapp;

import com.sf.collecat.common.model.Subtask;
import com.sf.collecat.common.model.Task;
import com.sf.collecat.manager.exception.subtask.SubtaskSearchException;
import com.sf.collecat.manager.exception.task.TaskSearchException;
import com.sf.collecat.manager.manage.JobManager;
import com.sf.collecat.manager.manage.NodeManager;
import com.sf.collecat.manager.manage.SubtaskManager;
import com.sf.collecat.manager.manage.TaskManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Hash Zhang
 * @version 1.0.0
 * @date 2016/7/8
 */
@Slf4j
@Controller
public class RouteController {
    @Autowired
    private SubtaskManager subtaskManager;
    @Autowired
    private TaskManager taskManager;
    @Autowired
    private JobManager jobManager;
    @Autowired
    private NodeManager nodeManager;

    private final static String TASK = "TASK-";
    private final static String SUBTASK = "SUBTASK-";
    private final static String JOB = "JOB-";
    private final static String NODE = "NODE-";

    @RequestMapping("/task/route")
    public ModelAndView task(HttpServletRequest request, ModelMap model) {
        String req = request.getParameter("req");
        try {
            if (req.contains(SUBTASK)) {
                Subtask subtask = subtaskManager.getSubtask(Integer.parseInt(req.substring(req.indexOf("-") + 1)));
                model.addAttribute("subtask", subtask);
                model.addAttribute("startTime", taskManager.getTask(subtask.getTaskId()).getStartTime());
                return new ModelAndView("subtask/displaySingle");
            } else if (req.contains(TASK)) {
                List<Subtask> subtasks = subtaskManager.getSubtasks(Integer.parseInt(req.substring(req.indexOf("-") + 1)));
                Task task = taskManager.getTask(Integer.parseInt(req.substring(req.indexOf("-") + 1)));
                task.setCurrentCompPer(subtasks);
                task.setTotalCompPer(subtasks);
                model.addAttribute("task", task);
                return new ModelAndView("task/displaySingle");
            } else {
                return new ModelAndView("task/taskDashBoard");
            }
        } catch (SubtaskSearchException | TaskSearchException e) {
            log.error("", e);
            model.addAttribute("message", e.getMessage());
            return new ModelAndView("task/taskDashBoard");
        }
    }
}
