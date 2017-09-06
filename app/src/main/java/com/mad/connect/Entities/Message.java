package com.mad.connect.Entities;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by pushparajparab on 11/17/16.
 */
public class Message {

    String message;
    String sender;
    String receiver;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    String key;
    Date sendTime;
    boolean read;
    boolean IsImage;
    ArrayList<MessageContent> allMessages;

    public ArrayList<MessageContent> getAllMessages() {
        return allMessages;
    }

    public void setAllMessages(ArrayList<MessageContent> allMessages) {
        this.allMessages = allMessages;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message1 = (Message) o;

        if (read != message1.read) return false;
        if (IsImage != message1.IsImage) return false;
        if (message != null ? !message.equals(message1.message) : message1.message != null)
            return false;
        if (sender != null ? !sender.equals(message1.sender) : message1.sender != null)
            return false;
        if (receiver != null ? !receiver.equals(message1.receiver) : message1.receiver != null)
            return false;
        if (key != null ? !key.equals(message1.key) : message1.key != null) return false;
        if (sendTime != null ? !sendTime.equals(message1.sendTime) : message1.sendTime != null)
            return false;
        return allMessages != null ? allMessages.equals(message1.allMessages) : message1.allMessages == null;

    }



    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isImage() {
        return IsImage;
    }

    public void setImage(boolean image) {
        IsImage = image;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }


    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
