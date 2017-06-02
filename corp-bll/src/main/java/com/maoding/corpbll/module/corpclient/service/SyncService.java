package com.maoding.corpbll.module.corpclient.service;

import com.maoding.core.bean.ApiResult;
import com.maoding.corpbll.module.corpclient.dto.PushResult;

import java.util.Map;

/**
 * Created by Wuwq on 2017/4/24.
 */
public interface SyncService {
    /**
     * 拉取变更
     */
    void pullChanges(String corpEndpoint) throws Exception;

    /**
     * 拉取指定组织的变更
     */
    void pullChangesByCompany(String corpEndpoint, String companyId);

    /**
     * 从业务系统拉取数据（GET）
     */
    ApiResult pullFromCorpServer(String url) throws Exception;

    /**
     * 从业务系统拉取数据（POST）
     */
    ApiResult pullFromCorpServer(Map<String, Object> param, String url) throws Exception;


    /**
     * 推送到对方协同服务端
     */
    PushResult pushToSOWServer(Object param, String url) throws Exception;
}
