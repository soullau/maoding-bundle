package com.maoding.im.module.schedule.service;

import com.google.common.collect.Lists;
import com.maoding.im.easemob.api.SendMessageApi;
import com.maoding.im.module.schedule.dao.ScheduleDAO;
import com.maoding.im.module.schedule.dto.ScheduleDTO;
import io.swagger.client.model.Msg;
import io.swagger.client.model.MsgContent;
import io.swagger.client.model.UserName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("scheduleService")
public class ScheduleServiceImpl implements ScheduleService {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleServiceImpl.class);

    @Autowired
    private SendMessageApi sendMessageApi;

    @Autowired
    private ScheduleDAO scheduleDAO;

    private static String fromUser= "123f9ef123c140fd9b6c7a5123c68e1a";

    private static String targetType = "users";

    @Override
    public void notifySchedule() {
        List<ScheduleDTO> list = scheduleDAO.getSchedule();
        list.stream().forEach(dto->{
            Msg msg = new Msg();
            msg.from(fromUser);
            UserName userName = new UserName();
            userName.addAll(Lists.newArrayList(dto.getUserId()));
            msg.target(userName);
            msg.targetType(targetType);
            MsgContent msgContent = new MsgContent();
            String content = "";
            if(dto.getScheduleType()==1){
                content = "你将有一个日程为“"+dto.getContent()+"”,开始时间为："+dto.getStartTime();
            }else {
                content = "你将有一个会议为“"+dto.getContent()+"”,开始时间为："+dto.getStartTime();
            }
            try {
                msgContent.type(MsgContent.TypeEnum.TXT).msg(content);
                msg.setMsg(msgContent);
                sendMessageApi.sendMessage(msg);
            } catch (Exception e) {
                logger.error("handleSendMessage发生异常", e);
            }
        });
    }
}
