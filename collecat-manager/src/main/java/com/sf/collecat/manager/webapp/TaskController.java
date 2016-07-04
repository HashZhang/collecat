package com.sf.collecat.manager.webapp;

import com.sf.collecat.common.model.Task;
import com.sf.collecat.common.utils.StrUtils;
import com.sf.collecat.manager.exception.task.TaskAddException;
import com.sf.collecat.manager.exception.task.TaskDeleteException;
import com.sf.collecat.manager.exception.task.TaskModifyException;
import com.sf.collecat.manager.exception.task.TaskSearchException;
import com.sf.collecat.manager.exception.validate.ValidateJDBCException;
import com.sf.collecat.manager.exception.validate.ValidateKafkaException;
import com.sf.collecat.manager.exception.validate.ValidateSQLException;
import com.sf.collecat.manager.manage.TaskManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    private static final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");

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
        model.addAttribute("task", task);
        return new ModelAndView("task/modifyTask");
    }

    @RequestMapping(value = "/task/update", method = RequestMethod.POST)
    public ModelAndView updateTask(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
        Task task = null;
        try {
            task = taskManager.getTask(Integer.parseInt(request.getParameter("taskId")));
            if (task != null) {
                task.setAllocateRoutine(request.getParameter("allocateRoutine"));
                task.setInitialSql(request.getParameter("initialSql"));
                task.setIsActive("1".equals(request.getParameter("active")));
                task.setKafkaClusterName(request.getParameter("kafkaClusterName"));
                task.setKafkaMessageSize(Integer.parseInt(request.getParameter("kafkaMessageSize")));
                task.setKafkaTopic(request.getParameter("kafkaTopic"));
                task.setKafkaTopicTokens(request.getParameter("kafkaTopicTokens"));
                task.setKafkaUrl(request.getParameter("kafkaUrl"));
                Date date = sdf.parse(StrUtils.makeString(request.getParameter("lastdate"), " ", request.getParameter("lasttime")));
                task.setLastTime(date);
                task.setMessageFormat(request.getParameter("messageFormat"));
                task.setRoutineTime(Integer.parseInt(request.getParameter("routineTime")));
                task.setSchemaUsed(request.getParameter("schemaUsed"));
                task.setTimeField(request.getParameter("timeField"));
                taskManager.modifyTask(task);
            }
        } catch (TaskSearchException e) {
            log.error("", e);
        } catch (TaskModifyException e) {
            log.error("", e);
        } catch (ParseException e) {
            log.error("", e);
        }
        String content = StrUtils.makeString(2, ";URL=", "/task.do");
        response.setHeader("REFRESH", content);
        model.addAttribute("item", "task");
        return new ModelAndView("/common/modifiedSuccessfully");
    }

    @RequestMapping(value = "/task/remove")
    public ModelAndView removeTask(HttpServletRequest request, ModelMap model) {
        Task task = null;
        try {
            task = taskManager.getTask(Integer.parseInt(request.getParameter("taskId")));
            taskManager.removeTask(task);
        } catch (TaskSearchException e) {
            log.error("", e);
        } catch (TaskDeleteException e) {
            log.error("", e);
        }
        return new ModelAndView("redirect:/task.do");
    }

    @RequestMapping(value = "/task/publish")
    public ModelAndView publishTask(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
        return new ModelAndView("/task/addTask");
    }

    @RequestMapping(value = "/task/add")
    public ModelAndView addTask(HttpServletRequest request, ModelMap model) {
        Task task = new Task();
        try {
            task.setAllocateRoutine(request.getParameter("allocateRoutine"));
            task.setInitialSql(request.getParameter("initialSql"));
            task.setIsActive("1".equals(request.getParameter("active")));
            task.setKafkaClusterName(request.getParameter("kafkaClusterName"));
            task.setKafkaMessageSize(Integer.parseInt(request.getParameter("kafkaMessageSize")));
            task.setKafkaTopic(request.getParameter("kafkaTopic"));
            task.setKafkaTopicTokens(request.getParameter("kafkaTopicTokens"));
            task.setKafkaUrl(request.getParameter("kafkaUrl"));
            Date date = sdf.parse(StrUtils.makeString(request.getParameter("lastdate"), " ", request.getParameter("lasttime")));
            task.setLastTime(date);
            task.setMessageFormat(request.getParameter("messageFormat"));
            task.setRoutineTime(Integer.parseInt(request.getParameter("routineTime")));
            task.setSchemaUsed(request.getParameter("schemaUsed"));
            task.setTimeField(request.getParameter("timeField"));
            if(StrUtils.isNull(task.getAllocateRoutine())){
                throw new Exception("AllocateRoutine can't be null");
            } else if(StrUtils.isNull(task.getInitialSql())){
                throw new Exception("InitialSql can't be null");
            } else if(StrUtils.isNull(task.getKafkaClusterName())){
                throw new Exception("KafkaClusterName can't be null");
            } else if(StrUtils.isNull(task.getKafkaTopic())){
                throw new Exception("KafkaTopic can't be null");
            } else if(StrUtils.isNull(task.getKafkaTopicTokens())){
                throw new Exception("KafkaTopicTokens can't be null");
            } else if(StrUtils.isNull(task.getKafkaUrl())){
                throw new Exception("KafkaUrl can't be null");
            } else if(StrUtils.isNull(task.getMessageFormat())){
                throw new Exception("MessageFormat can't be null and can only be csv or json");
            } else if(StrUtils.isNull(task.getSchemaUsed())){
                throw new Exception("SchemaUsed can't be null");
            } else if(StrUtils.isNull(task.getTimeField())){
                throw new Exception("TimeField can't be");
            }
            taskManager.addTask(task);
        } catch (ValidateKafkaException e) {
            model.addAttribute("message", "Something wrong in KafKa configuration");
            model.addAttribute("task", task);
            return new ModelAndView("/task/addTask");
        } catch (SQLException e) {
            model.addAttribute("message", "Something wrong in Schema Used");
            model.addAttribute("task", task);
            return new ModelAndView("/task/addTask");
        } catch (ValidateSQLException e) {
            model.addAttribute("message", "Something wrong in provided SQL statement");
            model.addAttribute("task", task);
            return new ModelAndView("/task/addTask");
        } catch (ParseException e) {
            model.addAttribute("message", "Something wrong in LastTime");
            model.addAttribute("task", task);
            return new ModelAndView("/task/addTask");
        } catch (ValidateJDBCException e) {
            model.addAttribute("message", "Something wrong in MySQL configuration");
            model.addAttribute("task", task);
            return new ModelAndView("/task/addTask");
        } catch(Exception e){
            model.addAttribute("message", e.getMessage());
            model.addAttribute("task", task);
            return new ModelAndView("/task/addTask");
        }
        return new ModelAndView("/common/modifiedSuccessfully");
    }
}
