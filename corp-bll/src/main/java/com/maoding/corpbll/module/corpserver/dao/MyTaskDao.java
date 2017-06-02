package com.maoding.corpbll.module.corpserver.dao;

import com.maoding.core.base.BaseDao;
import com.maoding.corpbll.module.corpserver.model.MyTaskEntity;

/**
 * Created by Wuwq on 2017/05/25.
 */
public interface MyTaskDao extends BaseDao<MyTaskEntity> {

    MyTaskEntity getMyTaskByProcessNodeId(String processNodeId);

    MyTaskEntity getPrincipalTaskByProjectTaskId(String projectTaskId);

    int updateMyTaskAsFinished(String myTaskId);

    int updateMyTaskAsActived(String myTaskId);

    int updateMyTaskAsInvalid(String myTaskId);
}
