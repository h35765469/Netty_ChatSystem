package com.example.user.netty_chatsystem;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.user.netty_chatsystem.Chat_CustomeElement.ProgressBar.CircularProgressBar;
import com.example.user.netty_chatsystem.Chat_WheelPicker.core.AbstractWheelPicker;
import com.example.user.netty_chatsystem.Chat_WheelPicker.view.WheelCrossPicker;
import com.mingle.sweetpick.CustomDelegate;
import com.mingle.sweetpick.SweetSheet;


import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;


public class Bombmode_Activity extends AppCompatActivity {
    Button button , button2 , button3;
    String dataStraight;
    private SweetSheet mSweetSheet3;
    private RelativeLayout r1;
    private PopupWindow mpopupwindow;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bombmode_);
        button = (Button)findViewById(R.id.button);
        r1 = (RelativeLayout) findViewById(R.id.fuck);
        button3 = (Button)findViewById(R.id.button3);



        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(Bombmode_Activity.this, R.style.selectorDialog);
                dialog.setContentView(R.layout.resource_chat_bombmessage_dialog);

                // 由程式設定 Dialog 視窗外的明暗程度, 亮度從 0f 到 1f
                WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
                lp.dimAmount = 0.2f;
                dialog.getWindow().setAttributes(lp);
                dialog.show();

                WheelCrossPicker straightPicker = (WheelCrossPicker) dialog.findViewById(R.id.wheel_straight);
                straightPicker.setItemIndex(2);
                straightPicker.setBackgroundColor(0xFFE5DEEB);
                straightPicker.setTextColor(0xFFA7A7DB);
                straightPicker.setCurrentTextColor(0xFF536D8A);
                straightPicker.setOnWheelChangeListener(new AbstractWheelPicker.SimpleWheelChangeListener() {
                    @Override
                    public void onWheelScrollStateChanged(int state) {
                        if (state != AbstractWheelPicker.SCROLL_STATE_IDLE) {
                            button2.setEnabled(false);
                        } else {
                            button2.setEnabled(true);
                        }
                    }

                    @Override
                    public void onWheelSelected(int index, String data) {
                        dataStraight = data;
                    }
                });

                button2 = (Button) dialog.findViewById(R.id.button2);
                button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(Bombmode_Activity.this, dataStraight, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });




    }




}



