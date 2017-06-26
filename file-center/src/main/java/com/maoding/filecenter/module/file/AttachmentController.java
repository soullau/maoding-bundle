package com.maoding.filecenter.module.file;

import com.maoding.core.bean.ApiResult;
import com.maoding.filecenter.module.file.dto.DeleteDTO;
import com.maoding.filecenter.module.file.dto.NetFileOrderDTO;
import com.maoding.filecenter.module.file.service.AttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Wuwq on 2017/05/27.
 */
@RestController
@RequestMapping("/fileCenter/attachment")
public class AttachmentController {

    @Autowired
    private AttachmentService attachmentService;

    /**
     * 上传项目合同扫描件
     */
    @RequestMapping(value = "/uploadProjectContract", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult uploadProjectContract(HttpServletRequest request) throws Exception {
        return attachmentService.uploadProjectContract(request);
    }

    /**
     * 上传公司logo
     */
    @RequestMapping(value = "/uploadCompanyLogo", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult uploadCompanyLogo(HttpServletRequest request) throws Exception {
        return attachmentService.uploadCompanyLogo(request);
    }

    /**
     * 上传公司轮播图
     */
    @RequestMapping(value = "/uploadCompanyBanner", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult uploadCompanyBanner(HttpServletRequest request) throws Exception {
        return attachmentService.uploadCompanyBanner(request);
    }

    /**
     * 调整公司轮播图片顺序
     */
    @RequestMapping(value = "/orderCompanyBanner", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult orderCompanyBanner(@RequestBody NetFileOrderDTO dto) throws Exception {
        return attachmentService.orderCompanyBanner(dto);
    }

    /**
     * 上传报销附件
     */
    @RequestMapping(value = "/uploadExpenseAttach", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult uploadExpenseAttach(HttpServletRequest request) throws Exception {
        return attachmentService.uploadExpenseAttach(request);
    }

    /**
     * 删除文件
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult deleteFile(@RequestBody DeleteDTO dto) {
        return attachmentService.delete(dto);
    }
}
