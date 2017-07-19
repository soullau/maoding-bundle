package com.maoding.admin.module.historyData.service;

import com.maoding.admin.module.historyData.dto.ProjectImportDTO;
import com.maoding.core.base.BaseService;
import com.maoding.core.bean.ApiRequestInsert;
import com.maoding.utils.ExcelUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Chengliang.zhang on 2017/7/19.
 */
@Service("importService")
public class ImportServiceImpl extends BaseService implements ImportService {
    /**
     * 导入项目数据
     *
     * @param request 操作请求
     */
    @Override
    public void importProjects(ApiRequestInsert<ProjectImportDTO> request) {
        if ((request == null) || (request.getData() == null)) throw new IllegalArgumentException("importProjects 参数错误");
        List<List<Object>> dataList = ExcelUtils.readFrom(request.getData().getImportFile(),-1,4,6,null,null);
    }
}
