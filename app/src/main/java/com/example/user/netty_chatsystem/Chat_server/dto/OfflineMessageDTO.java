package com.example.user.netty_chatsystem.Chat_server.dto;


import com.example.user.netty_chatsystem.Chat_biz.entity.OfflineMessage;
import com.example.user.netty_chatsystem.Chat_core.transport.DataBuffer;
import com.example.user.netty_chatsystem.Chat_core.transport.IMSerializer;

/**
 * Created by user on 2016/7/6.
 */
public class OfflineMessageDTO implements IMSerializer {

    private OfflineMessage offlineMessage;

    public OfflineMessageDTO() {

    }

    public OfflineMessageDTO(OfflineMessage offlineMessage) {
        this.offlineMessage = offlineMessage;
    }

    /*public Long getTo() {
        return message.getTo();
    }*/

    public OfflineMessage getOfflineMessage() {
        return offlineMessage;
    }

    public void setMessage(OfflineMessage offlineMessage) {
        this.offlineMessage = offlineMessage;
    }


    public DataBuffer encode(short version) {
        DataBuffer buffer = new DataBuffer();
        buffer.writeStringArray(offlineMessage.getOfflineMessageArray());
        return buffer;
    }


    public void decode(DataBuffer buffer, short version) {
        if (offlineMessage == null) {
            offlineMessage = new OfflineMessage();
        }
        offlineMessage.setOfflineMessageArray(buffer.readStringArray());

    }
}

