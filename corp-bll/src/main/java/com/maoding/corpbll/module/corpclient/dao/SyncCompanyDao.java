package com.maoding.corpbll.module.corpclient.dao;

import com.maoding.core.base.BaseDao;
import com.maoding.corpbll.module.corpclient.dto.SyncCompanyDTO_Select;
import com.maoding.corpbll.module.corpclient.model.SyncCompanyDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Wuwq on 2017/2/9.
 */
public interface SyncCompanyDao extends BaseDao<SyncCompanyDO> {
    List<SyncCompanyDTO_Select> selectSyncCompany(@Param("corpEndpoint") String corpEndpoint);
}
