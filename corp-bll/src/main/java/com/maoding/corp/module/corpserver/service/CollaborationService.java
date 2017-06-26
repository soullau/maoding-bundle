package com.maoding.corp.module.corpserver.service;


import com.maoding.core.bean.ApiResult;
import com.maoding.corp.module.corpserver.dto.CoCompanyDTO;
import com.maoding.corp.module.corpserver.dto.CoProjectPhaseDTO;
import com.maoding.corp.module.corpserver.dto.CoUserDTO;
import com.maoding.corp.module.corpserver.dto.ProjectDTO;

import java.util.List;
import java.util.Map;


public interface CollaborationService {


    List<CoCompanyDTO> listCompanyByIds(List<String> companyIds);


    /**
     * 根据组织Id获取项目列表
     */
    List<ProjectDTO> listProjectByCompanyId(String companyId, String syncDate) throws Exception;

    /**
     * 根据projectId获取项目信息
     */
    ProjectDTO getProjectById(String projectId);


    /**
     * 获取项目节点（含任务成员状态信息）
     */
    List<CoProjectPhaseDTO> listNode(String companyId, String projectId) throws Exception;


    /**
     * 登录接口
     */
    ApiResult login(Map<String, Object> map) throws Exception;

    /**
     * 协同同步通知
     */
    void pushChanges(String companyId, String type, String projectId);

    /**
     * 完成我的任务
     */
    ApiResult finishMyTask(String processNodeId);

    /**
     * 激活我的任务
     */
    ApiResult activeMyTask(String processNodeId);

    /**
     * 根据组织Id获取用户列表
     */
    List<CoUserDTO> listUserByCompanyId(String companyId);
}