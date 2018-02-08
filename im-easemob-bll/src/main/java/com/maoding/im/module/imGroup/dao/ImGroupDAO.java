package com.maoding.im.module.imGroup.dao;

import com.maoding.core.base.BaseDao;
import com.maoding.im.module.imGroup.model.ImGroupDO;

public interface ImGroupDAO extends BaseDao<ImGroupDO> {
    int updateWithOptimisticLock(ImGroupDO o);
}
