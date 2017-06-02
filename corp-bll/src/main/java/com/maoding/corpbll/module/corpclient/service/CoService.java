package com.maoding.corpbll.module.corpclient.service;

import com.maoding.core.bean.ApiResult;
import com.maoding.corpbll.module.corpclient.dto.PushResult;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Created by Wuwq on 2017/4/24.
 */
public interface CoService {
    /**
     * 登录接口
     */
    ApiResult login(Map<String, Object> param) throws Exception;

    /**
     * 设置节点完成状态
     */
    ApiResult handleMyTaskByProjectNodeId(Map<String, Object> param) throws Exception;

    /**
     * 通过组织Id推送项目信息
     */
    ApiResult pushProjectByCompanyId(String companyId, LocalDateTime syncDate) throws Exception;

    /**
     * 根据组织Id推送人员信息
     */
    ApiResult pushUserByCompanyId(String companyId, LocalDateTime syncDate) throws Exception;

    /**
     * 设置项目
     */
    ApiResult setProject(String companyId, String projectId, LocalDateTime syncDate) throws Exception;

    /**
     * 设置项目节点（含任务成员状态信息）
     */
    ApiResult setProjectNodes(String companyId, String projectId, LocalDateTime syncDate) throws Exception;

    /**
     *  发布项目（同步任务文件夹）
     */
    ApiResult publicProject(String projectId) throws Exception;
}
