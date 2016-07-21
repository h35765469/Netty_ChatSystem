package com.example.user.netty_chatsystem.Chat_biz.entity;


/**
 * Created by Tony on 2/20/15.
 */

public class Login extends BaseEntity {

    private String account;
    private long uin;
    private String authToken;
    private long activeTime;

    String[] friendArray;

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

    public String[] getFriendArray(){
        return friendArray;
    }

    public void setFriendArray(String[] friendArray){
        this.friendArray = friendArray;
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
