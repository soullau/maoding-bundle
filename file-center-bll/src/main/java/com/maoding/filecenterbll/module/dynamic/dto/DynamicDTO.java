package com.maoding.filecenterbll.module.dynamic.dto;

/**
 * Created by Chengliang.zhang on 2017/6/5.
 */
public class DynamicDTO {
    /**
     * 动态编号
     */
    private String id;
    /**
     * 操作者用户编号
     */
    private String userId;
    /**
     * 操作者职责编号
     */
    private String companyUserId;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 操作节点名
     */
    private String nodeName;
    /**
     * 动态类型编号
     */
    private Integer type;
    /**
     * 活动描述
     */
    private String action;
    /**
     * 活动参数描述
     */
    private String value;
    /**
     * 动态完整信息格式
     */
    private String template;
    /**
     * 动态完整信息，用于实现app兼容
     */
    private String dynamic;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getDynamic() {
        return dynamic;
    }

    public void setDynamic(String dynamic) {
        this.dynamic = dynamic;
    }
}
