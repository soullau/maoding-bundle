package com.maoding.corpbll.module.corpserver.dao;


import com.maoding.core.base.BaseDao;
import com.maoding.corpbll.module.corpserver.dto.CoCompany;
import com.maoding.corpbll.module.corpserver.dto.CoProjectProcess;
import com.maoding.corpbll.module.corpserver.dto.CoUser;
import com.maoding.corpbll.module.corpserver.dto.ProjectDTO;
import com.maoding.corpbll.module.corpserver.model.AccountEntity;
import com.maoding.corpbll.module.corpserver.model.CollaborationEntity;
import com.maoding.corpbll.module.corpserver.model.ProjectTaskEntity;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;


public interface CollaborationDao extends BaseDao<CollaborationEntity> {

    AccountEntity getAccountByCellphone(@Param("cellphone") String cellphone);

    List<CoCompany> listCompanyByIds(@Param("companyIds") List<String> companyIds);

    List<ProjectDTO> listProjectByCompanyId(@Param("companyId") String companyId, @Param("syncDate") String syncDate);

    ProjectDTO getProjectById(@Param("projectId") String projectId);

    List<CoUser> listUserByCompanyId(@Param("companyId") String companyId);

    List<CoProjectProcess> listProjectProcessByTaskId(@Param("taskId") String taskId, @Param("syncDate") String syncDate);

    List<ProjectTaskEntity> listProjectTask(@Param("projectId") String projectId, @Param("syncDate") String syncDate);

    CoUser getTaskPrincipal(@Param("projectTaskId") String projectTaskId);

    Long sumDocmgrSizeByCompanyId(@Param("companyId") String companyId);
}
