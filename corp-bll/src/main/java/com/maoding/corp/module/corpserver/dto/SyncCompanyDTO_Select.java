package com.maoding.corp.module.corpserver.dto;

/**
 * Created by Wuwq on 2017/2/9.
 * 组织同步清单
 */
public class SyncCompanyDTO_Select {

    private String id;
    /**
     * 协同端id
     */
    private String corpEndpoint;
    private String companyId;
    private String companyName;
    /**
     * 备注
     */
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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
