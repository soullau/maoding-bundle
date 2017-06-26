package com.maoding.corp.module.corpserver.dao;

import com.maoding.core.base.BaseDao;
import com.maoding.corp.module.corpserver.dto.CoProjectProcessNodeDTO;
import com.maoding.corp.module.corpserver.model.ProjectProcessNodeDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Wuwq on 2017/05/25.
 */
public interface ProcessNodeDAO extends BaseDao<ProjectProcessNodeDO> {

    List<CoProjectProcessNodeDTO> listProcessNodeByProcessId(@Param("processId") String processId, @Param("syncDate") String syncDate);

    CoProjectProcessNodeDTO getProcessNodeById(String id);

    int updateProcessNodeAsFinished(String projectNodeId);

    int updateProcessNodeAsActived(String projectNodeId);
}
