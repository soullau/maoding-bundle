package com.maoding.common.module.companyDisk.service;


import com.google.common.collect.Lists;
import com.maoding.core.base.BaseService;
import com.maoding.core.bean.ApiResult;
import com.maoding.common.module.companyDisk.dao.CompanyDiskDAO;
import com.maoding.common.module.companyDisk.model.CompanyDiskDO;
import com.maoding.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service("companyDiskService")
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class CompanyDiskServiceImpl extends BaseService implements CompanyDiskService {

    private static final Logger log = LoggerFactory.getLogger(CompanyDiskServiceImpl.class);
    private final static Long INIT_DISK_SIZE = 5L * 1024L * 1024L * 1024L;
    @Autowired
    private CompanyDiskDAO companyDiskDao;

    private ApiResult initOne(String companyId) {
        CompanyDiskDO cd = new CompanyDiskDO();
        cd.initEntity();
        cd.setId(companyId);
        cd.setCompanyId(companyId);
        cd.setTotalSize(INIT_DISK_SIZE);
        cd.setCorpSize(0L);
        cd.setDocmgrSize(0L);
        cd.setUpVersion(1L);
        cd.setCorpOnCloud(false);
        cd.setFreeSize(0L);
        cd.recalcFreeSize();

        if (companyDiskDao.insert(cd) > 0)
            return ApiResult.success(null, cd);

        return ApiResult.failed(null, null);
    }

    /**
     * 初始化组织空间
     */
    @Override
    public ApiResult initDisk(String companyId) {
        if (companyDiskDao.selectByPrimaryKey(companyId) == null)
            return initOne(companyId);

        return ApiResult.failed(null, null);
    }

    /**
     * 初始化所有组织空间
     */
    @Override
    public ApiResult initAllDisk() {
        List<String> ids = companyDiskDao.listUnInitCompanyId();
        List<CompanyDiskDO> cds = Lists.newArrayList();
        if (ids != null && ids.size() > 0) {
            //只添加未初始化的公司
            for (String id : ids) {
                ApiResult ar = initDisk(id);
                if (ar.isSuccessful())
                    cds.add((CompanyDiskDO) ar.getData());
            }
        }

        return ApiResult.success(null, cds);
    }

    /**
     * 当添加文件时重新计算容量
     */
    @Override
    public ApiResult recalcSizeOnAddFile(String companyId, Long fileSize) {
        ApiResult ar = ApiResult.failed(null, null);
        int maxRetryTimes = 3;
        for (int i = 0; i < maxRetryTimes; i++) {
            ar = recalcSize(companyId, fileSize, true);
            if (ar.isSuccessful())
                i = maxRetryTimes;
        }
        return ar;
    }

    /**
     * 当删除文件时重新计算容量
     */
    @Override
    public ApiResult recalcSizeOnRemoveFile(String companyId, Long fileSize) {
        ApiResult ar = ApiResult.failed(null, null);
        int maxRetryTimes = 3;
        for (int i = 0; i < maxRetryTimes; i++) {
            ar = recalcSize(companyId, fileSize, false);
            if (ar.isSuccessful())
                i = maxRetryTimes;
        }
        return ar;
    }


    /**
     * 重新计算容量
     */
    private ApiResult recalcSize(String companyId, Long fileSize, Boolean isAdd) {
        CompanyDiskDO cd = companyDiskDao.selectByPrimaryKey(companyId);
        if (cd == null) {
            //如果找不到该公司网盘就初始化
            ApiResult ar = initOne(companyId);
            if (!ar.isSuccessful())
                return ar;
            cd = (CompanyDiskDO) ar.getData();
        }
        //更新网盘容量
        if (isAdd)
            cd.setDocmgrSize(cd.getDocmgrSize() + fileSize);
        else
            cd.setDocmgrSize(cd.getDocmgrSize() - fileSize);
        cd.recalcFreeSize();
        cd.resetUpdateDate();
        if (companyDiskDao.updateWithOptimisticLock(cd) > 0)
            return ApiResult.success(null, cd);
        log.error("尝试更新网盘信息失败（recalcSize）");
        return ApiResult.failed(null, cd);
    }



    /**
     * 根据组织ID统计文档库大小
     */
    @Override
    public ApiResult sumDocmgrSizeByCompanyId(String companyId) {
        Long size=companyDiskDao.sumDocmgrSizeByCompanyId(companyId);
        String sizeDescription= StringUtils.getSize(size);
        return ApiResult.success(sizeDescription,size);
    }

    /**
     * 根据组织ID获取公司网盘容量信息
     */
    @Override
    public ApiResult getCompanyDiskInfo(String companyId) {
        if(com.mysql.jdbc.StringUtils.isNullOrEmpty(companyId))
            return ApiResult.failed("未指定组织ID",null);

        Example example=new Example(CompanyDiskDO.class);
        example.createCriteria().andCondition("company_id = ",companyId);
        List<CompanyDiskDO> companyDiskDos = companyDiskDao.selectByExample(example);
        if(companyDiskDos.size()==0)
            return ApiResult.failed("找不到指定组织的网盘信息",null);

        return ApiResult.success("获取获取公司网盘容量信息成功！",companyDiskDos.get(0));
    }

    /**
     * 根据组织ID更新协同占用空间
     */
    @Override
    public ApiResult updateCorpSizeOnCompanyDisk(String companyId, Long corpSize) {
        if(com.mysql.jdbc.StringUtils.isNullOrEmpty(companyId))
            return ApiResult.failed("未指定组织ID",null);

        Example example=new Example(CompanyDiskDO.class);
        example.createCriteria().andCondition("company_id = ",companyId);
        List<CompanyDiskDO> companyDiskDos = companyDiskDao.selectByExample(example);
        if(companyDiskDos.size()==0)
            return ApiResult.failed("找不到指定组织的网盘信息",null);

        CompanyDiskDO cd=companyDiskDos.get(0);
        cd.setCorpSize(corpSize);
        cd.recalcFreeSize();
        return ApiResult.success(null,cd);
    }
}
