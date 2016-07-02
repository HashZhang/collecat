package com.sf.collecat.manager.webapp;

import com.sf.collecat.common.model.Task;
import com.sf.collecat.manager.exception.task.TaskSearchException;
import com.sf.collecat.manager.manage.TaskManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Hash Zhang
 * @version 1.0.0
 * @date 2016/7/1
 */
@Slf4j
@Controller
public class TaskController {
    @Autowired
    private TaskManager taskManager;

    @RequestMapping("/task")
    public ModelAndView task(HttpServletRequest request, ModelMap model) {
        List<Task> tasks = null;
        try {
            tasks = taskManager.getAllTasks();
        } catch (TaskSearchException e) {
            log.error("", e);
        }
        model.addAttribute("alltasks", tasks);
        return new ModelAndView("task/task");
    }
    @RequestMapping("/task/modify")
    public ModelAndView modifyTask(HttpServletRequest request, ModelMap model) {
        Task task = null;
        try {
            task = taskManager.getTask(Integer.parseInt(request.getParameter("taskId")));
        } catch (TaskSearchException e) {
            log.error("", e);
        }
        model.addAttribute("task",task);
        return new ModelAndView("task/modifyTask");
    }

    @RequestMapping(value = "/task/update",method = RequestMethod.POST)
    public ModelAndView updateTask(HttpServletRequest request, ModelMap model) {
        System.out.println(request.getParameter("schemaUsed"));
        List<Task> tasks = null;
        try {
            tasks = taskManager.getAllTasks();
        } catch (TaskSearchException e) {
            log.error("", e);
        }
        model.addAttribute("alltasks", tasks);
        return new ModelAndView("task/task");
    }
}
