package com.maoding.corp.module.corpclient.dto;

/**
 * Created by Wuwq on 2017/2/9.
 * 同步记录
 */
public class SyncHistoryDTO_Create {
    /*协同端id*/
    private String corpEndpoint;
    /*组织Id*/
    private String companyId;
    /*同步指令*/
    private String syncCmd;
    /*同步结果 0:未开始 1：成功 2：待重试 3：失败*/
    private int syncResult;

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
}
