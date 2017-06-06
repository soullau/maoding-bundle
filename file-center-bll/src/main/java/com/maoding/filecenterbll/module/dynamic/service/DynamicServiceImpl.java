package com.maoding.filecenterbll.module.dynamic.service;

import com.maoding.core.base.BaseService;
import com.maoding.filecenterbll.constDefine.DynamicConst;
import com.maoding.filecenterbll.constDefine.NetFileType;
import com.maoding.filecenterbll.module.dynamic.dao.DynamicDAO;
import com.maoding.filecenterbll.module.dynamic.model.DynamicDO;
import com.maoding.filecenterbll.module.dynamic.model.ProjectDynamicsDO;
import com.maoding.filecenterbll.module.file.model.NetFileDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by Chengliang.zhang on 2017/6/5.
 */
@Service("dynamicService")
public class DynamicServiceImpl extends BaseService implements DynamicService{
    @Autowired
    private DynamicDAO dynamicDAO;

    /**
     * 记录项目动态
     *
     * @param dynamic 要添加的项目动态
     */
    @Override
    public void addDynamic(DynamicDO dynamic) {
        ProjectDynamicsDO entity = createProjectDynamicsFrom(dynamic);
        dynamicDAO.insert(entity);
    }

    /**
     * 生成创建对象类项目动态
     */
    @Override
    public DynamicDO createDynamicFrom(NetFileDO dto) {
        DynamicDO dynamic = new DynamicDO();
        dynamic.setUserId(dto.getCreateBy());
        dynamic.setCompanyUserId(getCompanyUserId(dto.getCompanyId(),dto.getCreateBy()));
        dynamic.setCompanyId(dto.getCompanyId());
        dynamic.setCompanyId(dto.getProjectId());
        dynamic.setTargetType(DynamicConst.TARGET_TYPE_SKY_DRIVE);
        dynamic.setTargetId(dto.getId());
        dynamic.setNodeName(dto.getFileName());
        dynamic.setType((dto.getType() == NetFileType.FILE) ?
                DynamicConst.DYNAMIC_TYPE_UPLOAD_FILE :
                DynamicConst.DYNAMIC_TYPE_CREATE_DIRECTORY);
        dynamic.setContent(dto.getFileName());
        return dynamic;
    }

    private String getCompanyUserId(String companyId, String userId){
        return null;
    }

    /**
     * 生成更改对象类项目动态
     */
    @Override
    public DynamicDO createDynamicFrom(NetFileDO dtoNew, NetFileDO dtoOld) {
        DynamicDO dynamic = new DynamicDO();
        dynamic.setUserId(dtoNew.getCreateBy());
        String cid = (dtoNew.getCompanyId() != null) ? dtoNew.getCompanyId() : dtoOld.getCompanyId();
        String pid = (dtoNew.getProjectId() != null) ? dtoNew.getProjectId() : dtoOld.getProjectId();
        dynamic.setCompanyUserId(getCompanyUserId(cid,dtoNew.getCreateBy()));
        dynamic.setCompanyId(cid);
        dynamic.setCompanyId(pid);
        dynamic.setTargetType(DynamicConst.TARGET_TYPE_SKY_DRIVE);
        dynamic.setTargetId(dtoNew.getId());
        dynamic.setNodeName(dtoOld.getFileName());
        dynamic.setType(DynamicConst.DYNAMIC_TYPE_UPDATE_FILE);
        dynamic.setContent(dtoOld.getFileName() + DynamicConst.SEPARATOR + dtoNew.getFileName());
        return dynamic;
    }
    /**
     * 生成删除对象类项目动态
     */
    @Override
    public DynamicDO createDynamicFrom(NetFileDO dto, String companyUserId, String userId) {
        DynamicDO dynamic = new DynamicDO();
        dynamic.setUserId(userId);
        dynamic.setCompanyUserId(companyUserId);
        dynamic.setCompanyId(dto.getCompanyId());
        dynamic.setCompanyId(dto.getProjectId());
        dynamic.setTargetType(DynamicConst.TARGET_TYPE_SKY_DRIVE);
        dynamic.setTargetId(dto.getId());
        dynamic.setNodeName(dto.getFileName());
        dynamic.setType(DynamicConst.DYNAMIC_TYPE_DELETE_FILE);
        dynamic.setContent(dto.getFileName());
        return dynamic;
    }

    /**
     * 转换新表设计为原有设计（保持兼容性）
     *
     * @param data 要转换的数据
     */
    private ProjectDynamicsDO createProjectDynamicsFrom(DynamicDO data) {
        ProjectDynamicsDO entity = new ProjectDynamicsDO();
        entity.initEntity();
        entity.setCompanyId(data.getCompanyId());
        entity.setProjectId(data.getProjectId());
        entity.setStatus(0);
        entity.setType(data.getType());
        entity.setCompanyUserId((data.getCompanyUserId()!=null) ? data.getCompanyUserId() : getCompanyUserId(data.getCompanyId(),data.getUserId()));
        entity.setCreateBy(data.getUserId());
        String content = getUserName(entity.getCompanyUserId(),data.getUserId());
        content += DynamicConst.SEPARATOR + data.getNodeName();
        content += DynamicConst.SEPARATOR + getDynamicTypeName(data.getType());
        content += DynamicConst.SEPARATOR + ((data.getContent() != null) ? data.getContent() : "");
        content += DynamicConst.SEPARATOR + DynamicConst.SEPARATOR + DynamicConst.SEPARATOR +  data.getType().toString();
        entity.setContent(content);
        return entity;
    }

    private String getUserName(String companyUserId,String userId){
        return "";
    }
    private String getDynamicTypeName(Integer type){
        return "";
    }
}
