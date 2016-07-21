package com.sf.collecat.manager.webapp.common;

import com.alibaba.fastjson.JSON;
import com.sf.collecat.manager.webapp.common.cytoscape.Data;
import com.sf.collecat.manager.webapp.common.cytoscape.Edge;
import com.sf.collecat.manager.webapp.common.cytoscape.Node;
import lombok.Getter;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Hash Zhang
 * @version 1.0.0
 * @date 2016/7/8
 */
public class CytoscapeElements implements Serializable{
    @Getter
    private final List<Node> nodes = new LinkedList<>();
    @Getter
    private final List<Edge> edges = new LinkedList<>();

    public static void main(String[] args) {
        CytoscapeElements cytoscapeElements = new CytoscapeElements();

        Node node = new Node();
        Data data = node.getData();
        data.setId("Task-1");
        data.setName("Task-1");
        data.setWeight(80);
        data.setFaveColor("#45818e");
        data.setFaveShape("rectangle");

        Edge edge = new Edge();
        data = edge.getData();
        data.setSource("Task-1");
        data.setTarget("Subtask-1");
        data.setFaveColor("#6FB1FC");
        data.setStrength(60);

        cytoscapeElements.getEdges().add(edge);
        cytoscapeElements.getNodes().add(node);

        node = new Node();
        data = node.getData();
        data.setId("Subtask-1");
        data.setName("Subtask-1");
        data.setWeight(80);
        data.setFaveColor("#6FB1FC");
        data.setFaveShape("rectangle");

        cytoscapeElements.getNodes().add(node);

        System.out.println(JSON.toJSONString(cytoscapeElements));
    }
}
