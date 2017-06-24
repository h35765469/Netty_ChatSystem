package com.cool.user.netty_chatsystem.Chat_Service;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cool.user.netty_chatsystem.Chat_Sharepreference.SharePreferenceManager;

import java.util.HashMap;

/**
 * Created by user on 2017/5/13.
 */

public class ChatRestartServiceBroadcastReceiver extends BroadcastReceiver {
    // 接收廣播後執行這個方法
    // 第一個參數Context物件，用來顯示訊息框、啟動服務
    // 第二個參數是發出廣播事件的Intent物件，可以包含資料
    @Override
    public void onReceive(Context context, Intent intent) {
        //close the service and restart the service
        System.out.println("ChatRestartService");
        Intent stopIntent = new Intent(context, ChatService.class);
        context.stopService(stopIntent);
    }
}
