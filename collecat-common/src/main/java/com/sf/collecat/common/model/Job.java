package com.sf.collecat.common.model;

import java.io.Serializable;
import java.util.Date;

public class Job implements Serializable {
    private Integer id;

    private String timeField;

    private Date timeFieldStart;

    private Date timeFieldEnd;

    private String jobSql;

    private String mysqlUrl;

    private String mysqlUsername;

    private String mysqlPassword;

    private Date createdTime;

    private Date modifiedTime;

    private String kafkaTopic;

    private String kafkaUrl;

    private String kafkaClusterName;

    private String kafkaTopicTokens;

    private Integer kafkaMessageSize;

    private String messageFormat;

    private Integer status;

    private Integer nodeAssignedTo;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTimeField() {
        return timeField;
    }

    public void setTimeField(String timeField) {
        this.timeField = timeField == null ? null : timeField.trim();
    }

    public Date getTimeFieldStart() {
        return timeFieldStart;
    }

    public void setTimeFieldStart(Date timeFieldStart) {
        this.timeFieldStart = timeFieldStart;
    }

    public Date getTimeFieldEnd() {
        return timeFieldEnd;
    }

    public void setTimeFieldEnd(Date timeFieldEnd) {
        this.timeFieldEnd = timeFieldEnd;
    }

    public String getJobSql() {
        return jobSql;
    }

    public void setJobSql(String jobSql) {
        this.jobSql = jobSql == null ? null : jobSql.trim();
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

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Date modifiedTime) {
        this.modifiedTime = modifiedTime;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getNodeAssignedTo() {
        return nodeAssignedTo;
    }

    public void setNodeAssignedTo(Integer nodeAssignedTo) {
        this.nodeAssignedTo = nodeAssignedTo;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", timeField=").append(timeField);
        sb.append(", timeFieldStart=").append(timeFieldStart);
        sb.append(", timeFieldEnd=").append(timeFieldEnd);
        sb.append(", jobSql=").append(jobSql);
        sb.append(", mysqlUrl=").append(mysqlUrl);
        sb.append(", mysqlUsername=").append(mysqlUsername);
        sb.append(", mysqlPassword=").append(mysqlPassword);
        sb.append(", createdTime=").append(createdTime);
        sb.append(", modifiedTime=").append(modifiedTime);
        sb.append(", kafkaTopic=").append(kafkaTopic);
        sb.append(", kafkaUrl=").append(kafkaUrl);
        sb.append(", kafkaClusterName=").append(kafkaClusterName);
        sb.append(", kafkaTopicTokens=").append(kafkaTopicTokens);
        sb.append(", kafkaMessageSize=").append(kafkaMessageSize);
        sb.append(", messageFormat=").append(messageFormat);
        sb.append(", status=").append(status);
        sb.append(", nodeAssignedTo=").append(nodeAssignedTo);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}