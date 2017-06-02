package com.maoding.core.bean;

import com.maoding.utils.StringUtils;

import java.util.HashMap;

/**
 * Created by Wuwq on 2016/12/15.
 */
public class ApiRequest {
    private Integer pageIndex;
    private Integer pageSize;
    private Integer pageStartIndex;
    private String orderBy;
    private HashMap<String, Object> params = new HashMap<>();

    public HashMap<String, Object> getParams() {
        return params;
    }

    public void setParams(HashMap<String, Object> params) {
        params.putAll(params);
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        resetPage(this.pageIndex, this.pageSize, orderBy);
    }

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        resetPage(pageIndex, this.pageSize, this.orderBy);
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        resetPage(this.pageIndex, pageSize, this.orderBy);
    }

    public void addParam(String key, Object value) {
        params.put(key, value);
    }

    public Integer getPageStartIndex() {
        return pageStartIndex;
    }

    /**
     * 重置分页
     */
    public void resetPage(Integer pageIndex, Integer pageSize, String orderBy) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.orderBy = orderBy;

        if (pageIndex != null && pageSize != null) {
            this.pageIndex = pageIndex;
            this.pageSize = pageSize;
            this.pageStartIndex = pageIndex * pageSize;
            params.put("pageIndex", pageIndex);
            params.put("pageSize", pageSize);
            params.put("pageStartIndex", this.pageStartIndex);
        } else {
            this.pageStartIndex = null;
            params.put("pageStartIndex", null);
        }

        if (StringUtils.isNotBlank(orderBy))
            params.put("orderBy", orderBy.trim());
        else {
            this.orderBy = null;
            params.put("orderBy", null);
        }
    }
}
