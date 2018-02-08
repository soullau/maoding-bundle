package com.maoding.im.module.imGroup.dto;

public class ImGroupMemberDTO {
    private String accountId;
    private String companyUserId;

    public ImGroupMemberDTO() {

    }

    public ImGroupMemberDTO(String accountId, String companyUserId) {
        this.accountId = accountId;
        this.companyUserId = companyUserId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getCompanyUserId() {
        return companyUserId;
    }

    public void setCompanyUserId(String companyUserId) {
        this.companyUserId = companyUserId;
    }
}
