package com.maoding.filecenterbll.module.file.service;

import com.maoding.core.bean.ApiResult;
import com.maoding.core.exception.DataNotFoundException;
import com.maoding.filecenterbll.module.file.dto.DeleteDTO;
import com.maoding.filecenterbll.module.file.dto.DirectoryDTO;
import com.maoding.filecenterbll.module.file.dto.NetFileDTO;
import com.maoding.filecenterbll.module.file.dto.RenameDTO;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Wuwq on 2017/05/27.
 */
public interface NetFileService {

    /**
     * 创建目录
     */
    ApiResult createDirectory(DirectoryDTO dir) throws DataNotFoundException;

    /**
     * 上传文件
     */
    ApiResult uploadFile(HttpServletRequest request) throws Exception;

    /**
     * 重命名文件
     */
    ApiResult rename(RenameDTO dto);

    /**
     * 删除文件
     */
    ApiResult delete(DeleteDTO dto);
}
