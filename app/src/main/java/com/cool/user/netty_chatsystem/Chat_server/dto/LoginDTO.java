package com.cool.user.netty_chatsystem.Chat_server.dto;


import com.cool.user.netty_chatsystem.Chat_biz.entity.Login;
import com.cool.user.netty_chatsystem.Chat_core.transport.DataBuffer;
import com.cool.user.netty_chatsystem.Chat_core.transport.IMSerializer;

/**
 * Created by Tony on 2/19/15.
 */
public class LoginDTO implements IMSerializer {

    private Login login;

    public LoginDTO() {

    }

    public LoginDTO(Login login) {
        this.login = login;
    }

    public Login getLogin() {
        return login;
    }

    public void setLogin(Login login) {
        this.login = login;
    }


    public DataBuffer encode(short version) {
        DataBuffer buffer = new DataBuffer();
        buffer.writeString(login.getId());
        buffer.writeLong(login.getUin());
        buffer.writeString(login.getNickeName());
        buffer.writeString(login.getAuthToken());
        buffer.writeLong(login.getActiveTime());
        buffer.writeString(login.getAccount());
        return buffer;
    }

    public void decode(DataBuffer buffer, short version) {
        if(login == null) {
            login = new Login();
        }
        login.setId(buffer.readString());
        login.setUin(buffer.readLong());
        login.setNickeName(buffer.readString());
        login.setAuthToken(buffer.readString());
        login.setActiveTime(buffer.readLong());
        login.setAccount(buffer.readString());
    }

}
