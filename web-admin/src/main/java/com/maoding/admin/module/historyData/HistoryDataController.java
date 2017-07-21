package com.maoding.admin.module.historyData;

import com.maoding.admin.module.historyData.dto.ImportResultDTO;
import com.maoding.admin.module.historyData.service.ImportService;
import com.maoding.core.base.BaseController;
import com.maoding.core.bean.ApiResult;
import com.maoding.core.bean.MultipartFileParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by Chengliang.zhang on 2017/7/19.
 */
@Controller
@RequestMapping("/historyData")
public class HistoryDataController extends BaseController {

    @Autowired
    ImportService importService;

    @RequestMapping("/entry")
    public void entry() {

    }

    @RequestMapping(value = "/importProjects", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult importProjects(HttpServletRequest request) throws Exception {
        MultipartFileParam fileParam = MultipartFileParam.parse(request);

        InputStream in = fileParam.getFileItem().getInputStream();
        Map<String,Object> param = fileParam.getParam();
        String token = (param != null) ? (String)param.get("token") : null;
        ImportResultDTO result = importService.importProjects(in,token);

        return ApiResult.success(result);
    }
}
