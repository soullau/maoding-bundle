package com.maoding.corpbll.module.corpserver.model;

import com.maoding.core.base.BaseEntity;

import javax.persistence.Table;
import java.math.BigInteger;

/**
 * Created by Wuwq on 2017/2/9.
 * 组织磁盘空间
 */
@Table(name = "maoding_corp_company_disk")
public class CompanyDiskEntity extends BaseEntity {

    /**
     * 组织Id
     */
    private String companyId;
    /**
     * 总容量
     */
    private BigInteger totalSize;
    /**
     * 是否云端协同
     */
    private Boolean corpOnCloud;
    /**
     * 协同占用容量
     */
    private BigInteger corpSize;
    /**
     * 文档库占用容量
     */
    private BigInteger docmgrSize;
    /**
     * 剩余容量
     */
    private BigInteger freeSize;
    /**
     * 版本控制(乐观锁）
     */
    private BigInteger upVersion;

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public BigInteger getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(BigInteger totalSize) {
        this.totalSize = totalSize;
    }

    public Boolean getCorpOnCloud() {
        return corpOnCloud;
    }

    public void setCorpOnCloud(Boolean corpOnCloud) {
        this.corpOnCloud = corpOnCloud;
    }

    public BigInteger getCorpSize() {
        return corpSize;
    }

    public void setCorpSize(BigInteger corpSize) {
        this.corpSize = corpSize;
    }

    public BigInteger getDocmgrSize() {
        return docmgrSize;
    }

    public void setDocmgrSize(BigInteger docmgrSize) {
        this.docmgrSize = docmgrSize;
    }

    public BigInteger getFreeSize() {
        return freeSize;
    }

    public void setFreeSize(BigInteger freeSize) {
        this.freeSize = freeSize;
    }

    public BigInteger getUpVersion() {
        return upVersion;
    }

    public void setUpVersion(BigInteger upVersion) {
        this.upVersion = upVersion;
    }
}
