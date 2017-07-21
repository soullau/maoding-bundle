package com.maoding.admin.module.historyData.service;

import com.maoding.admin.module.historyData.dto.ImportResultDTO;

import java.io.InputStream;

/**
 * Created by Chengliang.zhang on 2017/7/19.
 */
public interface ImportService {
    /** 导入项目数据 */
    ImportResultDTO importProjects(InputStream in, String token);
}
