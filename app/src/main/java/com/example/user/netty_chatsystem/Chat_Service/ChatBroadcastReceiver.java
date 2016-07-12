package com.example.user.netty_chatsystem.Chat_Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.user.netty_chatsystem.Character_Activity;
import com.example.user.netty_chatsystem.R;

/**
 * Created by user on 2016/7/12.
 */
public class ChatBroadcastReceiver extends BroadcastReceiver {


    // 接收廣播後執行這個方法
    // 第一個參數Context物件，用來顯示訊息框、啟動服務
    // 第二個參數是發出廣播事件的Intent物件，可以包含資料
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "receive the broadcast", Toast.LENGTH_SHORT).show();
        Notification notification;

        NotificationManager manager = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(context , Character_Activity.class);
        PendingIntent pendinfIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        Notification.Builder builder = new Notification.Builder(context);
        builder.setAutoCancel(false);
        //傳過來的標題
        builder.setTicker("訊息來臨");
        //主題名稱
        builder.setContentTitle(intent.getStringExtra("from"));
        //內容
        builder.setContentText(intent.getStringExtra("content"));
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentIntent(pendinfIntent);
        builder.setOngoing(true);
        //小標題
        builder.setNumber(1);
        builder.build();

        notification = builder.getNotification();
        manager.notify(11, notification);
    }
}
