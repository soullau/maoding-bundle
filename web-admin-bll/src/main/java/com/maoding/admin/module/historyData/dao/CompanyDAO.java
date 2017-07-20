package com.maoding.admin.module.historyData.dao;

import org.apache.ibatis.annotations.Param;

/**
 * Created by Chengliang.zhang on 2017/7/19.
 */
public interface CompanyDAO {
    String getCompanyIdByCompanyNameAndUserName(@Param("companyName") String companyName, @Param("userName") String userName);
    String getCompanyIdByCompanyNameForA(@Param("companyName") String companyName);
    String getCompanyIdByCompanyNameForB(@Param("companyName") String companyName, @Param("relateCompanyId") String relateCompanyId);
}
