package com.maoding.filecenterbll.module.file.service;

import com.maoding.core.bean.ApiResult;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Wuwq on 2017/05/27.
 */
public interface AttachmentService {

    /**
     * 方法描述：上传公司logo图片
     * 作者：MaoSF
     * 日期：2017/6/1
     * @param:
     * @return:
     */
    ApiResult uploadCompanyLogo(HttpServletRequest request) throws Exception;


    /**
     * 方法描述：上传公司轮播图片
     * 作者：MaoSF
     * 日期：2017/6/1
     * @param:
     * @return:
     */
    ApiResult uploadCompanyBanner(HttpServletRequest request) throws Exception;


    /**
     * 方法描述：上传报销附件
     * 作者：MaoSF
     * 日期：2017/6/1
     * @param:
     * @return:
     */
    ApiResult uploadExpenseAttach(HttpServletRequest request) throws Exception;


    /**
     * 上传项目合同扫描件
     */
    ApiResult uploadProjectContract(HttpServletRequest request) throws Exception;
}
