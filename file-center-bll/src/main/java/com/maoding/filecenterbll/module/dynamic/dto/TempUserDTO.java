package com.maoding.filecenterbll.module.dynamic.dto;

import com.sun.org.apache.xpath.internal.operations.String;

/**
 * Created by Chengliang.zhang on 2017/6/5.
 */
public class TempUserDTO {
    /**
     * 用户编号
     */
    String userId;
    /**
     * 公司雇员编号
     */
    String companyUserId;
    /**
     * 用户名称
     */
    String userName;
    /**
     * 用户别名
     */
    String aliasName;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCompanyUserId() {
        return companyUserId;
    }

    public void setCompanyUserId(String companyUserId) {
        this.companyUserId = companyUserId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }
}
