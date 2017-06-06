package com.maoding.corpbll.module.corpserver.dao;

import com.maoding.core.base.BaseDao;
import com.maoding.corpbll.module.corpserver.model.MyTaskDo;

/**
 * Created by Wuwq on 2017/05/25.
 */
public interface MyTaskDao extends BaseDao<MyTaskDo> {

    MyTaskDo getMyTaskByProcessNodeId(String processNodeId);

    MyTaskDo getPrincipalTaskByProjectTaskId(String projectTaskId);

    int updateMyTaskAsFinished(String myTaskId);

    int updateMyTaskAsActived(String myTaskId);

    int updateMyTaskAsInvalid(String myTaskId);
}
