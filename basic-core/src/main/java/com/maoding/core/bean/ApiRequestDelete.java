package com.maoding.core.bean;


import com.maoding.core.base.BaseQueryDTO;
import com.maoding.core.base.BaseRequest;

/**
 * Created by Chengliang.zhang on 2017/7/18.
 * 请求删除数据时用到的参数类型
 */
public final class ApiRequestDelete<Q extends BaseQueryDTO> extends BaseRequest {
    /** 要删除的数据的查询条件 */
    Q query;

    public Q getQuery() {
        return query;
    }

    public void setQuery(Q query) {
        this.query = query;
    }
}
