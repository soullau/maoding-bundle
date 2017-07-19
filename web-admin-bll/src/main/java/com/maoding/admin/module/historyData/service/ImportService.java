package com.maoding.admin.module.historyData.service;

import com.maoding.admin.module.historyData.dto.ProjectImportDTO;
import com.maoding.core.bean.ApiRequestInsert;

/**
 * Created by Chengliang.zhang on 2017/7/19.
 */
public interface ImportService {
    /** 导入项目数据 */
    void importProjects(ApiRequestInsert<ProjectImportDTO> request);
}