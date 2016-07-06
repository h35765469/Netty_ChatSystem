package com.example.user.netty_chatsystem.Chat_server.dto;


import com.example.user.netty_chatsystem.Chat_biz.entity.User;
import com.example.user.netty_chatsystem.Chat_core.transport.DataBuffer;
import com.example.user.netty_chatsystem.Chat_core.transport.IMSerializer;

/**
 * Created by Tony on 2/19/15.
 */
public class UserDTO implements IMSerializer {

    private User user;

    public UserDTO() {

    }

    public UserDTO(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public DataBuffer encode(short version) {
        DataBuffer data = new DataBuffer();
        data.writeString(user.getId());
        data.writeLong(user.getUin());
        data.writeString(user.getAccount());
        data.writeString(user.getPassword());
        data.writeString(user.getNickName());
        data.writeString(user.getAvatarUrl());
        data.writeByte(user.getGender());
        return data;
    }


    public void decode(DataBuffer buffer, short version) {
        if (user == null) {
            user = new User();
        }
        user.setId(buffer.readString());
        user.setUin(buffer.readLong());
        user.setAccount(buffer.readString());
        user.setPassword(buffer.readString());
        user.setNickName(buffer.readString());
        user.setAvatarUrl(buffer.readString());
        user.setGender(buffer.readByte());
    }
}
