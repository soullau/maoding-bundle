package com.maoding.filecenter.constDefine;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chengliang.zhang on 2017/6/5.
 */
public interface DynamicConst {
    /********* 文件类动态类型 **********/
    Integer DYNAMIC_TYPE_UPLOAD_FILE = 30;//上传文件
    Integer DYNAMIC_TYPE_UPDATE_FILE = 31;//修改文件
    Integer DYNAMIC_TYPE_DELETE_FILE = 32;//删除文件
    Integer DYNAMIC_TYPE_CREATE_DIRECTORY = 36;//创建目录
    Integer DYNAMIC_TYPE_UPDATE_DIRECTORY = 37;//修改目录
    Integer DYNAMIC_TYPE_DELETE_DIRECTORY = 38;//删除目录

    /******* 被操作的信息类型 **********/
    Integer TARGET_TYPE_SKY_DRIVE = 1; //操作网盘数据表：maoding_web_project_sky_drive

    /******* 格式相关 ***************/
    String SEPARATOR = " ;"; //分隔符

    /******* 动态类型转换 **************/
    Map<Integer,String> TYPE_STRING = new HashMap<Integer,String>(){
        {
            put(DYNAMIC_TYPE_UPLOAD_FILE,"上传文件");
            put(DYNAMIC_TYPE_UPDATE_FILE,"修改文件");
            put(DYNAMIC_TYPE_DELETE_FILE,"删除文件");
            put(DYNAMIC_TYPE_CREATE_DIRECTORY,"创建目录");
            put(DYNAMIC_TYPE_UPDATE_DIRECTORY,"修改目录");
            put(DYNAMIC_TYPE_DELETE_DIRECTORY,"删除目录");
        }
    };
}
