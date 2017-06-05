package com.maoding.filecenterbll.module.dynamic.service;

import com.maoding.filecenterbll.module.dynamic.dto.DynamicDTO;
import com.maoding.filecenterbll.module.dynamic.model.DynamicDO;
import com.maoding.filecenterbll.module.dynamic.model.ProjectDynamicsDO;
import com.maoding.filecenterbll.module.file.dto.DeleteDTO;
import com.maoding.filecenterbll.module.file.dto.DirectoryDTO;
import com.maoding.filecenterbll.module.file.dto.RenameDTO;
import com.maoding.filecenterbll.module.file.model.NetFileDO;

import java.util.List;

/**
 * Created by Chengliang.zhang on 2017/6/5.
 */
public interface DynamicService {
    /**
     * 创建文件类项目动态
     */
    void createDynamic(DirectoryDTO dto);
    void createDynamic(RenameDTO dto);
    void createDynamic(DeleteDTO dto);
    void createDynamic(NetFileDO dto);

    /**
     * 转换新表设计为原有设计（保持兼容性）
     */
    ProjectDynamicsDO toProjectDynamics(DynamicDO data);
}
