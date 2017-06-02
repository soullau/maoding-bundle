package com.maoding.notify.endpoint.verticle;

import com.maoding.notify.endpoint.config.ActiveMQConfig;
import com.maoding.notify.endpoint.config.AppConfig;
import com.maoding.notify.endpoint.module.message.dto.MessageDto;
import com.maoding.notify.endpoint.utils.JsonUtils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.connection.SingleConnectionFactory;
import org.springframework.stereotype.Component;

import javax.jms.*;
import java.util.List;
import java.util.Map;

/**
 * Created by Wuwq on 2016/10/14.
 */
@Component
public class ActiveMQVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(ActiveMQVerticle.class);

    @Autowired
    private ActiveMQConfig activeMQConfig;

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private SingleConnectionFactory connectionFactory;

    /*创建 ActiveMq 监听*/
    private void createActiveMQConsumer() {
        try {
            Connection connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(activeMQConfig.getListenDestination());
            MessageConsumer consumer = session.createConsumer(destination);
            consumer.setMessageListener(msg -> {
                try {
                    MessageDto m = getMessage(msg);
                    String url;
                    switch (m.getMessageType()) {
                        case "userMessage":
                            url = appConfig.getClientSubUrl_TopicSpec() + "." + m.getReceiver();
                            vertx.eventBus().publish(url, new JsonObject(JsonUtils.obj2json(m)));
                            break;
                        case "notice":
                            List<String> receiverList = m.getReceiverList();
                            for (String r : receiverList) {
                                url = appConfig.getClientSubUrl_TopicSpec() + "." + r;
                                vertx.eventBus().publish(url, new JsonObject(JsonUtils.obj2json(m)));
                            }
                            break;
                    }


                    /*vertx.eventBus().send(url, new JsonObject(JsonUtils.obj2json(m)), ar -> {
                        if (ar.succeeded()) {
                            logger.debug("ActiveMQ Send Ok");
                        } else {
                            logger.debug("ActiveMQ Send Failed");
                        }
                    });*/

                } catch (JMSException e) {
                    logger.error("ActiveMQ Received Exception:{}", e);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            logger.error("ActiveMQ Connection Exception:{}", e);
        }
    }


    private MessageDto getMessage(Message msg) throws JMSException {
        Map<String, Object> map = (Map<String, Object>) ((MapMessage) msg).getObject("messageEntity");
        logger.debug("ActiveMQ Receive:" + map);
        MessageDto m = new MessageDto();
        String messageType = (String) map.getOrDefault("messageType", null);
        m.setMessageType(messageType);

        switch (messageType) {
            case "userMessage":
                m.setReceiver((String) map.getOrDefault("userId", null));
                m.setContent((String) map.getOrDefault("userName", null));
                break;
            case "notice":
                m.setContent((String) map.getOrDefault("noticeTitle", null));
                m.setReceiverList((List<String>) map.getOrDefault("receiverList", null));
                break;
        }

        return m;
    }

    @Override
    public void start() throws Exception {
        createActiveMQConsumer();
    }
}
