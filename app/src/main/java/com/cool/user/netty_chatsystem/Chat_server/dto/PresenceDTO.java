package com.cool.user.netty_chatsystem.Chat_server.dto;


import com.cool.user.netty_chatsystem.Chat_biz.bean.Presence;
import com.cool.user.netty_chatsystem.Chat_core.transport.DataBuffer;
import com.cool.user.netty_chatsystem.Chat_core.transport.IMSerializer;

/**
 * Created by Tony on 2/24/15.
 */
//從presenceworker

public class PresenceDTO implements IMSerializer {

    private Presence presence;

    public PresenceDTO() {
    }

    public PresenceDTO(Presence presence) {
        this.presence = presence;
    }

    public Presence getPresence() {
        return presence;
    }

    public void setPresence(Presence presence) {
        this.presence = presence;
    }


    public DataBuffer encode(short version) {
        DataBuffer buffer = new DataBuffer();
        buffer.writeLong(presence.getUin());
        buffer.writeByte(presence.getMode());
        buffer.writeString(presence.getStatus());
        return buffer;
    }


    public void decode(DataBuffer buffer, short version) {
        presence = new Presence();
        presence.setUin(buffer.readLong());
        presence.setMode(buffer.readByte());
        presence.setStatus(buffer.readString());
    }
}
