package com.maoding.admin.module.orgAuth;

import com.maoding.admin.module.orgAuth.dto.OrgAuthAuditDTO;
import com.maoding.admin.module.orgAuth.dto.OrgAuthDTO;
import com.maoding.admin.module.orgAuth.dto.OrgAuthPageDTO;
import com.maoding.admin.module.orgAuth.dto.OrgAuthQueryDTO;
import com.maoding.admin.module.orgAuth.service.OrgAuthService;
import com.maoding.core.bean.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Wuwq on 2017/07/11.
 */
@RestController
@RequestMapping("/admin/orgCenter")
public class OrgAuthController {

    @Autowired
    OrgAuthService orgAuthService;

    /**
     * 方法：进行审核
     * 作者：zhangchengliang
     * 日期：2017/7/11
     */
    @RequestMapping(value = "/authorizeAuthentication", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult authorizeAuthentication(@RequestBody OrgAuthAuditDTO authorizeResult) throws Exception {
        OrgAuthDTO result = orgAuthService.authorizeAuthentication(authorizeResult);
        return (result!=null) ? ApiResult.success(result) : ApiResult.failed("审核失败");
    }

    /**
     * 方法：列出申请审核记录
     * 作者：zhangchengliang
     * 日期：2017/7/11
     */
    @RequestMapping(value = "/listAuthentication", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult listAuthentication(@RequestBody OrgAuthQueryDTO query) throws Exception {
        List<OrgAuthDTO> result = orgAuthService.listAuthentication(query);
        return ApiResult.success(result);
    }

    /**
     * 方法：按页列出申请审核记录
     * 作者：zhangchengliang
     * 日期：2017/7/11
     */
    @RequestMapping(value = "/getAuthenticationPage", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult getAuthenticationPage(@RequestBody OrgAuthQueryDTO query) throws Exception {
        OrgAuthPageDTO result = orgAuthService.getAuthenticationPage(query);
        return ApiResult.success(result);
    }
}
