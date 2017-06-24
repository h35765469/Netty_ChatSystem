package com.cool.user.netty_chatsystem.Chat_Sharepreference;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.cool.user.netty_chatsystem.Chat_Fragment.MainFragment.MainFragment;
import com.cool.user.netty_chatsystem.Chat_Service.ChatService;
import com.cool.user.netty_chatsystem.Chat_Service.NotificationData;
import com.cool.user.netty_chatsystem.R;

import java.util.HashMap;

/**
 * Created by user on 2016/7/7.
 */
public class SharePreferenceManager {

    //Shared Preferences
    SharedPreferences pref;

    //Editor for shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    public static Fragment loginFragment;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "Netty_ChatSystem";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "username";

    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "password";

    //獲得KeyId
    public static final String KEY_ID ="loginId";

    // Constructor
    public SharePreferenceManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String username, String password){
        // Storing login value as TRUE
        //editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        editor.putString(KEY_NAME, username);

        // Storing email in pref
        editor.putString(KEY_EMAIL, password);

        //初次登陸
        editor.putInt("firstLogin",0);

        // commit changes
        editor.commit();
    }

    public void setIsLogin(){
        editor.putBoolean(IS_LOGIN, true);
    }

    //創建notification的資料
    public void createNotificationData(){
        editor.putInt("notification", 1);//開關閉通知
        //0關閉 1 開啟
        editor.putInt("sound", 1);
        editor.putInt("vibrate", 1);
        editor.putInt("led", 1);
        editor.commit();
    }

   //獲取notification的資料
    public NotificationData  loadNotification(){
        int notification = pref.getInt("notification", 1);
        int sound = pref.getInt("sound", 1);
        int vibrate = pref.getInt("vibrate", 1);
        int led = pref.getInt("led",1);
        NotificationData notificationData = new NotificationData(notification, sound, vibrate, led);
        return notificationData;
    }

    //更新notification的資料
    /*notificationType
        0 : 通知
        1 : 聲音
        2 : 震動
        3 : led
        condition
        0 : 關閉
        1 : 開啟
    */
    public void updateNotification(int notificationType, int condition){
        switch(notificationType){
            case 0 : editor.putInt("notification",condition);
                break;
            case 1 : editor.putInt("sound",condition);
                break;
            case 2 : editor.putInt("vibrate", condition);
                break;
            case 3 : editor.putInt("led",condition);
                break;
        }
        editor.commit();
    }

    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));

        // user email id
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));


        // return user
        return user;
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(Fragment fragment){
        loginFragment = fragment;
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            //Intent i = new Intent(_context, MainActivity.class);
            // Closing all the Activities
            //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            //_context.startActivity(i);
            FragmentManager fragmentManager = ((FragmentActivity)_context).getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.allContainer,fragment);
            fragmentTransaction.commit();
        }else{
            saveMainFragmentIndex(1);//初次登入預設為1頁面
            Bundle bundle = fragment.getArguments();
            MainFragment mainFragment = new MainFragment();
            if(bundle!=null){
                System.out.println("sharePreferencebundle" + bundle.getInt("notificationType"));
                mainFragment.setArguments(bundle);//從MainActivity來的為了點擊上方提醒欄後可以直接開啟聊天室
                saveMainFragmentIndex(3);//開啟聊天室先到messagelist裡
            }
            FragmentManager fragmentManager = ((FragmentActivity)_context).getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.allContainer,mainFragment);
            fragmentTransaction.commit();
        }
    }

    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        //Intent i = new Intent(_context, MainActivity.class);
        // Closing all the Activities
        //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        //_context.startActivity(i);
        FragmentManager fragmentManager = ((FragmentActivity)_context).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.allContainer,loginFragment);
        fragmentTransaction.commit();
    }

    //剔除使用者清除資料
    public void kickOutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){

        //後面的false為假設pref無資料，預設為false
        return pref.getBoolean(IS_LOGIN, false);
    }

    public void saveLoginId(String loginId){
        editor.putString("loginId", loginId);
        editor.commit();
    }

    public String getLoginId(){
        return  pref.getString(KEY_ID,"");
    }

    public void saveNickName(String nickName){
        editor.putString("nickName", nickName);
        editor.commit();
    }

    public String getNickName(){
        return pref.getString("nickName","");
    }

    public void saveProfileName(String profileName){
        editor.putString("profileName",profileName);
        editor.commit();
    }

    public String getProfileName(){
        return pref.getString("profileName","");
    }

    public void saveFirstLogin(int firstLogin){
        editor.putInt("firstLogin", firstLogin);
        editor.commit();
    }

    //0是第一次登陸，1為登入過
    public int getFirstLogin(){
        return pref.getInt("firstLogin",0);
    }

    public void saveMainFragmentIndex(int mainFragmentIndex){
        editor.putInt("mainFragmentIndex", mainFragmentIndex);
        editor.commit();
    }

    public boolean getRestart(){
        return pref.getBoolean("restart",true);
    }

    public void saveRestart(Boolean restart){
        editor.putBoolean("restart", restart);
        editor.commit();
    }

    //確認目前為哪個主頁面
    public int getMainFragmentIndex(){
        return pref.getInt("mainFragmentIndex",1);
    }

    private void openConnection(String username, String password){
        try {
            Intent startIntent =  new  Intent(_context , ChatService.class );
            startIntent.putExtra("username" , username);
            startIntent.putExtra("password", password);
            if(!username.equals("")) {
                if(!isMyServiceRunning(ChatService.class)){
                    _context.startService(startIntent);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) _context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}

