package com.maoding.im.module.imAccount.dao;

import com.maoding.core.base.BaseDao;
import com.maoding.im.module.imAccount.model.ImAccountDO;

public interface ImAccountDAO extends BaseDao<ImAccountDO> {
    int updateWithOptimisticLock(ImAccountDO o);
}
