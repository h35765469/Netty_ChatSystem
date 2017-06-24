package com.cool.user.netty_chatsystem.Chat_Service;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cool.user.netty_chatsystem.Chat_Sharepreference.SharePreferenceManager;

import java.util.HashMap;

/**
 * Created by user on 2017/2/26.
 */
public class ChatRestartBroadCastReceiver extends BroadcastReceiver {

    // 接收廣播後執行這個方法
    // 第一個參數Context物件，用來顯示訊息框、啟動服務
    // 第二個參數是發出廣播事件的Intent物件，可以包含資料
    @Override
    public void onReceive(Context context, Intent intent) {
        SharePreferenceManager sharePreferenceManager = new SharePreferenceManager(context);
        HashMap<String,String> userDetails = sharePreferenceManager.getUserDetails();
        String username = userDetails.get(SharePreferenceManager.KEY_NAME);
        String password = userDetails.get(SharePreferenceManager.KEY_EMAIL);
        String nickName = sharePreferenceManager.getNickName();
        try {
            Intent startIntent =  new  Intent(context , ChatService.class );
            startIntent.putExtra("username" , username);
            startIntent.putExtra("password", password);
            startIntent.putExtra("nickName", nickName);
            if(username != null) {
                if(!isMyServiceRunning(context, ChatService.class)){
                    context.startService(startIntent);
                    System.out.println("restartbroadcast ");
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
