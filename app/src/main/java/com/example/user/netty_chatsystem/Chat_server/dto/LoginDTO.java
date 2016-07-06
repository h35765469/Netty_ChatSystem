package com.example.user.netty_chatsystem.Chat_server.dto;


import com.example.user.netty_chatsystem.Chat_biz.entity.Login;
import com.example.user.netty_chatsystem.Chat_core.transport.DataBuffer;
import com.example.user.netty_chatsystem.Chat_core.transport.IMSerializer;

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
        buffer.writeLong(login.getUin());
        buffer.writeString(login.getAuthToken());
        buffer.writeLong(login.getActiveTime());
        buffer.writeString(login.getAccount());
        return buffer;
    }

    public void decode(DataBuffer buffer, short version) {
        if(login == null) {
            login = new Login();
        }
        login.setUin(buffer.readLong());
        login.setAuthToken(buffer.readString());
        login.setActiveTime(buffer.readLong());
        login.setAccount(buffer.readString());
    }

}
