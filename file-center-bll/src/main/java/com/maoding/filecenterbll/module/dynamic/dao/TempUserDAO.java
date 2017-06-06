package com.maoding.filecenterbll.module.dynamic.dao;

import com.maoding.filecenterbll.module.dynamic.dto.TempUserDTO;

/**
 * Created by Chengliang.zhang on 2017/6/5.
 */
public interface TempUserDAO {
    TempUserDTO getUser(String userId);
    TempUserDTO getUser(String companyId, String userId);
    TempUserDTO getUserByCompanyUserId(String companyUserId);
}
