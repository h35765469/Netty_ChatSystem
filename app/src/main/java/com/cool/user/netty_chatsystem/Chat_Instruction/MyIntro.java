package com.cool.user.netty_chatsystem.Chat_Instruction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.cool.user.netty_chatsystem.MainActivity;
import com.cool.user.netty_chatsystem.R;
import com.github.paolorotolo.appintro.AppIntro;

public class MyIntro extends AppIntro {
    // Please DO NOT override onCreate. Use init
    @Override
    public void init(Bundle savedInstanceState) {

        getSupportActionBar().hide();
        //adding the three slides for introduction app you can ad as many you needed
        addSlide(AppIntroSampleSlider.newInstance(R.layout.app_intro1));
        addSlide(AppIntroSampleSlider.newInstance(R.layout.app_intro2));
        addSlide(AppIntroSampleSlider.newInstance(R.layout.app_intro3));
        addSlide(AppIntroSampleSlider.newInstance(R.layout.app_intro4));

        // Show and Hide Skip and Done buttons
        showStatusBar(true);
        showSkipButton(true);

        // Turn vibration on and set intensity
        // You will need to add VIBRATE permission in Manifest file
//        setVibrate(true);
//        setVibrateIntensity(30);

        //Add animation to the intro slider
        setFlowAnimation();
    }

    @Override
    public void onSkipPressed() {
        finish();
    }

    @Override
    public void onNextPressed() {
        // Do something here when users click or tap on Next button.
    }

    @Override
    public void onDonePressed() {
        // Do something here when users click or tap tap on Done button.
        finish();
    }

    @Override
    public void onSlideChanged() {
        // Do something here when slide is changed
    }
}
