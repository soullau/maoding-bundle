package com.maoding.corpbll.module.corpclient.dto;

import java.time.LocalDateTime;

/**
 * Created by Wuwq on 2017/2/9.
 * 同步记录
 */
public class SyncHistoryDto_Select {

    private String id;
    /*协同端id*/
    private String corpEndpoint;
    /*组织Id*/
    private String companyId;
    /*同步指令*/
    private String syncCmd;
    /*同步结果*/
    private int syncResult;
    /*最后同步时间*/
    private LocalDateTime last_sync_time;
    /*重试次数*/
    private int retryTimes;
    /*备注*/
    private String remarks;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getSyncCmd() {
        return syncCmd;
    }

    public void setSyncCmd(String syncCmd) {
        this.syncCmd = syncCmd;
    }

    public int getSyncResult() {
        return syncResult;
    }

    public void setSyncResult(int syncResult) {
        this.syncResult = syncResult;
    }

    public LocalDateTime getLast_sync_time() {
        return last_sync_time;
    }

    public void setLast_sync_time(LocalDateTime last_sync_time) {
        this.last_sync_time = last_sync_time;
    }

    public int getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
