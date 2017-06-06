package com.maoding.corpbll.module.corpserver.dao;


import com.maoding.core.base.BaseDao;
import com.maoding.corpbll.module.corpserver.dto.CoCompanyDTO;
import com.maoding.corpbll.module.corpserver.dto.CoProjectProcessDTO;
import com.maoding.corpbll.module.corpserver.dto.CoUserDTO;
import com.maoding.corpbll.module.corpserver.dto.ProjectDTO;
import com.maoding.corpbll.module.corpserver.model.AccountDo;
import com.maoding.corpbll.module.corpserver.model.CollaborationDo;
import com.maoding.corpbll.module.corpserver.model.ProjectTaskDo;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface CollaborationDao extends BaseDao<CollaborationDo> {

    AccountDo getAccountByCellphone(@Param("cellphone") String cellphone);

    List<CoCompanyDTO> listCompanyByIds(@Param("companyIds") List<String> companyIds);

    List<ProjectDTO> listProjectByCompanyId(@Param("companyId") String companyId, @Param("syncDate") String syncDate);

    ProjectDTO getProjectById(@Param("projectId") String projectId);

    List<CoUserDTO> listUserByCompanyId(@Param("companyId") String companyId);

    List<CoProjectProcessDTO> listProjectProcessByTaskId(@Param("taskId") String taskId, @Param("syncDate") String syncDate);

    List<ProjectTaskDo> listProjectTask(@Param("projectId") String projectId, @Param("syncDate") String syncDate);

    CoUserDTO getTaskPrincipal(@Param("projectTaskId") String projectTaskId);

    Long sumDocmgrSizeByCompanyId(@Param("companyId") String companyId);
}
