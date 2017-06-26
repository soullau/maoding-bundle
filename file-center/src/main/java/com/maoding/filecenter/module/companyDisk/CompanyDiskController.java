package com.maoding.filecenter.module.companyDisk;

import com.maoding.common.module.companyDisk.service.CompanyDiskService;
import com.maoding.core.bean.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Wuwq on 2017/05/26.
 */
@Controller
@RequestMapping("/fileCenter/companyDisk")
public class CompanyDiskController {

    @Autowired
    private CompanyDiskService companyDiskService;

    @RequestMapping("/initAllDisk")
    @ResponseBody
    public ApiResult initAllDisk() {
        return companyDiskService.initAllDisk();
    }

    @RequestMapping("/initDisk/{companyId}")
    @ResponseBody
    public ApiResult initDisk(@PathVariable String companyId) {
        return companyDiskService.initDisk(companyId);
    }
}
