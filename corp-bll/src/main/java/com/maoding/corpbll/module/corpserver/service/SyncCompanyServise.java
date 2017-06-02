package com.maoding.corpbll.module.corpserver.service;

import com.maoding.core.bean.ApiResult;
import com.maoding.corpbll.module.corpserver.dto.SyncCompanyDto_Create;
import com.maoding.corpbll.module.corpserver.dto.SyncCompanyDto_Update;

/**
 * Created by Wuwq on 2017/2/10.
 */
public interface SyncCompanyServise {

    ApiResult create(SyncCompanyDto_Create dto);

    ApiResult update(SyncCompanyDto_Update dto);

    ApiResult delete(String corpEndpoint, String companyId);

    ApiResult delete(String id);

    ApiResult select(String corpEndpoint);

    ApiResult syncToRedis();

    ApiResult pushSyncCmd(String syncCompanyId);
}
