package com.maoding.filecenterbll.module.file.service;

import com.maoding.core.bean.ApiResult;
import com.maoding.core.bean.FastdfsUploadResult;
import com.maoding.core.bean.MultipartFileParam;

import java.io.IOException;

/**
 * Created by Wuwq on 2017/06/01.
 */
public interface FastdfsService {
    /**
     * 上传到 FastDFS
     */
    FastdfsUploadResult upload(MultipartFileParam param) throws IOException;

    /**
     * 从 FastDFS 删除文件
     */
    ApiResult delete(String group,String path);
}
