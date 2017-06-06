package com.maoding.filecenterbll.module.dynamic.dto;

/**
 * Created by Chengliang.zhang on 2017/6/6.
 * 用于查找CompanyUser记录
 */
public class QueryUserDTO {
    /**
     * 组织编号
     */
    private String companyId;
    /**
     * 用户编号
     */
    private String userId;

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
