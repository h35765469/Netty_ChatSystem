package com.example.user.netty_chatsystem.Chat_server.dto;


import com.example.user.netty_chatsystem.Chat_core.transport.DataBuffer;
import com.example.user.netty_chatsystem.Chat_core.transport.IMSerializer;

/**
 * Created by Tony on 2/20/15.
 */
public class AckDTO implements IMSerializer {

    private String to;
    private String ackId;

    public AckDTO() {
    }

    public AckDTO(String to, String ackId) {
        this.to = to;
        this.ackId = ackId;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getAckId() {
        return ackId;
    }

    public void setAckId(String ackId) {
        this.ackId = ackId;
    }


    public DataBuffer encode(short version) {
        DataBuffer buffer = new DataBuffer();
        buffer.writeString(to);
        buffer.writeString(ackId);
        return buffer;
    }


    public void decode(DataBuffer buffer, short version) {
        to = buffer.readString();
        ackId = buffer.readString();
    }

    @Override
    public String toString() {
        return "AckDTO{" +
                "to=" + to +
                ", ackId='" + ackId + '\'' +
                '}';
    }
}
