package com.maoding.corpbll.module.corpclient.dao;

import com.maoding.core.base.BaseDao;
import com.maoding.corpbll.module.corpclient.model.SyncTaskDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Wuwq on 2017/2/14.
 */
public interface SyncTaskDao extends BaseDao<SyncTaskDO> {

    /**
     * 查询未完成的任务
     */
    List<SyncTaskDO> selectUnfinishedTasks(@Param("groupId") String groupId, @Param("count") int count);


}
