package com.sf.collecat.manager.webapp;

import com.sf.collecat.common.model.Subtask;
import com.sf.collecat.common.utils.StrUtils;
import com.sf.collecat.manager.exception.subtask.SubtaskAddOrUpdateException;
import com.sf.collecat.manager.exception.subtask.SubtaskSearchException;
import com.sf.collecat.manager.manage.JobManager;
import com.sf.collecat.manager.manage.SubtaskManager;
import com.sf.collecat.manager.manage.TaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Hash Zhang
 * @version 1.0.0
 * @date 2016/7/12
 */
@Controller
public class SubtaskController {
    private final static Logger log = LoggerFactory.getLogger(SubtaskController.class);
    @Autowired
    private TaskManager taskManager;
    @Autowired
    private SubtaskManager subtaskManager;
    @Autowired
    private JobManager jobManager;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");

    @RequestMapping("/subtask/modify")
    public ModelAndView modifyTask(HttpServletRequest request, ModelMap model) {
        Subtask subtask = null;
        String message = null;
        try {
            subtask = subtaskManager.getSubtask(Integer.parseInt(request.getParameter("subtaskId")));
        } catch (SubtaskSearchException e) {
            model.addAttribute("message", e.getMessage());
            return new ModelAndView("redirect:/task");
        }
        model.addAttribute("subtask", subtask);
        return new ModelAndView("subtask/modifySubtask");
    }

    @RequestMapping(value = "/subtask/update", method = RequestMethod.POST)
    public ModelAndView updateTask(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
        Subtask subtask = null;
        try {
            subtask = subtaskManager.getSubtask(Integer.parseInt(request.getParameter("subtaskId")));
            if (subtask != null) {
                subtask.setAllocateRoutine(request.getParameter("allocateRoutine"));
                subtask.setInitialSql(request.getParameter("initialSql"));
                subtask.setIsActive("1".equals(request.getParameter("active")));
                subtask.setKafkaClusterName(request.getParameter("kafkaClusterName"));
                subtask.setKafkaMessageSize(Integer.parseInt(request.getParameter("kafkaMessageSize")));
                subtask.setKafkaTopic(request.getParameter("kafkaTopic"));
                subtask.setKafkaTopicTokens(request.getParameter("kafkaTopicTokens"));
                subtask.setKafkaUrl(request.getParameter("kafkaUrl"));
                subtask.setMysqlUrl(request.getParameter("mysqlUrl"));
                subtask.setMysqlUsername(request.getParameter("mysqlUsername"));
                subtask.setMysqlPassword(request.getParameter("mysqlPassword"));
                Date lastDate = sdf.parse(StrUtils.makeString(request.getParameter("lastDate"), " ", request.getParameter("lastTime")));
                String endD = request.getParameter("endDate").trim();
                subtask.setLastTime(lastDate);
                if (endD != null && !endD.equals("")) {
                    Date endDate = sdf.parse(StrUtils.makeString(request.getParameter("endDate"), " ", request.getParameter("endTime")));
                    subtask.setEndTime(endDate);
                } else {
                    subtask.setEndTime(null);
                }
                subtask.setMessageFormat(request.getParameter("messageFormat"));
                subtask.setRoutineTime(Integer.parseInt(request.getParameter("routineTime")));
                subtask.setSchemaUsed(request.getParameter("schemaUsed"));
                subtask.setTimeField(request.getParameter("timeField"));
                subtaskManager.updateSubtask(subtask);
            }
        } catch (Exception e) {
            log.error("", e);
            model.addAttribute("message", e.getMessage());
            model.addAttribute("subtask", subtask);
            return new ModelAndView("subtask/modifySubtask");
        }
        String content = StrUtils.makeString(2, ";URL=", "/collecat-manager/task.do");
        response.setHeader("REFRESH", content);
        model.addAttribute("item", "subtask");
        return new ModelAndView("common/modifiedSuccessfully");
    }

    @RequestMapping(value = "/subtask/start")
    public ModelAndView startTask(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
        Subtask subtask = null;
        try {
            subtask = subtaskManager.getSubtask(Integer.parseInt(request.getParameter("subtaskId")));
            subtask.setIsActive(true);
            subtaskManager.updateSubtask(subtask);
        } catch (SubtaskAddOrUpdateException | SubtaskSearchException e) {
            model.addAttribute("message", StrUtils.makeString("Cannot start subtask:", e.getMessage()));
            model.addAttribute("subtask", subtask);
            return new ModelAndView("/task/displaySingle");
        }
        String content = StrUtils.makeString(2, ";URL=", "/collecat-manager/task.do");
        response.setHeader("REFRESH", content);
        model.addAttribute("item", "subtask");
        return new ModelAndView("/common/modifiedSuccessfully");
    }

    @RequestMapping(value = "/subtask/stop")
    public ModelAndView stopTask(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
        Subtask subtask = null;
        try {
            subtask = subtaskManager.getSubtask(Integer.parseInt(request.getParameter("subtaskId")));
            subtask.setIsActive(false);
            subtaskManager.updateSubtask(subtask);
        } catch (SubtaskAddOrUpdateException | SubtaskSearchException e) {
            model.addAttribute("message", StrUtils.makeString("Cannot stop subtask:", e.getMessage()));
            model.addAttribute("subtask", subtask);
            return new ModelAndView("/task/displaySingle");
        }
        String content = StrUtils.makeString(2, ";URL=", "/collecat-manager/task.do");
        response.setHeader("REFRESH", content);
        model.addAttribute("item", "subtask");
        return new ModelAndView("/common/modifiedSuccessfully");
    }
}
