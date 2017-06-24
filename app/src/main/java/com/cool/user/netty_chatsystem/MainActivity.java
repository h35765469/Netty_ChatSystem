package com.cool.user.netty_chatsystem;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.cool.user.netty_chatsystem.Chat_Fragment.EnterFragment.LoginFragment;
import com.cool.user.netty_chatsystem.Chat_Service.ChatService;
import com.cool.user.netty_chatsystem.Chat_Sharepreference.SharePreferenceManager;
import com.facebook.FacebookSdk;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.readystatesoftware.systembartint.SystemBarTintManager;


public class MainActivity extends AppCompatActivity {

    LoginFragment loginFragment = new LoginFragment();
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 100;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loginFragment.onActivityResult(requestCode, resultCode, data);
    }

    private Handler KickHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            //Yes Button Clicked
                            //使用sharePreferenceManager 判別是否以經登入過
                            SharePreferenceManager sharePreferenceManager = new SharePreferenceManager(MainActivity.this);
                            sharePreferenceManager.checkLogin(loginFragment);
                            //---------------------------------------------------------------------------------
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("有人登入你帳號，你已被強制登出")
                    .setPositiveButton("確定", dialogClickListener)
                    .setCancelable(false);
            AlertDialog alertDialog = builder.create();
            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
            alertDialog.show();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
        ChatService chatService = new ChatService();
        chatService.setChatServiceKickUserListener(new ChatService.ChatServiceKickUserListener() {
            @Override
            public void onChatServiceKickUserEvent() {
                Message message = new Message();
                KickHandler.sendMessage(message);
            }
        });

        SystemBarTintManager mTintManager;
        mTintManager = new SystemBarTintManager(this);
        mTintManager.setStatusBarTintEnabled(true);
        mTintManager.setNavigationBarTintEnabled(true);
        mTintManager.setTintColor(Color.RED);


        verifyStoragePermissions(this);
        getSupportActionBar().hide();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null){
            loginFragment.setArguments(bundle);//從ChatBroadcastReceiver來的為了點擊上方提醒欄後可以直接開啟聊天室
        }
        //使用sharePreferenceManager 判別是否以經登入過
        SharePreferenceManager sharePreferenceManager = new SharePreferenceManager(this);
        sharePreferenceManager.checkLogin(loginFragment);
        //---------------------------------------------------------------------------------
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);

        // -----Start New Dashboard  when notification is clicked----

        Intent broadcastReceiverIntent = new Intent(MainActivity.this, MainActivity.class);
        Bundle bundle = intent.getExtras();
        if(bundle != null){
            broadcastReceiverIntent.putExtras(bundle);//從ChatBroadcastReceiver來的為了點擊上方提醒欄後可以直接開啟聊天室
        }
        startActivity(broadcastReceiverIntent);

        MainActivity.this.finish();

        super.onNewIntent(intent);
    }

    //宣告可讀寫的權限
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
