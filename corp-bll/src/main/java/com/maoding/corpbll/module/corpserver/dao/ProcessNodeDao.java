package com.maoding.corpbll.module.corpserver.dao;

import com.maoding.core.base.BaseDao;
import com.maoding.corpbll.module.corpserver.dto.CoProjectProcessNode;
import com.maoding.corpbll.module.corpserver.model.ProjectProcessNodeEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Wuwq on 2017/05/25.
 */
public interface ProcessNodeDao extends BaseDao<ProjectProcessNodeEntity> {

    List<CoProjectProcessNode> listProcessNodeByProcessId(@Param("processId") String processId, @Param("syncDate") String syncDate);

    CoProjectProcessNode getProcessNodeById(String id);

    int updateProcessNodeAsFinished(String projectNodeId);

    int updateProcessNodeAsActived(String projectNodeId);
}
