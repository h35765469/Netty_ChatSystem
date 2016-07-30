package com.example.user.netty_chatsystem.Chat_Listview_Message;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by user on 2016/2/24.
 */
public class ChatMessage implements Serializable {
    private long id;
    private boolean isMe;
    private String message;
    private Bitmap bitmap;
    private Long userId;
    private String dateTime;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public boolean getIsme() {
        return isMe;
    }
    public void setMe(boolean isMe) {
        this.isMe = isMe;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public Bitmap getBitmap(){
        return bitmap;
    }
    public void setBitmap(Bitmap bitmap){
        this.bitmap = bitmap;
    }
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getDate() {
        return dateTime;
    }

    public void setDate(String dateTime) {
        this.dateTime = dateTime;
    }
}
