package com.maoding.im.module.imGroup.service;

import com.google.common.collect.Lists;
import com.maoding.core.base.BaseService;
import com.maoding.core.bean.ApiResult;
import com.maoding.im.easemob.api.ChatGroupApi;
import com.maoding.im.module.imGroup.dto.ImGroupDTO;
import com.maoding.utils.StringUtils;
import io.swagger.client.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("imGroupService")
public class ImGroupServiceImpl extends BaseService implements ImGroupService {
    private static final Logger logger = LoggerFactory.getLogger(ImGroupServiceImpl.class);

    @Autowired
    private ChatGroupApi chatGroupApi;

    /** 创建环信群组 */
    @Override
    public ApiResult create(ImGroupDTO dto) {
        Group group = new Group();
        group.setGroupname(dto.getGroupName());
        group.setOwner(dto.getGroupOwner());
        group.setDesc(StringUtils.isBlank(dto.getGroupImg()) ? null : dto.getGroupImg());
        group.setPublic(true);
        group.setMaxusers(2000);
        if (dto.getMembers() != null && dto.getMembers().size() > 0) {
            List<String> ids = Lists.newArrayList();
            dto.getMembers().forEach(c -> ids.add(c.getAccountId()));
            UserName memberIds = new UserName();
            memberIds.addAll(ids);
            group.setMembers(memberIds);
        }
        //group.setApproval(true);
        return chatGroupApi.createChatGroup(group);
    }

    /** 删除群 */
    @Override
    public ApiResult delete(String groupNo) {
        return chatGroupApi.deleteChatGroup(groupNo);
    }

    /** 修改环信群组名 */
    @Override
    public ApiResult modifyGroupName(ImGroupDTO dto) {
        ModifyGroup group = new ModifyGroup();
        group.setGroupname(dto.getGroupName());
        return chatGroupApi.modifyChatGroup(dto.getGroupNo(), group);
    }

    /** 移交环信群主 */
    @Override
    public ApiResult transferGroupOwner(ImGroupDTO dto) {
        NewOwner newOwner = new NewOwner();
        newOwner.newowner(dto.getGroupOwner());
        return chatGroupApi.transferChatGroupOwner(dto.getGroupNo(), newOwner);
    }

    /** 增加群成员 */
    @Override
    public ApiResult addMembers(ImGroupDTO dto) {
        UserName memberIds = new UserName();
        List<String> ids = Lists.newArrayList();
        dto.getMembers().forEach(c -> ids.add(c.getAccountId()));
        memberIds.addAll(ids);
        UserNames userNames = new UserNames().usernames(memberIds);
        return chatGroupApi.addBatchUsersToChatGroup(dto.getGroupNo(), userNames);
    }

    /** 移除群成员 */
    @Override
    public ApiResult deleteMembers(ImGroupDTO dto) {
        if (dto.getMembers() == null || dto.getMembers().size() == 0)
            return ApiResult.success(null);

        List<String> ids = Lists.newArrayList();
        dto.getMembers().forEach(c -> ids.add(c.getAccountId()));
        String[]  memberIds = ids.toArray(new String[ids.size()]);

        return chatGroupApi.removeBatchUsersFromChatGroup(dto.getGroupNo(), memberIds);
    }
}
