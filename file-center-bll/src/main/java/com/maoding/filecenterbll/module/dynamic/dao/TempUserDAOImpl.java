package com.maoding.filecenterbll.module.dynamic.dao;

import com.maoding.filecenterbll.module.dynamic.dto.TempUserDTO;
import org.springframework.stereotype.Service;

/**
 * Created by Chengliang.zhang on 2017/6/5.
 */
@Service("tempUserDAOImpl")
public class TempUserDAOImpl implements TempUserDAO{
    @Override
    public TempUserDTO getUser(String userId) {
        return null;
    }

    @Override
    public TempUserDTO getUser(String companyId, String userId) {
        return null;
    }

    @Override
    public TempUserDTO getUserByCompanyUserId(String companyUserId) {
        return null;
    }
}
