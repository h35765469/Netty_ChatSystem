package com.cool.user.netty_chatsystem.Chat_server.dto;


import com.cool.user.netty_chatsystem.Chat_biz.entity.Message_entity;
import com.cool.user.netty_chatsystem.Chat_core.transport.DataBuffer;
import com.cool.user.netty_chatsystem.Chat_core.transport.IMSerializer;

/**
 * Created by Tony on 2/20/15.
 */
public class MessageDTO implements IMSerializer {

    private Message_entity message;

    public MessageDTO() {

    }

    public MessageDTO(Message_entity message) {
        this.message = message;
    }

    /*public Long getTo() {
        return message.getTo();
    }*/

    public String getTo() {
        return message.getTo();
    }

    public String getFrom() {
        return message.getFrom();
    }

    public Message_entity getMessage() {
        return message;
    }

    public void setMessage(Message_entity message) {
        this.message = message;
    }


    public DataBuffer encode(short version) {
        DataBuffer buffer = new DataBuffer();
        buffer.writeString(message.getId());
        buffer.writeString(message.getToId());
        buffer.writeString(message.getTo());
        buffer.writeString(message.getFrom());
        buffer.writeString(message.getFromNickName());
        buffer.writeString(message.getToNickName());
        buffer.writeByte(message.getType());
        buffer.writeString(message.getMessage());
        buffer.writeLong(message.getCreateAt());
        buffer.writeInt(message.getRead());
        return buffer;
    }


    public void decode(DataBuffer buffer, short version) {
        if (message == null) {
            message = new Message_entity();
        }
        message.setId(buffer.readString());
        message.setToId(buffer.readString());
        message.setTo(buffer.readString());
        message.setFrom(buffer.readString());
        message.setFromNickName(buffer.readString());
        message.setToNickName(buffer.readString());
        message.setType(buffer.readByte());
        message.setMessage(buffer.readString());
        message.setCreateAt(buffer.readLong());
        message.setRead(buffer.readInt());
    }
}
