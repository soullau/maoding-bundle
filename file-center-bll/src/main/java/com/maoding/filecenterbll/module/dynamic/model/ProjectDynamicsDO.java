package com.maoding.filecenterbll.module.dynamic.model;

import com.maoding.core.base.BaseEntity;

import javax.persistence.Table;

/**
 * Created by Chengliang.zhang on 2017/6/5.
 * 原ProjectDynamicsEntity, 维持与原有数据表相同结构，保持兼容性
 */
@Table(name = "maoding_web_project_dynamics")
public class ProjectDynamicsDO extends BaseEntity {

    private String title;

    private String projectId;

    private int status;

    private String companyId;

    private int type;

    private String companyUserId;

    private String field1;

    private String content;

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId == null ? null : projectId.trim();
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId == null ? null : companyId.trim();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public String getCompanyUserId() {
        return companyUserId;
    }

    public void setCompanyUserId(String companyUserId) {
        this.companyUserId = companyUserId;
    }

    public String getField1() {
        return field1;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
