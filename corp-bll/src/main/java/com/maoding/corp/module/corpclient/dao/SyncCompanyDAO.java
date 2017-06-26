package com.maoding.corp.module.corpclient.dao;

import com.maoding.core.base.BaseDao;
import com.maoding.corp.module.corpclient.dto.SyncCompanyDTO_Select;
import com.maoding.corp.module.corpclient.model.SyncCompanyDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Wuwq on 2017/2/9.
 */
public interface SyncCompanyDAO extends BaseDao<SyncCompanyDO> {
    List<SyncCompanyDTO_Select> selectSyncCompany(@Param("corpEndpoint") String corpEndpoint);
}
