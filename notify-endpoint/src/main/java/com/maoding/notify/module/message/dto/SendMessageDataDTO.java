package com.maoding.notify.module.message.dto;


import java.util.List;

public class SendMessageDataDTO {

    private String receiver;
    private List<String> receiverList;
    private String content;
    private String messageType;

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public List<String> getReceiverList() {
        return receiverList;
    }

    public void setReceiverList(List<String> receiverList) {
        this.receiverList = receiverList;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
}