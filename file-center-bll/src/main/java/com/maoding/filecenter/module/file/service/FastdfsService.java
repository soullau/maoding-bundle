package com.maoding.filecenter.module.file.service;

import com.maoding.core.bean.ApiResult;
import com.maoding.core.bean.FastdfsUploadResult;
import com.maoding.core.bean.MultipartFileParam;

import java.io.IOException;

/**
 * Created by Wuwq on 2017/06/01.
 */
public interface FastdfsService {
    /**
     * 上传文件
     */
    FastdfsUploadResult upload(MultipartFileParam param) throws IOException;

    /**
     * 上传LOGO
     */
    FastdfsUploadResult uploadLogo(MultipartFileParam param) throws Exception;

    /**
     * 从 FastDFS 删除文件
     */
    ApiResult delete(String group, String path);
}
