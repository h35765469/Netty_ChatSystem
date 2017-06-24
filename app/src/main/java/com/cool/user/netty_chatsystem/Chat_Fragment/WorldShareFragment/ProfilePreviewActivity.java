package com.cool.user.netty_chatsystem.Chat_Fragment.WorldShareFragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.cool.user.netty_chatsystem.Chat_MySQL.Config;
import com.cool.user.netty_chatsystem.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class ProfilePreviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_preview);
        getSupportActionBar().hide();
        ImageView profilePreviewImg = (ImageView)findViewById(R.id.profilePreviewImg);
        ImageView backImg = (ImageView)findViewById(R.id.backImg);

        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.logo)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.logo_red)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String Profile = bundle.getString("profile");
        System.out.println("profiellength " + Profile.length());

        //download the profile----------------------------------
        if(Profile.length() > 0) {
            ImageLoader.getInstance().displayImage(Config.SERVER_PROFILE_ADDRESS + Profile + ".jpg", profilePreviewImg, options, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                }
            }, new ImageLoadingProgressListener() {
                @Override
                public void onProgressUpdate(String imageUri, View view, int current, int total) {
                }
            });
        }else{
            profilePreviewImg.setImageResource(R.drawable.logo_red);
        }

        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              finish();
            }
        });

    }
}
