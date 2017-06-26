package com.maoding.common.module.dynamic.dao;

import com.maoding.common.module.dynamic.dto.ZUserDTO;
import org.apache.ibatis.annotations.Param;

/**
 * Created by Chengliang.zhang on 2017/6/5.
 * 用于查找用户名和用户雇员ID，临时使用，最终应放入用户管理服务模块中
 */
public interface ZUserDAO {
    ZUserDTO getUserById(String userId);
    ZUserDTO getUserByCompanyIdAndUserId(@Param("companyId") String companyId, @Param("userId") String userId);
    ZUserDTO getUserByCompanyUserId(String companyUserId);
}
