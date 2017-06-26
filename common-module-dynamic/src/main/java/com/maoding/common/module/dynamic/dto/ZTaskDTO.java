package com.maoding.common.module.dynamic.dto;

import java.util.Date;

/**
 * Created by Chengliang.zhang on 2017/6/26.
 */
public class ZTaskDTO {
    /**
     * 任务ID
     */
    String id;
    /**
     * 项目ID
     */
    String projectId;
    /**
     * 任务短名称
     */
    String taskName;
    /**
     * 任务长名称，包括负责人、进度计划等信息
     */
    String taskFullName;
    /**
     * 任务的类型
     */
    Integer taskType;
    /**
     * 任务的类型名称，如设计内容、生产任务等
     */
    String typeName;
    /**
     * 进度计划起止时间组合字符串
     */
    String taskPeriod;
    /**
     * 任务负责人
     */
    String leaderName;
    /**
     * 任务参与人员组合字符串
     */
    String members;
    /**
     * 设计组织
     */
    String toCompanyName;

    /**
     * 完成时间
     */
    Date completeDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskFullName() {
        return taskFullName;
    }

    public void setTaskFullName(String taskFullName) {
        this.taskFullName = taskFullName;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTaskPeriod() {
        return taskPeriod;
    }

    public void setTaskPeriod(String taskPeriod) {
        this.taskPeriod = taskPeriod;
    }

    public String getLeaderName() {
        return leaderName;
    }

    public void setLeaderName(String leaderName) {
        this.leaderName = leaderName;
    }

    public String getMembers() {
        return members;
    }

    public void setMembers(String members) {
        this.members = members;
    }

    public String getToCompanyName() {
        return toCompanyName;
    }

    public void setToCompanyName(String toCompanyName) {
        this.toCompanyName = toCompanyName;
    }

    public Date getCompleteDate() {
        return completeDate;
    }

    public void setCompleteDate(Date completeDate) {
        this.completeDate = completeDate;
    }
}
