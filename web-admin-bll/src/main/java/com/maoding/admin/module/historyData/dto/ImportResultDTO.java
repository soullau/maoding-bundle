package com.maoding.admin.module.historyData.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Chengliang.zhang on 2017/7/20.
 */
public class ImportResultDTO {
    /** 总导入记录数 */
    Integer totalCount;
    /** 失败记录列表 */
    List<Map<String,Object>> failedList;

    public void addTotalCount(){
        if (totalCount == null) totalCount = 0;
        totalCount++;
    }

    public void addFailed(Map<String,Object> record){
        if (failedList == null) failedList = new ArrayList<Map<String,Object>>();
        failedList.add(record);
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getFailedCount() {
        return (failedList != null) ? failedList.size() : 0;
    }

    public List<Map<String, Object>> getFailedList() {
        return failedList;
    }

    public void setFailedList(List<Map<String, Object>> failedList) {
        this.failedList = failedList;
    }
}
