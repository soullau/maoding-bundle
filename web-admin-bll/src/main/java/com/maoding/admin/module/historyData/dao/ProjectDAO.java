package com.maoding.admin.module.historyData.dao;

import com.maoding.admin.module.historyData.dto.MemberQueryDTO;
import com.maoding.admin.module.historyData.dto.ProjectQueryDTO;
import com.maoding.admin.module.historyData.model.ProjectDO;
import com.maoding.admin.module.historyData.model.ProjectMemberDO;
import com.maoding.core.base.BaseDao;

/**
 * Created by Chengliang.zhang on 2017/7/20.
 */
public interface ProjectDAO extends BaseDao<ProjectDO> {
    ProjectDO getProject(ProjectQueryDTO query);

}
