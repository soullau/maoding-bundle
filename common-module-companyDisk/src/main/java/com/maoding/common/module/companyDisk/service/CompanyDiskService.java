package com.maoding.common.module.companyDisk.service;


import com.maoding.core.bean.ApiResult;


public interface CompanyDiskService {

    /**
     * 初始化组织空间
     */
    ApiResult initDisk(String companyId);

    /**
     * 初始化所有组织空间
     */
    ApiResult initAllDisk();

    /**
     * 当添加文件时重新计算容量
     */
    ApiResult recalcSizeOnAddFile(String companyId, Long fileSize);

    /**
     * 当删除文件时重新计算容量
     */
    ApiResult recalcSizeOnRemoveFile(String companyId, Long fileSize);

    /**
     * 根据组织ID统计文档库大小
     */
    ApiResult sumDocmgrSizeByCompanyId(String companyId);

    /**
     * 根据组织ID获取公司网盘容量信息
     */
    ApiResult getCompanyDiskInfo(String companyId);

    /**
     * 根据组织ID更新协同占用空间
     */
    ApiResult updateCorpSizeOnCompanyDisk(String companyId,Long corpSize);
}
