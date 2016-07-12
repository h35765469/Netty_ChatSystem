package com.example.user.netty_chatsystem.Chat_Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

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
    }
}
