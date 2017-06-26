package com.maoding.common.module.dynamic.dao;

import com.maoding.common.module.dynamic.dto.ZCostDTO;
import com.maoding.common.module.dynamic.dto.ZProcessNodeDTO;
import com.maoding.common.module.dynamic.dto.ZProjectDTO;
import com.maoding.common.module.dynamic.dto.ZTaskDTO;
import com.maoding.common.module.dynamic.model.*;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * Created by Chengliang.zhang on 2017/6/26.
 */
public interface ZInfoDAO {
    String getUserNameByUserId(String userId);
    String getUserNameByCompanyUserId(String companyUserId);
    String getUserIdByCompanyUserId(String companyUserId);
    String getCompanyUserIdByCompanyIdAndUserId(@Param("companyId") String companyId, @Param("userId") String userId);
    String getCompanyUserIdByCompanyIdAndUserId(Map<String,String> query);
    String getProjectNameByProjectId(String projectId);
    String getProjectAddressByProject(ProjectEntity project);
    String getProjectBuiltFloorByProject(ProjectEntity project);
    String getProjectStatusNameByStatus(String status);
    String getContractCompanyNameByCompanyId(String companyId);
    String getCompanyNameByTaskId(String taskId);
    String getCompanyNameByCompanyId(String companyId);
    String getCompanyIdByTaskId(String taskId);
    String getProduceTaskFullNameByTask(ProjectTaskEntity task);
    String getProduceTaskNameByTask(ProjectTaskEntity task);
    String getProduceTaskNameWithLeaderByTask(ProjectTaskEntity task);
    String getProduceTaskNameWithMembersByTask(ProjectTaskEntity task);
    String getPhaseTaskFullNameByTask(ProjectTaskEntity task);
    String getPhaseTaskNameByTask(ProjectTaskEntity task);
    String getPhaseTaskFullNameByDesignContent(ProjectDesignContentEntity designContent);
    String getPhaseTaskNameByDesignContent(ProjectDesignContentEntity designContent);
    String getIssueTaskFullNameByTask(ProjectTaskEntity task);
    String getIssueTaskNameByTask(ProjectTaskEntity task);
    String getIssueTaskNameWithToCompanyByTask(ProjectTaskEntity task);
    String getIssueTaskNameWithPlanByTask(ProjectTaskEntity task);

    String getPeriodByTaskId(String taskId);

    String getPeriodByDesignContentId(String designContentId);
    ProjectProcessTimeEntity getTimeByTaskId(String taskId);
    String getMembersByTaskId(String taskId);

    ZCostDTO getCostByPoint(ProjectCostPointEntity point);
    ZCostDTO getCostByDetail(ProjectCostPointDetailEntity detail);

    ZCostDTO getCostByPay(ProjectCostPaymentDetailEntity pay);
    ZTaskDTO getTaskByTaskId(String id);
    ZTaskDTO getTaskByTask(ProjectTaskEntity task);
    ZTaskDTO getTaskByTime(ProjectProcessTimeEntity time);
    ZProcessNodeDTO getProcessNodeByProcessNode(ProjectProcessNodeEntity node);
    ZProjectDTO getProjectByProject(ProjectEntity project);
    Integer getLastQueryCount();
}
