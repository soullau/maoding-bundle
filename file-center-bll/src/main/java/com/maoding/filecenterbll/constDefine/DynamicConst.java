package com.maoding.filecenterbll.constDefine;

/**
 * Created by Chengliang.zhang on 2017/6/5.
 */
public interface DynamicConst {
    /********* 文件类动态类型 **********/
    Integer DYNAMIC_TYPE_UPLOAD_FILE = 30;//上传文件
    Integer DYNAMIC_TYPE_UPDATE_FILE = 31;//修改文件
    Integer DYNAMIC_TYPE_DELETE_FILE = 32;//删除文件
    Integer DYNAMIC_TYPE_CREATE_DIRECTORY = 33;//创建目录

    /******* 被操作的信息类型 **********/
    Integer TARGET_TYPE_SKY_DRIVE = 1; //操作网盘数据表：maoding_web_project_sky_drive

    /******* 格式相关 ***************/
    String SEPARATOR = " ;";
}
