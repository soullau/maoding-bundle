package com.maoding.corpbll.module.corpclient.service;

import com.maoding.core.bean.ApiResult;

/**
 * Created by Wuwq on 2017/2/14.
 */
public interface SyncTaskService {



    /**
     * 执行同步任务
     */
    void runSyncTask(String corpEndpoint) throws Exception;

    /**
     * 启动时修改执行中的状态
     */
    void runUpdateStatusTask();

//    /**
//     * 同步组织
//     */
//    void syncCompany(String corpEndpoint) throws Exception;

    /**
     * 同步组织人员和项目
     */
    void syncAllCompanyUserAndProject(String corpEndpoint) throws Exception;

    /**
     * 同步组织人员和项目
     */
    ApiResult syncOneCompanyUserAndProject(String companyId);
}
