package com.maoding.admin.module.orgAuth.service;

import com.maoding.admin.module.orgAuth.dao.CommonDAO;
import com.maoding.admin.module.orgAuth.dao.OrgAuthAuditDAO;
import com.maoding.admin.module.orgAuth.dao.OrgAuthDAO;
import com.maoding.admin.module.orgAuth.dto.*;
import com.maoding.admin.module.orgAuth.model.OrgAuthAuditDO;
import com.maoding.admin.module.orgAuth.model.OrgAuthDO;
import com.maoding.admin.module.orgAuth.model.ProjectSkyDriveEntity;
import com.maoding.constDefine.netFile.NetFileType;
import com.maoding.core.base.BaseService;
import com.maoding.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wuwq on 2017/07/11.
 */
@Service("orgAuthService")
public class OrgAuthServiceImpl extends BaseService implements OrgAuthService {

    protected String fastdfsUrl = "http://172.16.6.73/";

    @Autowired
    private OrgAuthDAO orgAuthDAO;

    @Autowired
    private OrgAuthAuditDAO orgAuthAuditDAO;

    @Autowired
    private CommonDAO commonMapper;

    /**
     * 方法：设置免费期
     * 作者：zhangchengliang
     * 日期：2017/7/11
     *
     * @param orgId 组织ID
     * @param expiryDate 免费到期日期
     */
    @Override
    public void setExpiryDate(String orgId, LocalDateTime expiryDate) {
        setExpiryDate(orgId,expiryDate,null);
    }

    /**
     * 方法：设置免费期
     * 作者：zhangchengliang
     * 日期：2017/7/11
     *
     * @param orgId 组织ID
     * @param expiryDate 免费到期日期
     * @param operatorUserId 操作者ID
     */
    @Override
    public void setExpiryDate(String orgId, LocalDateTime expiryDate, String operatorUserId) {
        if ((orgId == null) || (expiryDate == null)) throw new NullPointerException("setExpiryDate 参数错误");

        OrgAuthDO entity = new OrgAuthDO();
        entity.setId(orgId);
        entity.setExpiryDate(expiryDate);
        entity.setUpdateBy(operatorUserId);
        entity.setUpdateDate(LocalDateTime.now());
        orgAuthDAO.updateByPrimaryKey(entity);
    }

    /**
     * 方法：延长免费期
     * 作者：zhangchengliang
     * 日期：2017/7/11
     *
     * @param orgId
     * @param days
     */
    @Override
    public void extendExpiryDate(String orgId, Integer days) {
        extendExpiryDate(orgId, days, null);
    }

    /**
     * 方法：延长免费期
     * 作者：zhangchengliang
     * 日期：2017/7/11
     *
     * @param orgId 组织ID
     * @param days 延期时长
     * @param operatorUserId 操作者ID
     */
    @Override
    public void extendExpiryDate(String orgId, Integer days, String operatorUserId) {
        if ((orgId == null) || (days == null)) throw new NullPointerException("extendExpiryDate 参数错误");
        OrgAuthDO entity = orgAuthDAO.selectByPrimaryKey(orgId);
        if (entity == null) throw new IllegalArgumentException ("extendExpiryDate 无法创建OrgAuthDO");

        LocalDateTime expiryDate = (entity.getExpiryDate() != null) ? entity.getExpiryDate().plusDays(days) : null;
        if ((expiryDate == null) && (entity.getApplyDate() != null)) expiryDate = entity.getApplyDate().plusDays(days);
        if ((expiryDate == null) && (entity.getCreateDate() != null)) expiryDate = entity.getCreateDate().plusDays(days);
        if (expiryDate == null) expiryDate = LocalDateTime.now().plusDays(days);
        setExpiryDate(orgId,expiryDate,operatorUserId);
    }

    /**
     * 方法：提交审核
     * 作者：zhangchengliang
     * 日期：2017/7/11
     *
     * @param authentication
     */
    @Override
    public void applyAuthentication(OrgAuthDTO authentication) {
        if (authentication == null) throw new IllegalArgumentException("applyAuthentication 参数错误");
        //更新审核记录，如果在审核表内没有找到记录创建一条
        OrgAuthDO entity = (authentication.getId() != null) ? orgAuthDAO.selectByPrimaryKey(authentication.getId()) : null;
        Boolean isNew = false;
        if (entity == null){
            entity = new OrgAuthDO();
            isNew = true;
        }
        BeanUtils.copyProperties(authentication,entity);
        entity.setApplyDate(LocalDateTime.now());
        entity.setAuthenticationStatus(2);
        entity.setRejectReason("");

        if (isNew){
            if (entity.getId() == null) entity.resetId();
            entity.setCreateDate(entity.getApplyDate());
            orgAuthDAO.insert(entity);
        } else {
            entity.setUpdateDate(entity.getApplyDate());
            orgAuthDAO.updateByPrimaryKey(entity);
        }
    }

    /**
     * 方法：处理审核
     * 作者：zhangchengliang
     * 日期：2017/7/11
     *
     * @param authorizeResult 审核结果
     */
    @Override
    public OrgAuthDTO authorizeAuthentication(OrgAuthAuditDTO authorizeResult) {
        if ((authorizeResult == null) || (authorizeResult.getOrgId() == null))
            throw new IllegalArgumentException("authorizeAuthentication 参数错误");

        if ((authorizeResult.getStatus() != 1) && (authorizeResult.getRejectType() == null)) throw new IllegalArgumentException("不通过审核原因不能为空");

        //保存当次审核结果
        OrgAuthDO origin = orgAuthDAO.selectByPrimaryKey(authorizeResult.getOrgId());
        if (origin == null) throw new IllegalArgumentException("authorizeAuthentication 参数错误");
        OrgAuthDO entity = new OrgAuthDO();
        BeanUtils.copyProperties(origin,entity);
        BeanUtils.copyProperties(authorizeResult,entity);
        if ((origin.getAuthenticationStatus() != 1) && (entity.getAuthenticationStatus() == 1)){ //首次通过认证
            final Integer EXPIRY_DAYS = 30;
            LocalDateTime expiryDate = (origin.getExpiryDate() != null) ? origin.getExpiryDate().plusDays(EXPIRY_DAYS) : null;
            if ((expiryDate == null) && (origin.getApplyDate() != null)) expiryDate = origin.getApplyDate().plusDays(EXPIRY_DAYS);
            if ((expiryDate == null) && (origin.getCreateDate() != null)) expiryDate = origin.getCreateDate().plusDays(EXPIRY_DAYS);
            if (expiryDate == null) expiryDate = LocalDateTime.now().plusDays(EXPIRY_DAYS);
            entity.setExpiryDate(expiryDate);
        }

        entity.resetUpdateDate();
        orgAuthDAO.updateByPrimaryKey(entity);

        //更新认证信息历史
        orgAuthAuditDAO.updateStatusByOrgId(authorizeResult.getOrgId()); //更新以往数据的isNew字段，设置为0

        OrgAuthAuditDO auditDO = new OrgAuthAuditDO();
        BeanUtils.copyProperties(authorizeResult,auditDO);
        if (entity.getUpdateDate() != null) {
            auditDO.setApproveDate(entity.getUpdateDate());
        }
        auditDO.setAuditMessage(authorizeResult.getRejectType().toString());
        auditDO.setIsNew(1);
        if (entity.getApplyDate() != null) {
            auditDO.setSubmitDate(entity.getApplyDate());
        }
        auditDO.setCreateDate(entity.getUpdateDate());
        auditDO.setCreateBy(entity.getUpdateBy());
        auditDO.resetUpdateDate();
        if (auditDO.getId() == null) auditDO.resetId();
        orgAuthAuditDAO.insert(auditDO);

        OrgAuthDataDTO data = orgAuthDAO.getOrgAuthenticationInfo(entity.getId());
        return createAuthenticationByEntity(data);
    }

    /**
     * 方法：列出申请审核记录
     * 作者：zhangchengliang
     * 日期：2017/7/11
     *
     * @param query 查询过滤条件
     */
    @Override
    public List<OrgAuthDTO> listAuthentication(OrgAuthQueryDTO query) {
        if (query == null) throw new IllegalArgumentException("listAuthentication 参数错误");
        List<OrgAuthDataDTO> dataList = orgAuthDAO.listOrgAuthenticationInfo(query);
        List<OrgAuthDTO> dtoList = new ArrayList<>();
        for (OrgAuthDataDTO data : dataList) {
            OrgAuthDTO dto = createAuthenticationByEntity(data);
            dtoList.add(dto);
        }
        return dtoList;
    }

    @Override
    public OrgAuthPageDTO getAuthenticationPage(OrgAuthQueryDTO query) {
        if (query == null) throw new IllegalArgumentException("getAuthenticationPage 参数错误");
        OrgAuthPageDTO result = new OrgAuthPageDTO();
        List<OrgAuthDTO> list = listAuthentication(query);
        result.setTotal(commonMapper.getLastQueryCount());
        if (result.getTotal() > 0) {
            result.setList(list);
        }
        return result;
    }

    /**
     * 方法：获取注册信息
     * 作者：zhangchengliang
     * 日期：2017/7/11
     *
     * @param orgId
     */
    @Override
    public OrgAuthDTO getAuthenticationById(String orgId) {
        if (orgId == null) throw new IllegalArgumentException("getAuthenticationById 参数错误");
        OrgAuthDataDTO dto = orgAuthDAO.getOrgAuthenticationInfo(orgId);
        return createAuthenticationByEntity(dto);
    }

    private OrgAuthDTO createAuthenticationByEntity(OrgAuthDataDTO data){
        if (data == null) throw new IllegalArgumentException("createAuthenticationByEntity 参数错误");
        OrgAuthDTO authentication = new OrgAuthDTO();
        BeanUtils.copyProperties(data,authentication);
        List<ProjectSkyDriveEntity> fileList = data.getAttachList();
        //复制附件文件名
        for (ProjectSkyDriveEntity file : fileList) {
            if (file.getType() == NetFileType.CERTIFICATE_ATTACH) {
                authentication.setSealPhoto(getFilePath(file));
            } else if (file.getType() == NetFileType.BUSINESS_LICENSE_ATTACH) {
                authentication.setBusinessLicensePhoto(getFilePath(file));
            } else if (file.getType() == NetFileType.LEGAL_REPRESENTATIVE_ATTACH){
                authentication.setLegalRepresentativePhoto(getFilePath(file));
            } else if (file.getType() == NetFileType.OPERATOR_ATTACH) {
                authentication.setOperatorPhoto(getFilePath(file));
            }
        }
        return authentication;
    }

    private String getFilePath(ProjectSkyDriveEntity entity){
        return this.fastdfsUrl + entity.getFileGroup() + "/" + entity.getFilePath();
    }
}
