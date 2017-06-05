package com.maoding.filecenterbll.module.dynamic.service;

import com.maoding.core.base.BaseService;
import com.maoding.filecenterbll.module.dynamic.model.DynamicDO;
import com.maoding.filecenterbll.module.dynamic.model.ProjectDynamicsDO;
import com.maoding.filecenterbll.module.file.dto.DeleteDTO;
import com.maoding.filecenterbll.module.file.dto.DirectoryDTO;
import com.maoding.filecenterbll.module.file.dto.RenameDTO;
import com.maoding.filecenterbll.module.file.model.NetFileDO;
import org.springframework.stereotype.Service;

/**
 * Created by Chengliang.zhang on 2017/6/5.
 */
@Service("dynamicService")
public class DynamicServiceImpl extends BaseService implements DynamicService{
    /**
     * 创建文件类项目动态
     */
    @Override
    public void createDynamic(DirectoryDTO dto) {

    }

    @Override
    public void createDynamic(RenameDTO dto) {

    }

    @Override
    public void createDynamic(DeleteDTO dto) {

    }

    @Override
    public void createDynamic(NetFileDO dto) {

    }

    /**
     * 转换新表设计为原有设计（保持兼容性）
     *
     * @param data 要转换的数据
     */
    @Override
    public ProjectDynamicsDO toProjectDynamics(DynamicDO data) {
        return null;
    }
}
