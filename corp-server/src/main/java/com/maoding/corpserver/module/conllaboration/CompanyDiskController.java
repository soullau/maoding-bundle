package com.maoding.corpserver.module.conllaboration;

import com.maoding.common.module.companyDisk.service.CompanyDiskService;
import com.maoding.core.bean.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by Wuwq on 2017/06/07.
 */
@RestController
@RequestMapping("/corpserver/collaboration")
public class CompanyDiskController {

    @Autowired
    private CompanyDiskService companyDiskService;

    /**
     * 根据组织Id统计文档库大小
     */
    @RequestMapping(value = "/sumDocmgrSizeByCompanyId/{companyId}", method = RequestMethod.GET)
    @ResponseBody
    public ApiResult sumDocmgrSizeByCompanyId(@PathVariable String companyId) {
        return companyDiskService.sumDocmgrSizeByCompanyId(companyId);
    }

    /**
     * 根据组织ID获取公司网盘容量信息
     */
    @RequestMapping(value = "/getCompanyDiskInfo", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult getCompanyDiskInfo(@RequestBody Map<String, Object> param) {
        String companyId = (String) param.get("companyId");
        return companyDiskService.getCompanyDiskInfo(companyId);
    }

    /**
     * 根据组织ID更新协同占用空间
     */
    @RequestMapping(value = "/updateCorpSizeOnCompanyDisk", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult updateCorpSizeOnCompanyDisk(@RequestBody Map<String, Object> param) {
        String companyId = (String) param.get("companyId");
        Long corpSize = Long.parseLong((String) param.get("corpSize"));
        return companyDiskService.updateCorpSizeOnCompanyDisk(companyId, corpSize);
    }
}
