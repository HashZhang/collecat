package com.sf.collecat.manager.util;

import com.sf.collecat.common.model.Subtask;
import com.sf.collecat.common.model.Task;
import com.sf.collecat.common.utils.StrUtils;
import com.sf.collecat.manager.webapp.common.cytoscape.Data;
import com.sf.collecat.manager.webapp.common.cytoscape.Edge;
import com.sf.collecat.manager.webapp.common.cytoscape.Node;

/**
 * @author Hash Zhang
 * @version 1.0.0
 * @date 2016/7/8
 */
public class CytoscapeHelper {
    public static final String TASK_PREFIX = "TASK-";
    public static final String SUBTASK_PREFIX = "SUBTASK-";
    public static final String JOB_PREFIX = "JOB-";

    private static final String TASK_ACTIVE_COLOR = "#3c78d8";
    private static final String TASK_INACTIVE_COLOR = "#a9aaab";
    private static final String SUBTASK_ACTIVE_COLOR = "#6d9eeb";
    private static final String SUBTASK_INACTIVE_COLOR = "#a9aaab";
    private static final String DB_COLOR = "#a4c2f4";

    private static final String NORMAL_EDGE_COLOR = "#45818e";
    private static final String EXCEPTION_EDGE_COLOR = "#cc0000";

    private static final Integer BORDER_WEIGHT = 100;

    private static final String FAVE_SHAPE = "rectangle";

    public static Node getNode(Task task) {
        Node node = new Node();
        Data data = node.getData();
        data.setFaveColor(task.getIsActive() ? TASK_ACTIVE_COLOR : TASK_INACTIVE_COLOR);
        String id = StrUtils.makeString(TASK_PREFIX, task.getId());
        data.setId(id);
        data.setName(id);
        data.setWeight(BORDER_WEIGHT);
        data.setFaveShape(FAVE_SHAPE);
        return node;
    }

    public static Node getNode(Subtask subtask) {
        Node node = new Node();
        Data data = node.getData();
        data.setFaveColor(subtask.getIsActive() ? SUBTASK_ACTIVE_COLOR : SUBTASK_INACTIVE_COLOR);
        String id = StrUtils.makeString(SUBTASK_PREFIX, subtask.getId());
        data.setId(id);
        data.setName(id);
        data.setWeight(BORDER_WEIGHT);
        data.setFaveShape(FAVE_SHAPE);
        return node;
    }

    public static Node getNode(String dbUrl) {
        Node node = new Node();
        Data data = node.getData();
        data.setFaveColor(DB_COLOR);
        String id = dbUrl.substring(dbUrl.indexOf("//"), dbUrl.indexOf("?"));
        data.setId(id);
        data.setName(id);
        data.setWeight(BORDER_WEIGHT);
        data.setFaveShape(FAVE_SHAPE);
        return node;
    }


    public static Edge getEdge(Task task, Subtask subtask) {
        Edge edge = new Edge();
        Data data = edge.getData();
        data.setSource(StrUtils.makeString(TASK_PREFIX, task.getId()));
        data.setTarget(StrUtils.makeString(SUBTASK_PREFIX, subtask.getId()));
        data.setFaveColor(NORMAL_EDGE_COLOR);
        data.setStrength(BORDER_WEIGHT);
        return edge;
    }

    public static Edge getEdge(Subtask subtask, String dbUrl, boolean noException) {
        Edge edge = new Edge();
        Data data = edge.getData();
        data.setSource(StrUtils.makeString(SUBTASK_PREFIX, subtask.getId()));
        data.setTarget(dbUrl.substring(dbUrl.indexOf("//"), dbUrl.indexOf("?")));
        if (!subtask.getIsActive()) {
            data.setFaveColor(EXCEPTION_EDGE_COLOR);
            data.setLabel("Stopped!");
        } else if (noException) {
            data.setFaveColor(EXCEPTION_EDGE_COLOR);
            data.setLabel("Exception!");
        } else if (subtask.getEndTime() == null) {
            data.setFaveColor(NORMAL_EDGE_COLOR);
            data.setLabel("Working!");
        } else if (subtask.getLastTime().getTime() >= subtask.getEndTime().getTime()) {
            data.setFaveColor(NORMAL_EDGE_COLOR);
            data.setLabel("Working!");
        } else {
            data.setFaveColor(NORMAL_EDGE_COLOR);
            data.setLabel("Working!");
        }
        data.setStrength(BORDER_WEIGHT);
        return edge;
    }

}
