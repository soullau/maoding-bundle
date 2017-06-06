package com.maoding.corpbll.module.corpclient.model;

import com.maoding.core.base.BaseEntity;

import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * Created by Wuwq on 2017/2/14.
 */
@Table(name = "sync_task")
public class SyncTaskDO extends BaseEntity {

    /** 任务组Id */
    private String taskGroupId;
    /** 状态(0：待执行，1：执行中，2：已结束） */
    private int taskStatus;
    /** 协同端id */
    private String corpEndpoint;
    /** 组织Id */
    private String companyId;
    /** 项目Id */
    private String projectId;
    /** 要同步的时间点 */
    private LocalDateTime syncPoint;
    /** 最后开始执行时间，用来判断超时 */
    private LocalDateTime lastEntry;
    /** 同步优先级（值越小越高） */
    private int syncPriority;
    /** 同步指令（CU、PU、PT、PF） */
    private String syncCmd;
    /** 同步参数 **/
    private String syncParam;
    /** 同步状态（0：待同步，1：待重试，2：同步成功，3：同步失败） */
    private int syncStatus;
    /** 当前重试次数 */
    private int retryTimes;

    public String getTaskGroupId() {
        return taskGroupId;
    }

    public void setTaskGroupId(String taskGroupId) {
        this.taskGroupId = taskGroupId;
    }

    public int getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(int taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getCorpEndpoint() {
        return corpEndpoint;
    }

    public void setCorpEndpoint(String corpEndpoint) {
        this.corpEndpoint = corpEndpoint;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public LocalDateTime getSyncPoint() {
        return syncPoint;
    }

    public void setSyncPoint(LocalDateTime syncPoint) {
        this.syncPoint = syncPoint;
    }

    public LocalDateTime getLastEntry() {
        return lastEntry;
    }

    public void setLastEntry(LocalDateTime lastEntry) {
        this.lastEntry = lastEntry;
    }

    public int getSyncPriority() {
        return syncPriority;
    }

    public void setSyncPriority(int syncPriority) {
        this.syncPriority = syncPriority;
    }

    public String getSyncCmd() {
        return syncCmd;
    }

    public void setSyncCmd(String syncCmd) {
        this.syncCmd = syncCmd;
    }

    public String getSyncParam() {
        return syncParam;
    }

    public void setSyncParam(String syncParam) {
        this.syncParam = syncParam;
    }

    public int getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(int syncStatus) {
        this.syncStatus = syncStatus;
    }

    public int getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }
}
