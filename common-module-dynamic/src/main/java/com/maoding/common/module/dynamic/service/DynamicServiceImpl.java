package com.maoding.common.module.dynamic.service;

import com.maoding.core.base.BaseService;
import com.maoding.filecenter.constDefine.DynamicConst;
import com.maoding.filecenter.constDefine.NetFileType;
import com.maoding.common.module.dynamic.dao.DynamicDAO;
import com.maoding.common.module.dynamic.dao.ZUserDAO;
import com.maoding.common.module.dynamic.dto.ZUserDTO;
import com.maoding.common.module.dynamic.model.DynamicDO;
import com.maoding.common.module.dynamic.model.ProjectDynamicsDO;
import com.maoding.filecenter.module.file.model.NetFileDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Chengliang.zhang on 2017/6/5.
 */
@Service("dynamicService")
public class DynamicServiceImpl extends BaseService implements DynamicService{
    @Autowired
    private DynamicDAO dynamicDAO;

    @Autowired
    private ZUserDAO userDAO;

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
        dynamic.setProjectId(dto.getProjectId());
        dynamic.setTargetType(DynamicConst.TARGET_TYPE_SKY_DRIVE);
        dynamic.setTargetId(dto.getId());
        dynamic.setNodeName(dto.getFileName());
        dynamic.setType((dto.getType() == NetFileType.FILE) ?
                DynamicConst.DYNAMIC_TYPE_UPLOAD_FILE :
                DynamicConst.DYNAMIC_TYPE_CREATE_DIRECTORY);
        return dynamic;
    }

    private String getCompanyUserId(String companyId, String userId){
        ZUserDTO user = userDAO.getUserByCompanyIdAndUserId(companyId,userId);
        return (user != null) ? user.getCompanyUserId() : null;
    }

    /**
     * 生成更改对象类项目动态
     */
    @Override
    public DynamicDO createDynamicFrom(NetFileDO dtoNew, NetFileDO dtoOld) {
        DynamicDO dynamic = new DynamicDO();
        String uid = dtoNew.getUpdateBy();
        String cid = dtoOld.getCompanyId();
        String pid = dtoOld.getProjectId();
        String did = getCompanyUserId(cid,uid);
        String tid = dtoNew.getId();
        dynamic.setUserId(uid);
        dynamic.setCompanyUserId(did);
        dynamic.setCompanyId(cid);
        dynamic.setProjectId(pid);
        dynamic.setTargetType(DynamicConst.TARGET_TYPE_SKY_DRIVE);
        dynamic.setTargetId(tid);
        dynamic.setNodeName(dtoOld.getFileName());
        dynamic.setType((dtoOld.getType() == NetFileType.FILE) ?
                DynamicConst.DYNAMIC_TYPE_UPDATE_FILE :
                DynamicConst.DYNAMIC_TYPE_UPDATE_DIRECTORY);
        dynamic.setContent(dtoNew.getFileName());
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
        dynamic.setProjectId(dto.getProjectId());
        dynamic.setTargetType(DynamicConst.TARGET_TYPE_SKY_DRIVE);
        dynamic.setTargetId(dto.getId());
        dynamic.setNodeName(dto.getFileName());
        dynamic.setType((dto.getType() == NetFileType.FILE) ?
                DynamicConst.DYNAMIC_TYPE_DELETE_FILE :
                DynamicConst.DYNAMIC_TYPE_DELETE_DIRECTORY);
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
        content += DynamicConst.SEPARATOR + ((data.getContent() != null) ? data.getContent() : "");
        content += DynamicConst.SEPARATOR + DynamicConst.SEPARATOR + DynamicConst.SEPARATOR +  data.getType().toString();
        entity.setContent(content);
        return entity;
    }

    private String getUserName(String companyUserId,String userId){
        String userName = "";

        if (companyUserId != null) {
            ZUserDTO user = userDAO.getUserByCompanyUserId(companyUserId);
            if (user != null) {
                userName = (user.getAliasName() != null) ? user.getAliasName() : user.getUserName();
            }
        } else if (userId != null) {
            ZUserDTO user = userDAO.getUserById(userId);
            if (user != null){
                userName = (user.getAliasName() != null) ? user.getAliasName() : user.getUserName();
            }
        }
        return userName;
    }
}
