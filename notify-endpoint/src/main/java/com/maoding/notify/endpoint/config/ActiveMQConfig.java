package com.maoding.notify.endpoint.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.SingleConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

/**
 * Created by Wuwq on 2016/10/13.
 */
@Configuration
public class ActiveMQConfig {

    @Value("${activeMQ.broker.url}")
    private String brokerUrl;
    @Value("${activeMQ.listen.destination}")
    private String listenDestination;

    public String getBrokerUrl() {
        return brokerUrl;
    }

    public void setBrokerUrl(String brokerUrl) {
        this.brokerUrl = brokerUrl;
    }

    public String getListenDestination() {
        return listenDestination;
    }

    public void setListenDestination(String listenDestination) {
        this.listenDestination = listenDestination;
    }

    @Bean(name = "targetConnectionFactory")
    public ActiveMQConnectionFactory getActiveMQConnectionFactory() {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setBrokerURL(brokerUrl);
        return activeMQConnectionFactory;
    }

    @Bean(name = "connectionFactory")
    public SingleConnectionFactory getSingleConnectionFactory(ActiveMQConnectionFactory targetConnectionFactory) {
        SingleConnectionFactory singleConnectionFactory = new SingleConnectionFactory();
        singleConnectionFactory.setTargetConnectionFactory(targetConnectionFactory);
        return singleConnectionFactory;
    }

    /*@Bean(name="messageConverter")
    public MessageConverter getJacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }*/

    @Bean
    public JmsTemplate getJmsTemplate(SingleConnectionFactory connectionFactory) {
        JmsTemplate jmsTemplate = new JmsTemplate();
        //jmsTemplate.setMessageConverter(messageConverter);
        jmsTemplate.setConnectionFactory(connectionFactory);
        return jmsTemplate;
    }
}
