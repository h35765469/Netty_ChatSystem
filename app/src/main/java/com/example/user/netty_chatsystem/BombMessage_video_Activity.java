package com.example.user.netty_chatsystem;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.user.netty_chatsystem.Chat_DrawBoard.PageTwoFragment;

public class BombMessage_video_Activity extends AppCompatActivity {

    PageTwoFragment cameraFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bomb_message_video_);
        getSupportActionBar().hide();
        FragmentManager manager;
        manager = this.getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        cameraFragment = new PageTwoFragment();
        transaction.replace(R.id.cameraContainer, cameraFragment);
        transaction.commit();
    }
}
