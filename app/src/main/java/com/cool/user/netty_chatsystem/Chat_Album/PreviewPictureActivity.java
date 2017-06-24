package com.cool.user.netty_chatsystem.Chat_Album;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import com.cool.user.netty_chatsystem.R;

import java.io.File;

public class PreviewPictureActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_picture);
        ImageView previewImageview = (ImageView)findViewById(R.id.previewImageview);
        Intent intent = getIntent();

        File imgFile = new File(intent.getStringExtra("picturePath"));
        if(imgFile.exists())
        {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            previewImageview.setImageBitmap(myBitmap);
        }
    }
}
