package com.maoding.corp.module.corpclient.dao;

import com.maoding.core.base.BaseDao;
import com.maoding.corp.module.corpclient.model.SyncTaskDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Wuwq on 2017/2/14.
 */
public interface SyncTaskDAO extends BaseDao<SyncTaskDO> {

    /**
     * 查询未完成的任务
     */
    List<SyncTaskDO> selectUnfinishedTasks(@Param("groupId") String groupId, @Param("count") int count);


}
