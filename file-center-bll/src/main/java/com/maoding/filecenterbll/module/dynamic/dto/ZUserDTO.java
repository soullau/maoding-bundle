package com.maoding.filecenterbll.module.dynamic.dto;

/**
 * Created by Chengliang.zhang on 2017/6/5.
 * 用于查找用户名和用户雇员ID，临时使用，最终应放入用户管理服务模块中
 */
public class ZUserDTO {
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
