package com.cool.user.netty_chatsystem.Chat_Listview_Message;

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
    private boolean isEffect = false;
    private int effectMessage;//驚喜的類別
    private int effectPicture = 0;
    private int isRead = 0;
    private Long userId;
    private String dateTime;
    private String filePath;
    private String think;//驚喜的心情

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

    public boolean getIsEffect(){ return isEffect;}
    public void setIsEffect(boolean isEffect){
        this.isEffect = isEffect;
    }

    public int getEffectMessage(){
        return effectMessage;
    }
    public void setEffectMessage(int effectMessage){
        this.effectMessage = effectMessage;
    }

    public int getEffectPicture(){
        return effectPicture;
    }
    public void setEffectPicture(int effectPicture){
        this.effectPicture = effectPicture;
    }

    public int getIsRead(){
        return isRead;
    }
    public void setIsRead(int isRead){
        this.isRead = isRead;
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

    public String getFilePath(){
        return filePath;
    }

    public void setFilePath(String filePath){
        this.filePath = filePath;
    }

    public String getThink(){
        return  think;
    }

    public void setThink(String think){
        this.think = think;
    }
}
