package com.sf.collecat.manager.webapp;

import com.sf.collecat.common.model.Subtask;
import com.sf.collecat.common.model.Task;
import com.sf.collecat.common.utils.StrUtils;
import com.sf.collecat.manager.exception.subtask.SubtaskAddOrUpdateException;
import com.sf.collecat.manager.exception.task.TaskAddException;
import com.sf.collecat.manager.exception.task.TaskDeleteException;
import com.sf.collecat.manager.exception.task.TaskModifyException;
import com.sf.collecat.manager.exception.task.TaskSearchException;
import com.sf.collecat.manager.exception.validate.ValidateJDBCException;
import com.sf.collecat.manager.exception.validate.ValidateKafkaException;
import com.sf.collecat.manager.exception.validate.ValidateSQLException;
import com.sf.collecat.manager.manage.JobManager;
import com.sf.collecat.manager.manage.TaskManager;
import com.sf.collecat.manager.util.CytoscapeHelper;
import com.sf.collecat.manager.util.PropertyLoader;
import com.sf.collecat.manager.webapp.common.CytoscapeElements;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringBufferInputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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
    @Autowired
    private JobManager jobManager;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");

    @RequestMapping("/task/all")
    public ModelAndView task(HttpServletRequest request, ModelMap model) {
        List<Task> tasks = null;
        String message = null;
        try {
            tasks = taskManager.getAllTasks();
        } catch (TaskSearchException e) {
            log.error("", e);
            message = e.getMessage();
        }
        model.addAttribute("alltasks", tasks);
        model.addAttribute("message", message);
        return new ModelAndView("task/task");
    }

    @RequestMapping("/task/modify")
    public ModelAndView modifyTask(HttpServletRequest request, ModelMap model) {
        Task task = null;
        String message = null;
        try {
            task = taskManager.getTask(Integer.parseInt(request.getParameter("taskId")));
        } catch (TaskSearchException e) {
            log.error("", e);
            message = e.getMessage();
        }
        model.addAttribute("task", task);
        model.addAttribute("message", message);
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
                String endD = request.getParameter("endDate").trim();
                if (endD != null && !endD.equals("")) {
                    Date endDate = sdf.parse(StrUtils.makeString(request.getParameter("endDate"), " ", request.getParameter("endTime")));
                    task.setEndTime(endDate);
                } else {
                    task.setEndTime(null);
                }
                task.setMessageFormat(request.getParameter("messageFormat"));
                task.setRoutineTime(Integer.parseInt(request.getParameter("routineTime")));
                task.setSchemaUsed(request.getParameter("schemaUsed"));
                task.setTimeField(request.getParameter("timeField"));
                taskManager.modifyTask(task);
            }
        } catch (TaskSearchException | TaskModifyException | ParseException e) {
            model.addAttribute("message", e.getMessage());
            model.addAttribute("task", task);
            return new ModelAndView("/task/modifyTask");
        } catch (Exception e) {
            log.error("", e);
            model.addAttribute("message", e.getMessage());
            model.addAttribute("task", task);
            return new ModelAndView("/task/modifyTask");
        }
        String content = StrUtils.makeString(2, ";URL=", "/collecat-manager/task.do");
        response.setHeader("REFRESH", content);
        model.addAttribute("item", "task");
        return new ModelAndView("/common/modifiedSuccessfully");
    }
    @RequestMapping(value = "/task/start")
    public ModelAndView startTask(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
        Task task = null;
        try {
            task = taskManager.getTask(Integer.parseInt(request.getParameter("taskId")));
            task.setIsActive(true);
            taskManager.modifyTask(task);
        } catch (TaskSearchException|SubtaskAddOrUpdateException|TaskModifyException e) {
            model.addAttribute("message", StrUtils.makeString("Cannot start Task:",e.getMessage()));
            model.addAttribute("task", task);
            return new ModelAndView("/task/displaySingle");
        }
        String content = StrUtils.makeString(2, ";URL=", "/collecat-manager/task.do");
        response.setHeader("REFRESH", content);
        model.addAttribute("item", "task");
        return new ModelAndView("/common/modifiedSuccessfully");
    }

    @RequestMapping(value = "/task/stop")
    public ModelAndView stopTask(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
        Task task = null;
        try {
            task = taskManager.getTask(Integer.parseInt(request.getParameter("taskId")));
            task.setIsActive(false);
            taskManager.modifyTask(task);
        } catch (TaskSearchException|SubtaskAddOrUpdateException|TaskModifyException e) {
            model.addAttribute("message", StrUtils.makeString("Cannot stop Task:",e.getMessage()));
            model.addAttribute("task", task);
            return new ModelAndView("/task/displaySingle");
        }
        String content = StrUtils.makeString(2, ";URL=", "/collecat-manager/task.do");
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
    public ModelAndView addTask(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
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
            String endD = request.getParameter("endDate").trim();
            if (endD != null && !endD.equals("")) {
                Date endDate = sdf.parse(StrUtils.makeString(request.getParameter("endDate"), " ", request.getParameter("endTime")));
                task.setEndTime(endDate);
            } else {
                task.setEndTime(null);
            }
            task.setMessageFormat(request.getParameter("messageFormat"));
            task.setRoutineTime(Integer.parseInt(request.getParameter("routineTime")));
            task.setSchemaUsed(request.getParameter("schemaUsed"));
            task.setTimeField(request.getParameter("timeField"));
            if (StrUtils.isNull(task.getAllocateRoutine())) {
                throw new Exception("AllocateRoutine can't be null");
            } else if (StrUtils.isNull(task.getInitialSql())) {
                throw new Exception("InitialSql can't be null");
            } else if (StrUtils.isNull(task.getKafkaClusterName())) {
                throw new Exception("KafkaClusterName can't be null");
            } else if (StrUtils.isNull(task.getKafkaTopic())) {
                throw new Exception("KafkaTopic can't be null");
            } else if (StrUtils.isNull(task.getKafkaTopicTokens())) {
                throw new Exception("KafkaTopicTokens can't be null");
            } else if (StrUtils.isNull(task.getKafkaUrl())) {
                throw new Exception("KafkaUrl can't be null");
            } else if (StrUtils.isNull(task.getMessageFormat())) {
                throw new Exception("MessageFormat can't be null and can only be csv or json");
            } else if (StrUtils.isNull(task.getSchemaUsed())) {
                throw new Exception("SchemaUsed can't be null");
            } else if (StrUtils.isNull(task.getTimeField())) {
                throw new Exception("TimeField can't be null");
            }
            taskManager.addTask(task);
        } catch (ValidateKafkaException | SQLException | ValidateSQLException | ParseException | ValidateJDBCException e) {
            model.addAttribute("message", "Something wrong in KafKa configuration");
            model.addAttribute("task", task);
            return new ModelAndView("/task/addTask");
        } catch (Exception e) {
            log.error("", e);
            model.addAttribute("message", e.getMessage());
            model.addAttribute("task", task);
            return new ModelAndView("/task/addTask");
        }
        String content = StrUtils.makeString(2, ";URL=", "/task.do");
        response.setHeader("REFRESH", content);
        model.addAttribute("item", "task");
        return new ModelAndView("/common/modifiedSuccessfully");
    }

    @RequestMapping(value = "/task/batchPublish")
    public ModelAndView batchPublishTask(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
        return new ModelAndView("/task/batchAddTasks");
    }

    @RequestMapping(value = "/task/batchAdd")
    public ModelAndView batchAddTask(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
        String props = "";
        try {
            props = request.getParameter("tasksProperties");
            Properties pt = new Properties();
            pt.load(new StringBufferInputStream(props));
            List<Task> tasks = PropertyLoader.getTasks(pt);
            taskManager.batchAddTask(tasks);
        } catch (IOException | TaskAddException | ValidateKafkaException | SQLException | ValidateSQLException | ParseException | ValidateJDBCException | ClassNotFoundException e) {
            model.addAttribute("message", e.getMessage());
            model.addAttribute("tasksProperties", props);
            return new ModelAndView("/task/batchAddTasks");
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            model.addAttribute("tasksProperties", props);
            return new ModelAndView("/task/batchAddTasks");
        }
        String content = StrUtils.makeString(2, ";URL=", "/collecat-manager/task.do");
        response.setHeader("REFRESH", content);
        model.addAttribute("item", "task");
        return new ModelAndView("/common/modifiedSuccessfully");
    }

    @RequestMapping("/task")
    public ModelAndView taskDashBoard(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
        CytoscapeElements cytoscapeElements = new CytoscapeElements();
        Map<Integer, Task> taskMap = taskManager.getTaskMap();
        for (Task task : taskMap.values()) {
            cytoscapeElements.getNodes().add(CytoscapeHelper.getNode(task));
            Map<Integer, Subtask> subtaskHashMap = task.getSubtaskHashMap();
            for (Subtask subtask : subtaskHashMap.values()) {
                cytoscapeElements.getNodes().add(CytoscapeHelper.getNode(subtask));
                cytoscapeElements.getEdges().add(CytoscapeHelper.getEdge(task, subtask));
                cytoscapeElements.getNodes().add(CytoscapeHelper.getNode(subtask.getMysqlUrl()));
                cytoscapeElements.getEdges().add(CytoscapeHelper.getEdge(subtask, subtask.getMysqlUrl(), jobManager.hasException(subtask)));
            }
        }
        model.addAttribute("elements", cytoscapeElements);
        return new ModelAndView("/task/taskDashBoard");
    }
}
