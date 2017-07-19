package com.maoding.core.bean;


import com.maoding.core.base.BaseRequest;

/**
 * Created by Chengliang.zhang on 2017/7/18.
 * 请求添加数据时用到的参数类型
 */
public final class ApiRequestInsert<T> extends BaseRequest {
    /** 要添加的数据 */
    T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
