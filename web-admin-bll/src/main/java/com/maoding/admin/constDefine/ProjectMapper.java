package com.maoding.admin.constDefine;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chengliang.zhang on 2017/7/19.
 */
public interface ProjectMapper {
    final String PROJECT_NO = "projectNo";
    final String PROJECT_NAME = "projectName";
    final String PROJECT_CITY = "city";
    final String PROJECT_COUNTY = "county";
    final String PROJECT_DETAIL_ADDRESS = "detailAddress";
    final String PROJECT_STATUS = "status";
    final String PROJECT_BID = "companyBid";

    final String PROJECT_STATUS_NO_FINISHED = "0";
    final String PROJECT_STATUS_FINISHED = "1";

    /** 状态转换 */
    final Map<String,String> STATUS_MAPPER = new HashMap<String,String>(){
        {
            put("已完成",PROJECT_STATUS_FINISHED);
            put("进行中",PROJECT_STATUS_NO_FINISHED);
        }
    };

    final Map<String,String> TITLE_MAPPER = new HashMap<String,String>(){
        {
            put("项目编号*",PROJECT_NO);
            put("项目名称*",PROJECT_NAME);
            put("省",PROJECT_CITY);
            put("市",PROJECT_COUNTY);
            put("详细地址",PROJECT_DETAIL_ADDRESS);
            put("项目状态",PROJECT_STATUS);
            put("乙方",PROJECT_BID);
        }
    };
}
