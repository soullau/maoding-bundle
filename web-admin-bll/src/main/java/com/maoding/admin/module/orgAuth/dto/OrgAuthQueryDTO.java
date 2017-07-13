package com.maoding.admin.module.orgAuth.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.maoding.core.base.BaseQueryDTO;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Created by Chengliang.zhang on 2017/7/12.
 */
public class OrgAuthQueryDTO extends BaseQueryDTO {
    /**
     * 认证状态(0.否，1.是，2申请认证)
     */
    Integer authenticationStatus;

    /**
     * 有效期需在此日期之前
     */
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    Date expiryDateBefore;

    /**
     * 组织法定名称过滤条件
     */
    String orgNameMask;
    /**
     * 组织在卯丁内定义名称的过滤条件
     */
    String orgAliasMask;

    public Integer getAuthenticationStatus() {
        return authenticationStatus;
    }

    public void setAuthenticationStatus(Integer authenticationStatus) {
        this.authenticationStatus = authenticationStatus;
    }

    public Date getExpiryDateBefore() {
        return expiryDateBefore;
    }

    public void setExpiryDateBefore(Date expiryDateBefore) {
        this.expiryDateBefore = expiryDateBefore;
    }

    public String getOrgNameMask() {
        return orgNameMask;
    }

    public void setOrgNameMask(String orgNameMask) {
        this.orgNameMask = orgNameMask;
    }

    public String getOrgAliasMask() {
        return orgAliasMask;
    }

    public void setOrgAliasMask(String orgAliasMask) {
        this.orgAliasMask = orgAliasMask;
    }

}
