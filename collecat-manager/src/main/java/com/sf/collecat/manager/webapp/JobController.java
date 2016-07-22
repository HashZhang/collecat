package com.sf.collecat.manager.webapp;

import com.sf.collecat.common.model.Job;
import com.sf.collecat.common.utils.StrUtils;
import com.sf.collecat.manager.exception.job.JobResetException;
import com.sf.collecat.manager.exception.job.JobSearchException;
import com.sf.collecat.manager.manage.JobManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author Hash Zhang
 * @version 1.0.0
 * @date 2016/7/5
 */
@Controller
public class JobController {
    private final static Logger log = LoggerFactory.getLogger(JobController.class);
    @Autowired
    private JobManager jobManager;

    @RequestMapping("/job")
    public ModelAndView job(HttpServletRequest request, ModelMap model) {
        List<Job> jobs = null;
        String message = null;
        try {
            jobs = jobManager.getAllJob();
        } catch (JobSearchException e) {
            log.error("", e);
            message = e.getMessage();
        }
        System.out.println(jobs);
        model.addAttribute("allJobs", jobs);
        model.addAttribute("message", message);
        return new ModelAndView("/job/job");
    }

    @RequestMapping("/job/reset")
    public ModelAndView resetJob(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
        try {
            jobManager.resetJob(request.getParameter("jobId"));
        } catch (JobSearchException | JobResetException e) {
            model.addAttribute("message", e.getMessage());
            return new ModelAndView("redirect:/job");
        }
        String content = StrUtils.makeString(2, ";URL=", "/collecat-manager/job.do");
        response.setHeader("REFRESH", content);
        model.addAttribute("item", "job");
        return new ModelAndView("/common/modifiedSuccessfully");
    }
}
