package com.example.user.netty_chatsystem;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.user.netty_chatsystem.Chat_Fragment.DrawBoard_Fragment;

public class DrawBoard_Activity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_board_);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new DrawBoard_Fragment())
                    .commit();
        }


    }

}
