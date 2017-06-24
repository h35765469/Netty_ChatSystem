package com.cool.user.netty_chatsystem;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Logo_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo_);
        getSupportActionBar().hide();
        handler.sendEmptyMessageDelayed(GOTO_MAIN_ACTIVITY, 2000);
    }

    private static final int GOTO_MAIN_ACTIVITY = 0;

    private Handler handler = new Handler(){

        public void handleMessage(android.os.Message msg) {



            switch (msg.what) {

                case GOTO_MAIN_ACTIVITY:

                    Intent intent = new Intent();

                    intent.setClass(Logo_Activity.this, MainActivity.class);

                    startActivity(intent);

                    finish();

                    break;



                default:

                    break;

            }

        };

    };
}
