package com.sf.collecat.manager.webapp.common.cytoscape;

import java.io.Serializable;

/**
 * @author Hash Zhang
 * @version 1.0.0
 * @date 2016/7/8
 */
public class Node implements Serializable {
    private Data data = new Data();

    public Data getData() {
        return data;
    }
}
