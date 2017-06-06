package com.maoding.corpbll.module.corpserver.dao;

import com.maoding.core.base.BaseDao;
import com.maoding.corpbll.module.corpserver.dto.SyncCompanyDTO_Select;
import com.maoding.corpbll.module.corpserver.model.SyncCompanyDo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Wuwq on 2017/2/9.
 */
public interface SyncCompanyDao extends BaseDao<SyncCompanyDo> {
    List<SyncCompanyDTO_Select> selectSyncCompany(@Param("corpEndpoint") String corpEndpoint);
}
