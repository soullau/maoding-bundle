package com.maoding.admin.module.historyData.dao;

import org.apache.ibatis.annotations.Param;

/**
 * Created by Chengliang.zhang on 2017/7/19.
 */
public interface CompanyDAO {
    String getCompanyIdByCompanyName(@Param("companyName") String companyName, @Param("relatedCompanyId") String relatedCompanyId);
}
