package com.maoding.corp.module.corpserver.dao;


import com.maoding.core.base.BaseDao;
import com.maoding.corp.module.corpserver.dto.CoCompanyDTO;
import com.maoding.corp.module.corpserver.dto.CoProjectProcessDTO;
import com.maoding.corp.module.corpserver.dto.CoUserDTO;
import com.maoding.corp.module.corpserver.dto.ProjectDTO;
import com.maoding.corp.module.corpserver.model.AccountDO;
import com.maoding.corp.module.corpserver.model.CollaborationDO;
import com.maoding.corp.module.corpserver.model.ProjectTaskDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface CollaborationDAO extends BaseDao<CollaborationDO> {

    AccountDO getAccountByCellphone(@Param("cellphone") String cellphone);

    List<CoCompanyDTO> listCompanyByIds(@Param("companyIds") List<String> companyIds);

    List<ProjectDTO> listProjectByCompanyId(@Param("companyId") String companyId, @Param("syncDate") String syncDate);

    ProjectDTO getProjectById(@Param("projectId") String projectId);

    List<CoUserDTO> listUserByCompanyId(@Param("companyId") String companyId);

    List<CoProjectProcessDTO> listProjectProcessByTaskId(@Param("taskId") String taskId, @Param("syncDate") String syncDate);

    List<ProjectTaskDO> listProjectTask(@Param("projectId") String projectId, @Param("syncDate") String syncDate);

    CoUserDTO getTaskPrincipal(@Param("projectTaskId") String projectTaskId);

    Long sumDocmgrSizeByCompanyId(@Param("companyId") String companyId);
}
