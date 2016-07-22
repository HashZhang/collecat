package com.sf.collecat.manager.webapp;

import com.sf.collecat.common.model.Node;
import com.sf.collecat.manager.exception.node.NodeSearchException;
import com.sf.collecat.manager.manage.NodeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * @date 2016/7/5
 */
@Controller
public class NodeController {
    private final static Logger log = LoggerFactory.getLogger(NodeController.class);
    @Autowired
    private NodeManager nodeManager;

    @RequestMapping("/node")
    public ModelAndView job(HttpServletRequest request, ModelMap model) {
        List<Node> nodes = null;
        String message = null;
        try {
            nodes = nodeManager.getAllNodesFromDB();
        } catch (NodeSearchException e) {
            log.error("", e);
            message = e.getMessage();
        }
        model.addAttribute("allNodes", nodes);
        model.addAttribute("message", message);
        return new ModelAndView("/node/node");
    }
}
