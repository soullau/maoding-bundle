package com.maoding.core.base;

import java.io.Serializable;

/**
 * Created by Chengliang.zhang on 2017/7/18.
 * 操作请求基类
 */
public class BaseRequest implements Serializable {
    /** 操作者用户ID */
    String userId;
    /** 操作者组织ID */
    String companyId;
    /** 操作者雇员ID */
    String companyUserId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getCompanyUserId() {
        return companyUserId;
    }

    public void setCompanyUserId(String companyUserId) {
        this.companyUserId = companyUserId;
    }
}
