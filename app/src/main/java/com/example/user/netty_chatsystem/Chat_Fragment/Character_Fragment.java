package com.example.user.netty_chatsystem.Chat_Fragment;


import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.netty_chatsystem.BombMessage_video_Activity;
import com.example.user.netty_chatsystem.Character_Activity;
import com.example.user.netty_chatsystem.Chat_Client.ChatClient;
import com.example.user.netty_chatsystem.Chat_Client.handler.Client_MessageHandler;
import com.example.user.netty_chatsystem.Chat_Service.ChatBroadcastReceiver;
import com.example.user.netty_chatsystem.Chat_Service.ChatService;
import com.example.user.netty_chatsystem.Chat_Sharepreference.SharePreferenceManager;
import com.example.user.netty_chatsystem.Chat_biz.entity.Message_entity;
import com.example.user.netty_chatsystem.R;

import java.util.HashMap;

/**
 * Created by user on 2016/3/16.
 */
public class Character_Fragment extends Fragment {
    // Session Manager Class
    SharePreferenceManager sharePreferenceManager;

    ChatClient chatClient;

    private ChatService.ChatBinder chatBinder;

    //發送廣播事件用的Action
    public static final String BROADCAT_ACTION = "com.netty_chatsystem.ChatBroadcast";

    //建立接受廣播接收元件
    ChatBroadcastReceiver chatBroadcastReceiver = new ChatBroadcastReceiver();

    //宣告client_MessageHandler用來獲取message
    Client_MessageHandler client_messageHandler;

    public Character_Fragment(){

    }

    private ServiceConnection connection =  new  ServiceConnection() {

        @Override
        public  void  onServiceDisconnected(ComponentName name) {
        }

        @Override
        public  void  onServiceConnected(ComponentName name, IBinder service) {
            chatBinder = (ChatService.ChatBinder) service;
            chatBinder.startConnection();
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_character_, container, false);

        final Character_Activity character_activity = new Character_Activity();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        // Session class instance
        sharePreferenceManager = new SharePreferenceManager(getActivity().getApplicationContext());


        chatClient = new ChatClient();

        Toast.makeText(getActivity().getApplicationContext(), "User Login Status: " + sharePreferenceManager.isLoggedIn(), Toast.LENGTH_LONG).show();

        /**
                 * Call this function whenever you want to check user login
                 * This will redirect user to LoginActivity is he is not
                 * logged in
                * */
        sharePreferenceManager.checkLogin();

        // get user data from session
        HashMap<String, String> user = sharePreferenceManager.getUserDetails();

        // name
       final String username = user.get(SharePreferenceManager.KEY_NAME);

        // password
       final String password = user.get(SharePreferenceManager.KEY_EMAIL);

        try {
            //Intent startIntent =  new  Intent(getActivity() , ChatService.class );
            /*startIntent.putExtra("username" , username);
            startIntent.putExtra("password", password);
            //getActivity().startService(startIntent);
            //getActivity().bindService(startIntent, connection, Context.BIND_AUTO_CREATE);*/
            chatClient.run(username,password);

        }catch(Exception e){
            e.printStackTrace();
        }


        // 建立準備發送廣播事件的Intent物件
        client_messageHandler = new Client_MessageHandler();
        client_messageHandler.setListener(new Client_MessageHandler.Listener() {
            @Override
            public void onInterestingEvent(Message_entity message) {
                if (username.equals(message.getTo())) {
                    getActivity().registerReceiver(chatBroadcastReceiver, new IntentFilter(BROADCAT_ACTION));
                    Intent intent = new Intent(BROADCAT_ACTION);
                    intent.putExtra("from" , message.getFrom());
                    intent.putExtra("content" , message.getMessage());
                    getActivity().sendBroadcast(intent);
                }
            }
        });

        /**
         * Logout button click event
         * */
        TextView textView = (TextView)rootView.findViewById(R.id.textView9);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //close the service
                Intent stopIntent =  new  Intent( getActivity() , ChatService.class );
                //getActivity().stopService(stopIntent);
                //getActivity().unbindService(connection);

                // Clear the session data
                // This will clear all session data and
                // redirect user to LoginActivity
                sharePreferenceManager.logoutUser();
            }
        });

        //此為進入特殊相機
        ImageView menuicon_imageview = (ImageView)rootView.findViewById(R.id.menuicon_imageview);
        menuicon_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent();
                it.setClass(getActivity(),BombMessage_video_Activity.class);
                startActivity(it);
            }
        });

        //此為點擊進入聊天介面
        ImageView messagelisticon_imageview = (ImageView)rootView.findViewById(R.id.messagelisticon_imageview);
        messagelisticon_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //centerBottomMenu.close(true);
                character_activity.mViewPager.setCurrentItem(0);
            }
        });

        //此為點擊進入朋友介面
        ImageView friendlisticon_imageview = (ImageView)rootView.findViewById(R.id.friendlisticon_imageview);
        friendlisticon_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //centerBottomMenu.close(true);
                character_activity.mViewPager.setCurrentItem(2);
            }
        });

        return rootView;
    }

}
