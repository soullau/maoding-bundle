package com.maoding.corpbll.module.corpclient.dao;

import com.maoding.core.base.BaseDao;
import com.maoding.corpbll.module.corpclient.model.SyncTaskGroup;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Wuwq on 2017/2/16.
 */
public interface SyncTaskGroupDao extends BaseDao<SyncTaskGroup> {

    /**
     * 查询未完成的任务组
     */
    List<SyncTaskGroup> selectUnfinishedTaskGroups(@Param("corpEndpoint") String corpEndpoint, @Param("companyId") String companyId, @Param("count") int count);

    /**
     * 更新状态为：待同步
     */
    @ResultType(Integer.class)
    @Update("update sync_task_group set task_group_status = 0 , update_date = now() where id= #{id} and task_group_status = #{oldStatus}")
    int updateAsWaitRunningStatus(@Param("id") String id, @Param("oldStatus") int oldStatus);

    /**
     * 更新状态为：执行中
     */
    @ResultType(Integer.class)
    @Update("update sync_task_group set task_group_status = 1 , last_entry = #{lastEntry} , update_date= now()  where id= #{id} and task_group_status = #{oldStatus} ")
    int updateAsRunningStatus(@Param("id") String id, @Param("oldStatus") int oldStatus, @Param("lastEntry") LocalDateTime lastEntry);

    /**
     * 更新状态为：已结束
     */
    @ResultType(Integer.class)
    @Update("update sync_task_group set task_group_status = 2 , update_date = now() where id= #{id} and task_group_status = #{oldStatus}")
    int updateAsFinishedStatus(@Param("id") String id, @Param("oldStatus") int oldStatus);

    /**
     * 更新状态为：待同步
     */
    @ResultType(Integer.class)
    @Update("update sync_task_group set task_group_status = 0 , update_date = now() where task_group_status = #{oldStatus}")
    int updateAsWaitRunningOldStatus(@Param("oldStatus") int oldStatus);
}
