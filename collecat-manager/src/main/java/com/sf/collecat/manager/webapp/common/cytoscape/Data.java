package com.sf.collecat.manager.webapp.common.cytoscape;

import java.io.Serializable;

/**
 * @author Hash Zhang
 * @version 1.0.0
 * @date 2016/7/8
 */
public class Data implements Serializable {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFaveColor() {
        return faveColor;
    }

    public void setFaveColor(String faveColor) {
        this.faveColor = faveColor;
    }

    public String getFaveShape() {
        return faveShape;
    }

    public void setFaveShape(String faveShape) {
        this.faveShape = faveShape;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public Integer getStrength() {
        return strength;
    }

    public void setStrength(Integer strength) {
        this.strength = strength;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    private String id;
    private String name;
    private String faveColor;
    private String faveShape;
    private Integer weight;
    private String source;
    private String target;
    private Integer strength;
    private String label;
}
