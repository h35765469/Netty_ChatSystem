package com.cool.user.netty_chatsystem.Chat_Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import com.cool.user.netty_chatsystem.Chat_Sharepreference.SharePreferenceManager;
import com.cool.user.netty_chatsystem.MainActivity;
import com.cool.user.netty_chatsystem.R;

/**
 * Created by user on 2016/7/12.
 */
public class ChatBroadcastReceiver extends BroadcastReceiver {
    Context mContext;

    //**以下相關函式皆為通知MessageListFragment更改介面元件使用
    public static ChangeMessageListListener mChangeMessageListListener;

    public interface ChangeMessageListListener{
        public void onChangeMessageListEvent(String fromId, String from, String fromNickName,  String content, long createTime);
    }

    public void setChangeMessageListListener(ChangeMessageListListener mChangeMessageListListener1){
        mChangeMessageListListener = mChangeMessageListListener1;
    }

    //****************************************************************




    // 接收廣播後執行這個方法
    // 第一個參數Context物件，用來顯示訊息框、啟動服務
    // 第二個參數是發出廣播事件的Intent物件，可以包含資料
    @Override
    public void onReceive(Context context, Intent intent) {
        SharePreferenceManager sharePreferenceManager = new SharePreferenceManager(context);
        //sharePreferenceManager.createNotificationData();
        NotificationData notificationData = sharePreferenceManager.loadNotification();

        if(notificationData.getNotification() ==1) {
            mContext = context;
            Notification notification;
            NotificationManager manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

            Intent notificationIntent = new Intent(context, MainActivity.class);
            Bundle bundle = new Bundle();

            bundle.putString("friendId", intent.getStringExtra("fromId"));
            bundle.putInt("notificationType", intent.getIntExtra("type", 0));
            notificationIntent.putExtras(bundle);
            PendingIntent pendinfIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification.Builder builder = new Notification.Builder(context);
            builder.setAutoCancel(false);

            //傳過來的標題
            /*if (intent.getIntExtra("type", 0) == 0) {
                builder.setTicker("訊息來臨");
            }else if (intent.getIntExtra("type",0) == 1){
                builder.setTicker("朋友訊息");
            }else if(intent.getIntExtra("type",0) == 2){
                builder.setTicker("慌消息");
            }else if(intent.getIntExtra("type",0) == 3){
                builder.setTicker("喜訊");
            }*/

            //主題名稱
            builder.setContentTitle("WowChat");
            //內容
            if(intent.getIntExtra("type", 0) == 1 || intent.getIntExtra("type",0) == 2 || intent.getIntExtra("type",0) == 3){
                builder.setContentText(intent.getStringExtra("from") + intent.getStringExtra("content"));
            }else if(intent.getIntExtra("type",0) == 0){
                builder.setContentText(intent.getStringExtra("fromNickName"));
            }
            else{
                builder.setContentText(intent.getStringExtra("from"));
            }
            Bitmap logoRedIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_red);
            Bitmap logoIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder.setColor(Color.RED);
                builder.setSmallIcon(R.drawable.logo);
                //builder.setLargeIcon(logoIcon);
            } else {
                builder.setSmallIcon(R.drawable.logo_red);
                builder.setLargeIcon(logoRedIcon);
            }
            builder.setContentIntent(pendinfIntent);
            builder.setOngoing(false);
            builder.setPriority(Notification.PRIORITY_HIGH);
            //小標題
            builder.build();
            builder.setAutoCancel(true);


            notification = builder.getNotification();

            if(notificationData.getSound() == 1){
                notification.defaults |= Notification.DEFAULT_SOUND;
            }
            if(notificationData.getVibrate() == 1){
                notification.defaults |= Notification.DEFAULT_VIBRATE;
            }
            if(notificationData.getLed() == 1){
                notification.defaults |= Notification.DEFAULT_LIGHTS;
                notification.ledARGB = Color.GREEN;
                notification.ledOnMS = 300;
                notification.ledOffMS = 1000;
                notification.flags |= Notification.FLAG_SHOW_LIGHTS;
            }

            manager.notify(11, notification);


            /*if(intent.getIntExtra("type", 0) == 0) {//當是訊息文字時才發出
                if (intent.getStringExtra("from") != null) {
                    ChatBroadcastReceiverDoes(intent.getStringExtra("fromId"), intent.getStringExtra("from"), intent.getStringExtra("fromNickName"), intent.getStringExtra("content"), intent.getLongExtra("createTime", 0));
                }
            }*/

        }
    }
}
