package com.maoding.corp.module.corpclient.service;

import com.maoding.core.bean.ApiResult;
import com.maoding.corp.module.corpclient.model.SyncTaskDO;

/**
 * Created by Wuwq on 2017/2/14.
 */
public interface SyncTaskService {

    /**
     * 同步一个任务
     */
    void runOneTask(String syncTaskId);


    /**
     * 执行同步任务
     */
    void runSyncTask(String corpEndpoint) throws Exception;

    /**
     * 启动时把任务状态为“执行中”的重置为“等待执行”
     */
    void resetTaskStatusOnStartup();

    /**
     * 同步协同占用
     */
    ApiResult syncCorpSizeByCompanyId(String companyId);
}
