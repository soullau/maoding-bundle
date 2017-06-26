package com.maoding.common.module.companyDisk.dao;


import com.maoding.core.base.BaseDao;
import com.maoding.common.module.companyDisk.model.CompanyDiskDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Wuwq on 2017/05/26.
 */
public interface CompanyDiskDAO extends BaseDao<CompanyDiskDO> {

    List<CompanyDiskDO> listAll();

    List<String> listUnInitCompanyId();

    int updateWithOptimisticLock(CompanyDiskDO o);

    Long sumDocmgrSizeByCompanyId(@Param("companyId") String companyId);
}
