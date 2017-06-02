package com.maoding.corpbll.module.corpserver.dao;

import com.maoding.core.base.BaseDao;
import com.maoding.corpbll.module.corpserver.dto.SyncCompanyDto_Select;
import com.maoding.corpbll.module.corpserver.model.SyncCompany;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Wuwq on 2017/2/9.
 */
public interface SyncCompanyDao extends BaseDao<SyncCompany> {
    List<SyncCompanyDto_Select> selectSyncCompany(@Param("corpEndpoint") String corpEndpoint);
}
