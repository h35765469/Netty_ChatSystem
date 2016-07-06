package com.example.user.netty_chatsystem.Chat_Fragment;


import android.app.Dialog;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.user.netty_chatsystem.BombMessage_video_Activity;
import com.example.user.netty_chatsystem.Character_Activity;
import com.example.user.netty_chatsystem.Chat_AnimationElement.SlideInAnimationHandler;
import com.example.user.netty_chatsystem.Myself_setting_Activity;
import com.example.user.netty_chatsystem.R;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 2016/3/16.
 */
public class Character_Fragment extends Fragment {

    final int[] image = {
      R.drawable.danger,R.drawable.danger
    };

    final String[] string = {
      "123","456"
    };


    public Character_Fragment(){

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_character_, container, false);

        final Character_Activity character_activity = new Character_Activity();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

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
