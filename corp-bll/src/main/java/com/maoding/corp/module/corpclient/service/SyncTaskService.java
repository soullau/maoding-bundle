package com.maoding.corp.module.corpclient.service;

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
     * 启动时把任务状态为“执行中”的重置为“等待执行”
     */
    void resetTaskStatus();

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
    ApiResult syncOneCompany(String companyId);
}
