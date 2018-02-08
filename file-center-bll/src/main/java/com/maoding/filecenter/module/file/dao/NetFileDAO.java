package com.maoding.filecenter.module.file.dao;

import com.maoding.core.base.BaseDao;
import com.maoding.filecenter.module.file.dto.DirectoryDTO;
import com.maoding.filecenter.module.file.model.NetFileDO;

import java.util.List;

/**
 * Created by Wuwq on 2017/05/31.
 */
public interface NetFileDAO extends BaseDao<NetFileDO> {
    /**
     * 查询文件夹顺序
     */
    List<DirectoryDTO> getDirectoryDTOList(DirectoryDTO dir);

}
