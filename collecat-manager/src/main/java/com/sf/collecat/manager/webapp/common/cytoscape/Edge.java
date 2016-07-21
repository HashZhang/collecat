package com.sf.collecat.manager.webapp.common.cytoscape;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * @author Hash Zhang
 * @version 1.0.0
 * @date 2016/7/8
 */
public class Edge implements Serializable {
    @Getter
    private Data data = new Data();
}
