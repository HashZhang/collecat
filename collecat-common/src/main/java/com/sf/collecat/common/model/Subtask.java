package com.sf.collecat.common.model;

import it.sauronsoftware.cron4j.Scheduler;

import java.io.Serializable;
import java.util.Date;

public class Subtask implements Serializable {
    private Integer id;
    private Scheduler scheduler;

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    private Integer taskId;

    private String initialSql;

    private String schemaUsed;

    private String timeField;

    private Date lastTime;

    private Integer routineTime;

    private String allocateRoutine;

    private Boolean isActive;

    private String kafkaTopic;

    private String kafkaUrl;

    private String kafkaClusterName;

    private String kafkaTopicTokens;

    private Integer kafkaMessageSize;

    private String mysqlUrl;

    private String mysqlUsername;

    private String mysqlPassword;

    private String messageFormat;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
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

    public Date getLastTime() {
        return lastTime;
    }

    public void setLastTime(Date lastTime) {
        this.lastTime = lastTime;
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

    public String getMysqlUrl() {
        return mysqlUrl;
    }

    public void setMysqlUrl(String mysqlUrl) {
        this.mysqlUrl = mysqlUrl == null ? null : mysqlUrl.trim();
    }

    public String getMysqlUsername() {
        return mysqlUsername;
    }

    public void setMysqlUsername(String mysqlUsername) {
        this.mysqlUsername = mysqlUsername == null ? null : mysqlUsername.trim();
    }

    public String getMysqlPassword() {
        return mysqlPassword;
    }

    public void setMysqlPassword(String mysqlPassword) {
        this.mysqlPassword = mysqlPassword == null ? null : mysqlPassword.trim();
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
        sb.append(", taskId=").append(taskId);
        sb.append(", initialSql=").append(initialSql);
        sb.append(", schemaUsed=").append(schemaUsed);
        sb.append(", timeField=").append(timeField);
        sb.append(", lastTime=").append(lastTime);
        sb.append(", routineTime=").append(routineTime);
        sb.append(", allocateRoutine=").append(allocateRoutine);
        sb.append(", isActive=").append(isActive);
        sb.append(", kafkaTopic=").append(kafkaTopic);
        sb.append(", kafkaUrl=").append(kafkaUrl);
        sb.append(", kafkaClusterName=").append(kafkaClusterName);
        sb.append(", kafkaTopicTokens=").append(kafkaTopicTokens);
        sb.append(", kafkaMessageSize=").append(kafkaMessageSize);
        sb.append(", mysqlUrl=").append(mysqlUrl);
        sb.append(", mysqlUsername=").append(mysqlUsername);
        sb.append(", mysqlPassword=").append(mysqlPassword);
        sb.append(", messageFormat=").append(messageFormat);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}