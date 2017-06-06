package com.maoding.filecenterbll.module.dynamic.model;

import com.maoding.core.base.BaseEntity;

/**
 * Created by Chengliang.zhang on 2017/6/2.
 * 作为预备升级的动态表记录，目前没有对应的表
 */
public class DynamicDO {
    /**
     * 操作者用户编号
     */
    private String userId;
    /**
     * 操作者职责编号
     */
    private String companyUserId;
    /**
     * 项目编号
     */
    private String projectId;
    /**
     * 公司编号
     */
    private String companyId;
    /**
     * 被操作的信息类型
     */
    private Integer targetType;
    /**
     * 被操作的信息编号
     */
    private String targetId;
    /**
     * 被操作的信息的名称
     */
    private String nodeName;
    /**
     * 动态类型编号
     */
    private Integer type;
    /**
     * 操作信息描述
     */
    private String content;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCompanyUserId() {
        return companyUserId;
    }

    public void setCompanyUserId(String companyUserId) {
        this.companyUserId = companyUserId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public Integer getTargetType() {
        return targetType;
    }

    public void setTargetType(Integer targetType) {
        this.targetType = targetType;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
