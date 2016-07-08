package com.sf.collecat.manager.webapp.common.cytoscape;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author Hash Zhang
 * @version 1.0.0
 * @date 2016/7/8
 */
public class Data implements Serializable {
    @Getter
    @Setter
    private String id;
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private String faveColor;
    @Getter
    @Setter
    private String faveShape;
    @Getter
    @Setter
    private Integer weight;
    @Getter
    @Setter
    private String source;
    @Getter
    @Setter
    private String target;
    @Getter
    @Setter
    private Integer strength;
    @Getter
    @Setter
    private String label;
}
