package com.maoding.corpbll.module.corpserver.dao;

import com.maoding.core.base.BaseDao;
import com.maoding.corpbll.module.corpserver.model.ProjectTaskDo;

/**
 * Created by Wuwq on 2017/05/25.
 */
public interface ProjectTaskDao extends BaseDao<ProjectTaskDo> {

    int updateProcessTaskAsActived(String projectTaskId);
}
