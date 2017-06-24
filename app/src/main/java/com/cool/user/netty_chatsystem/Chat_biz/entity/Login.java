package com.cool.user.netty_chatsystem.Chat_biz.entity;


/**
 * Created by Tony on 2/20/15.
 */

public class Login extends BaseEntity {

    private String account;
    private long uin;
    private String nickeName;
    private String authToken;
    private long activeTime;


    public Login(){

    }

    public Login(long uin){
        this.uin = uin;
    }

    public String getAccount(){
        return account;
    }

    public void setAccount(String account){
        this.account = account;
    }

    public long getUin() {
        return uin;
    }

    public void setUin(long uin) {
        this.uin = uin;
    }

    public String getNickeName(){
        return nickeName;
    }

    public void setNickeName(String nickeName){
        this.nickeName = nickeName;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public long getActiveTime() {
        return activeTime;
    }

    public void setActiveTime(long activeTime) {
        this.activeTime = activeTime;
    }


    @Override
    public String toString() {
        return "Login{" +
                "uin=" + uin +
                ", account='" + account + '\'' +
                ", authToken='" + authToken + '\'' +
                ", activeTime=" + activeTime +
                '}';
    }
}
