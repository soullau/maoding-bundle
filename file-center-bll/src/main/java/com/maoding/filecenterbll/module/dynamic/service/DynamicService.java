package com.maoding.filecenterbll.module.dynamic.service;

import com.maoding.filecenterbll.module.dynamic.model.DynamicDO;
import com.maoding.filecenterbll.module.file.model.NetFileDO;

/**
 * Created by Chengliang.zhang on 2017/6/5.
 */
public interface DynamicService {
    /**
     * 记录项目动态
     */
    void addDynamic(DynamicDO dynamic);

    /**
     * 生成创建对象类项目动态
     */
    DynamicDO createDynamicFrom(NetFileDO dto);

    /**
     * 生成更改对象类项目动态
     */
    DynamicDO createDynamicFrom(NetFileDO dtoNew, NetFileDO dtoOld);

    /**
     * 生成删除对象类项目动态
     */
    DynamicDO createDynamicFrom(NetFileDO dto, String companyUserId, String userId);
}
