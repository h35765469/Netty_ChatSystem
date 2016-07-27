package com.example.user.netty_chatsystem.Chat_Fragment;


import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Intent;
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

import com.example.user.netty_chatsystem.Character_Activity;
import com.example.user.netty_chatsystem.Chat_Client.handler.Client_UserHandler;
import com.example.user.netty_chatsystem.Chat_Service.ChatService;
import com.example.user.netty_chatsystem.Chat_Sharepreference.SharePreferenceManager;
import com.example.user.netty_chatsystem.Chat_core.connetion.IMConnection;
import com.example.user.netty_chatsystem.Chat_core.protocol.Commands;
import com.example.user.netty_chatsystem.Chat_core.protocol.Handlers;
import com.example.user.netty_chatsystem.Chat_core.transport.Header;
import com.example.user.netty_chatsystem.Chat_core.transport.IMResponse;
import com.example.user.netty_chatsystem.Myself_setting_Activity;
import com.example.user.netty_chatsystem.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by user on 2016/3/16.
 */
public class Character_Fragment extends Fragment {
    // Session Manager Class
    SharePreferenceManager sharePreferenceManager;

    //連上Server的connection變數
    IMConnection connection;

    private ChatService.ChatBinder chatBinder;

    public Character_Fragment(){

    }

    private ServiceConnection serviceConnection =  new  ServiceConnection() {

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
            Intent startIntent =  new  Intent(getActivity() , ChatService.class );
            startIntent.putExtra("username" , username);
            startIntent.putExtra("password", password);
            if(!username.equals("")) {
                if(!isServiceRunning()){
                    getActivity().startService(startIntent);
                }
                //getActivity().bindService(startIntent, serviceConnection, Context.BIND_AUTO_CREATE);
            }

        }catch(Exception e){
            e.printStackTrace();
        }


        /**
         * Logout button click event
         * */
        TextView textView = (TextView)rootView.findViewById(R.id.textView9);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //close the service
                Intent stopIntent =  new  Intent( getActivity() , ChatService.class );
                getActivity().stopService(stopIntent);
                //getActivity().unbindService(serviceConnection);

                connection = Client_UserHandler.getConnection();

                IMResponse resp = new IMResponse();
                Header header = new Header();
                header.setHandlerId(Handlers.MESSAGE);
                header.setCommandId(Commands.USER_LOGOUT_REQUEST);
                resp.setHeader(header);
                connection.sendResponse(resp);


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
                Intent it = getActivity().getIntent();
                //it.setClass(getActivity(),BombMessage_video_Activity.class);
                it.setClass(getActivity(), Myself_setting_Activity.class);
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

    public boolean isServiceRunning(){
        final ActivityManager activityManager = (ActivityManager)getActivity().getSystemService(getActivity().ACTIVITY_SERVICE);
        final List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
            if (runningServiceInfo.service.getClassName().equals("com.example.user.netty_chatsystem.Chat_Service.ChatService")){
                return true;
            }
        }
        return false;
    }

}
