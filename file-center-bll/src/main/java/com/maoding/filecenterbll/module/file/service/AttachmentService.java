package com.maoding.filecenterbll.module.file.service;

import com.maoding.core.bean.ApiResult;
import com.maoding.filecenterbll.module.file.dto.DeleteDTO;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Wuwq on 2017/05/27.
 */
public interface AttachmentService {

    /**
     * 上传公司logo图片
     */
    ApiResult uploadCompanyLogo(HttpServletRequest request) throws Exception;


    /**
     * 上传公司轮播图片
     */
    ApiResult uploadCompanyBanner(HttpServletRequest request) throws Exception;


    /**
     * 上传报销附件
     */
    ApiResult uploadExpenseAttach(HttpServletRequest request) throws Exception;

    /**
     * 上传项目合同扫描件
     */
    ApiResult uploadProjectContract(HttpServletRequest request) throws Exception;


    /**
     * 删除附件
     */
    ApiResult delete(DeleteDTO dto);
}
