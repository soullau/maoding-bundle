package com.maoding.admin.constDefine;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chengliang.zhang on 2017/7/19.
 */
public interface ProjectMapper {
    final String PROJECT_NO = "项目编号*";
    final String PROJECT_NAME = "项目名称*";
    final String PROJECT_COMPANY_NAME = "立项组织*";
    final String PROJECT_CREATOR_NAME = "立项人*";
    final String PROJECT_CONTRACT_DATE = "立项日期*";
    final String PROJECT_PROVINCE = "省/直辖市";
    final String PROJECT_CITY = "市/直辖市区";
    final String PROJECT_COUNTY = "区/县";
    final String PROJECT_DETAIL_ADDRESS = "详细地址";
    final String PROJECT_STATUS = "项目状态";
    final String PROJECT_A_NAME = "甲方";
    final String PROJECT_B_NAME = "乙方";

    final String PROJECT_STATUS_NO_FINISHED = "0";
    final String PROJECT_STATUS_FINISHED = "1";

    /** 状态转换 */
    final Map<String,String> STATUS_MAPPER = new HashMap<String,String>(){
        {
            put("已完成",PROJECT_STATUS_FINISHED);
            put("进行中",PROJECT_STATUS_NO_FINISHED);
        }
    };
}
