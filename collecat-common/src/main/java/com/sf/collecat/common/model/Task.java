package com.sf.collecat.common.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Task implements Serializable {
    private Integer id;

    private Map<Integer, Subtask> subtaskHashMap = new ConcurrentHashMap<Integer, Subtask>();

    private String initialSql;

    private String schemaUsed;

    private String timeField;

    private Date startTime;

    private Date endTime;

    private Integer routineTime;

    private String allocateRoutine;

    private Boolean isActive;

    private String kafkaTopic;

    private String kafkaUrl;

    private String kafkaClusterName;

    private String kafkaTopicTokens;

    private Integer kafkaMessageSize;

    private String messageFormat;

    private static final long serialVersionUID = 1L;

    public int getCurrentCompPer() {
        return currentCompPer;
    }

    public void setCurrentCompPer(List<Subtask> subtasks) {
        int count = 0;
        for (Subtask subtask : subtasks) {
            count += subtask.getCurrentCompletePercent(this.startTime);
        }
        currentCompPer = count / subtasks.size();
    }

    private int currentCompPer;

    public int getTotalCompPer() {
        return totalCompPer;
    }

    public void setTotalCompPer(List<Subtask> subtasks) {
        int count = 0;
        if (this.endTime != null) {
            for (Subtask subtask : subtasks) {
                count += subtask.getTotalCompletePercent(this.startTime);
            }
        }
        totalCompPer = count / subtasks.size();
    }

    private int totalCompPer;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getInitialSql() {
        return initialSql;
    }

    public void setInitialSql(String initialSql) {
        this.initialSql = initialSql == null ? null : initialSql.trim();
    }

    public String getSchemaUsed() {
        return schemaUsed;
    }

    public void setSchemaUsed(String schemaUsed) {
        this.schemaUsed = schemaUsed == null ? null : schemaUsed.trim();
    }

    public String getTimeField() {
        return timeField;
    }

    public void setTimeField(String timeField) {
        this.timeField = timeField == null ? null : timeField.trim();
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getRoutineTime() {
        return routineTime;
    }

    public void setRoutineTime(Integer routineTime) {
        this.routineTime = routineTime;
    }

    public String getAllocateRoutine() {
        return allocateRoutine;
    }

    public void setAllocateRoutine(String allocateRoutine) {
        this.allocateRoutine = allocateRoutine == null ? null : allocateRoutine.trim();
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getKafkaTopic() {
        return kafkaTopic;
    }

    public void setKafkaTopic(String kafkaTopic) {
        this.kafkaTopic = kafkaTopic == null ? null : kafkaTopic.trim();
    }

    public String getKafkaUrl() {
        return kafkaUrl;
    }

    public void setKafkaUrl(String kafkaUrl) {
        this.kafkaUrl = kafkaUrl == null ? null : kafkaUrl.trim();
    }

    public String getKafkaClusterName() {
        return kafkaClusterName;
    }

    public void setKafkaClusterName(String kafkaClusterName) {
        this.kafkaClusterName = kafkaClusterName == null ? null : kafkaClusterName.trim();
    }

    public String getKafkaTopicTokens() {
        return kafkaTopicTokens;
    }

    public void setKafkaTopicTokens(String kafkaTopicTokens) {
        this.kafkaTopicTokens = kafkaTopicTokens == null ? null : kafkaTopicTokens.trim();
    }

    public Integer getKafkaMessageSize() {
        return kafkaMessageSize;
    }

    public void setKafkaMessageSize(Integer kafkaMessageSize) {
        this.kafkaMessageSize = kafkaMessageSize;
    }

    public String getMessageFormat() {
        return messageFormat;
    }

    public void setMessageFormat(String messageFormat) {
        this.messageFormat = messageFormat == null ? null : messageFormat.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", initialSql=").append(initialSql);
        sb.append(", schemaUsed=").append(schemaUsed);
        sb.append(", timeField=").append(timeField);
        sb.append(", startTime=").append(startTime);
        sb.append(", endTime=").append(endTime);
        sb.append(", routineTime=").append(routineTime);
        sb.append(", allocateRoutine=").append(allocateRoutine);
        sb.append(", isActive=").append(isActive);
        sb.append(", kafkaTopic=").append(kafkaTopic);
        sb.append(", kafkaUrl=").append(kafkaUrl);
        sb.append(", kafkaClusterName=").append(kafkaClusterName);
        sb.append(", kafkaTopicTokens=").append(kafkaTopicTokens);
        sb.append(", kafkaMessageSize=").append(kafkaMessageSize);
        sb.append(", messageFormat=").append(messageFormat);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }

    public Map<Integer, Subtask> getSubtaskHashMap() {
        return subtaskHashMap;
    }

    public void setSubtaskHashMap(Map<Integer, Subtask> subtaskHashMap) {
        this.subtaskHashMap = subtaskHashMap;
    }
}