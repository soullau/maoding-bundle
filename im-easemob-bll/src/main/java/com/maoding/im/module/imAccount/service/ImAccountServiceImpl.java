package com.maoding.im.module.imAccount.service;

import com.maoding.core.base.BaseService;
import com.maoding.core.bean.ApiResult;
import com.maoding.im.easemob.api.ImUserApi;
import com.maoding.im.easemob.comm.ExUser;
import com.maoding.im.module.imAccount.dto.ImAccountDTO;
import io.swagger.client.model.NewPassword;
import io.swagger.client.model.Nickname;
import io.swagger.client.model.RegisterUsers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("imAccountService")
public class ImAccountServiceImpl extends BaseService implements ImAccountService {
    private static final Logger logger = LoggerFactory.getLogger(ImAccountServiceImpl.class);

    @Autowired
    private ImUserApi imUserApi;

    /** 创建环信账号 **/
    @Override
    public ApiResult create(ImAccountDTO dto) {
        RegisterUsers users = new RegisterUsers();
        ExUser user = new ExUser().username(dto.getAccountId()).password(dto.getPassword()).nickname(dto.getAccountName());
        users.add(user);
        return imUserApi.createNewIMUserSingle(users);
    }

    /** 批量创建环信账号 **/
    @Override
    public ApiResult createBatch(List<ImAccountDTO> list) {
        RegisterUsers users = new RegisterUsers();
        list.forEach(o -> {
            ExUser user = new ExUser().username(o.getAccountId()).password(o.getPassword()).nickname(o.getAccountName());
            users.add(user);
        });
        return imUserApi.createNewIMUserBatch(users);
    }

    /** 修改环信账号昵称 **/
    @Override
    public ApiResult modifyNickname(ImAccountDTO dto) {
        Nickname nickname = new Nickname().nickname(dto.getAccountName());
        return imUserApi.modifyIMUserNickNameWithAdminToken(dto.getAccountId(), nickname);
    }

    /** 重置环信账号密码 **/
    @Override
    public ApiResult modifyPassword(ImAccountDTO dto) {
        NewPassword password = new NewPassword().newpassword(dto.getPassword());
        return imUserApi.modifyIMUserPasswordWithAdminToken(dto.getAccountId(), password);
    }

    /** 删除环信账号 **/
    @Override
    public ApiResult delete(String accountId) {
        return imUserApi.deleteIMUserByUserName(accountId);
    }
}
