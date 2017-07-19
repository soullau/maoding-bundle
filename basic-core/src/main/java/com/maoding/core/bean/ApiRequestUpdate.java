package com.maoding.core.bean;

import com.maoding.core.base.BaseQueryDTO;
import com.maoding.core.base.BaseRequest;

/**
 * Created by Chengliang.zhang on 2017/7/18.
 * 请求更改数据时用到的参数类型
 */
public final class ApiRequestUpdate<T, Q extends BaseQueryDTO> extends BaseRequest {
    /** 要更改到的目标数据，字段为null则不更改 */
    T data;
    /** 要更改的数据的查询条件 */
    Q query;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Q getQuery() {
        return query;
    }

    public void setQuery(Q query) {
        this.query = query;
    }
}
