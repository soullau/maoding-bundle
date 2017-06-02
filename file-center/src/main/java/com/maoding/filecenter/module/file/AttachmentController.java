package com.maoding.filecenter.module.file;

import com.maoding.core.bean.ApiResult;
import com.maoding.filecenterbll.module.file.service.AttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
    public ApiResult uploadFile(HttpServletRequest request) throws Exception {
        return attachmentService.uploadProjectContract(request);
    }
}
