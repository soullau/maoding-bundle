package com.maoding.admin.module.historyData;

import com.maoding.admin.module.historyData.dto.ImportResultDTO;
import com.maoding.admin.module.historyData.dto.ProjectImportDTO;
import com.maoding.admin.module.historyData.service.ImportService;
import com.maoding.core.base.BaseController;
import com.maoding.core.bean.ApiRequestInsert;
import com.maoding.core.bean.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Chengliang.zhang on 2017/7/19.
 */
@Controller
@RequestMapping("/import")
public class importController extends BaseController {
    @Autowired
    ImportService importService;

    @RequestMapping(value = "/projects", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult importProjects(@RequestBody ApiRequestInsert<ProjectImportDTO> request) {
        ImportResultDTO result = importService.importProjects(request);
        return ApiResult.success(result);
    }
}
