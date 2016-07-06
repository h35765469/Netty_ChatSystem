package com.example.user.netty_chatsystem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class TextMessage_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_message_);
        final com.beardedhen.androidbootstrap.BootstrapEditText verification_edittext = (com.beardedhen.androidbootstrap.BootstrapEditText)findViewById(R.id.verification);
        ImageView sendnumber_imageview = (ImageView)findViewById(R.id.sendnumber_imageview);

        sendnumber_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verification_edittext.setVisibility(v.VISIBLE);
                Intent it = new Intent();
                it.setClass(TextMessage_Activity.this,Character_Activity.class);
                startActivity(it);
                finish();
            }
        });
    }
}
