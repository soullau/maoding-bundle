package com.maoding.admin.module.historyData.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Chengliang.zhang on 2017/7/19.
 */
public interface CompanyDAO {
    String getCompanyIdByCompanyNameAndUserName(@Param("companyName") String companyName, @Param("userName") String userName);
    String getCompanyIdByCompanyNameForA(@Param("companyName") String companyName, @Param("relateCompanyId") String relateCompanyId);
    String getCompanyIdByCompanyNameForB(@Param("companyName") String companyName, @Param("relateCompanyId") String relateCompanyId);
    List<String> listUserIdByCompanyIdAndPermissionId(@Param("companyId") String companyId, @Param("permissionId") String permissionId);
    String getCompanyUserIdByCompanyIdAndUserId(@Param("companyId") String companyId, @Param("userId") String userId);
}
