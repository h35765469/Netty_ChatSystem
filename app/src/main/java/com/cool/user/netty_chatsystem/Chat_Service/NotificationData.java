package com.cool.user.netty_chatsystem.Chat_Service;

/**
 * Created by user on 2017/1/6.
 */
public class NotificationData{
    int notification;
    int sound;
    int vibrate;
    int led;
    public NotificationData(){

    }
    public NotificationData(int notification, int sound, int vibrate, int led){
        this.notification = notification;
        this.sound = sound;
        this.vibrate = vibrate;
        this.led =led;
    }

    public int getNotification(){
        return notification;
    }
    public int getSound(){
        return sound;
    }
    public int getVibrate(){
        return vibrate;
    }
    public int getLed(){
        return led;
    }
}
