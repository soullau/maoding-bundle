package com.maoding.corpbll.module.corpclient.model;

import com.maoding.core.base.BaseEntity;

import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * Created by Wuwq on 2017/2/16.
 */
@Table(name = "sync_task_group")
public class SyncTaskGroup extends BaseEntity {

    /** 状态(0：待执行，1：执行中，2：已结束） */
    private int taskGroupStatus;
    /** 协同端id */
    private String corpEndpoint;
    /** 组织Id */
    private String companyId;
    /** 要同步的时间点 */
    private LocalDateTime syncPoint;
    /** 最后开始执行时间，用来判断超时 */
    private LocalDateTime lastEntry;
    /** 任务量 */
    private int taskAmount;

    public int getTaskGroupStatus() {
        return taskGroupStatus;
    }

    public void setTaskGroupStatus(int taskGroupStatus) {
        this.taskGroupStatus = taskGroupStatus;
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

    public int getTaskAmount() {
        return taskAmount;
    }

    public void setTaskAmount(int taskAmount) {
        this.taskAmount = taskAmount;
    }
}
