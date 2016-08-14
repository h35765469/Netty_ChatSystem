package com.example.user.netty_chatsystem.Chat_Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.netty_chatsystem.Chat_AnimationElement.BangEffect.SmallBang;
import com.example.user.netty_chatsystem.Chat_AnimationElement.BangEffect.SmallBangListener;
import com.example.user.netty_chatsystem.R;

import java.io.InputStream;

/**
 * Created by user on 2016/8/9.
 */
public class Profile_Fragment extends BaseFragment {

    private int mInterval = 2000; // 5 seconds by default, can be changed later
    private Handler mHandler;
    ImageView effectProfile_imageview;
    int effectdrawable ;
    SmallBang smallBang;
    Bitmap firstReturnedImage;
    Bitmap SecondReturnedImage;

    //以下為使用在dialog的元件變數********
    ImageView firstProfile_imageview ;
    ImageView profileEffect_imageview;
    ImageView secondProfile_imageview ;
    ImageView firstAlbum_imageview ;
    ImageView effectSelecter_imageview ;
    ImageView secondAlbum_imageview ;
    //************************************

    @Override
    public View initView(LayoutInflater inflater){
        View view = inflater.inflate(R.layout.profile_fragment,null);
        effectProfile_imageview = (ImageView)view.findViewById(R.id.effectprofile_imageview);
        TextView myName_textview = (TextView)view.findViewById(R.id.myname_textview);
        smallBang = SmallBang.attach2Window(getActivity());
        effectProfile_imageview.setImageResource(R.drawable.blockade_whie);
        effectProfile_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRepeatingTask();
                final Dialog dialog = new Dialog(getActivity(), R.style.selectorDialog);
                dialog.setContentView(R.layout.resource_profilefragment_dialog);

                assignProfile(dialog);

                // 由程式設定 Dialog 視窗外的明暗程度, 亮度從 0f 到 1f
                WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
                lp.dimAmount = 0.2f;
                dialog.getWindow().setAttributes(lp);
                dialog.show();
            }
        });

        return view;
    }

    public void assignProfile(Dialog dialog){
        firstProfile_imageview = (ImageView)dialog.findViewById(R.id.firstProfile_imageview);
        profileEffect_imageview = (ImageView)dialog.findViewById(R.id.profileEffect_imageview);
        secondProfile_imageview = (ImageView)dialog.findViewById(R.id.secondProfile_imageview);
        firstAlbum_imageview = (ImageView)dialog.findViewById(R.id.firstAlbum_imageview);
        effectSelecter_imageview = (ImageView)dialog.findViewById(R.id.effectSelecter_imageview);
        secondAlbum_imageview = (ImageView)dialog.findViewById(R.id.secondAlbum_imageview);

        //第一張圖片拍照
        firstProfile_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        profileEffect_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //第二張圖片拍照
        secondProfile_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //選擇第一張頭貼的相本
        firstAlbum_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }
        });

        //選擇頭貼特效
        effectSelecter_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //選擇第二張頭貼的相本
        secondAlbum_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 2);
            }
        });


    }



    @Override
    public  void initData(Bundle savedInstanceState){

    }

    @Override
    public void onStart(){
        super.onStart();
        mHandler = new Handler();
        startRepeatingTask();
    }

    @Override
    public void onPause(){
        super.onPause();
        //stopRepeatingTask();
    }


    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
            effectProfile_imageview.setImageBitmap(SecondReturnedImage);
            smallBang.bang(effectProfile_imageview, new SmallBangListener() {
                @Override
                public void onAnimationStart() {
                }

                @Override
                public void onAnimationEnd() {
                    effectProfile_imageview.setImageBitmap(firstReturnedImage);
                }
            });
            mHandler.postDelayed(mStatusChecker, mInterval);
        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

    //處理照片
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1){
            if(data != null) {
                Uri selectedImage = data.getData();

                getPath(selectedImage);
                try {
                    InputStream is;
                    is = getActivity().getContentResolver().openInputStream(selectedImage);
                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    opts.inJustDecodeBounds = true;
                    //BitmapFactory.decodeStream(bis,null,opts);
                    BitmapFactory.decodeStream(is, null, opts);

                    //The new size we want to scale to
                    final int REQUIRED_SIZE = 200;

                    //Find the correct scale value. It should be the power of 2.
                    int scale = 1;
                    while (opts.outWidth / scale / 2 >= REQUIRED_SIZE || opts.outHeight / scale / 2 >= REQUIRED_SIZE)
                        scale *= 2;

                    opts.inSampleSize = scale;
                    opts.inJustDecodeBounds = false;
                    is = null;
                    System.gc();
                    InputStream is2 = getActivity().getContentResolver().openInputStream(selectedImage);

                    firstReturnedImage = BitmapFactory.decodeStream(is2, null, opts);

                    firstProfile_imageview.setImageBitmap(firstReturnedImage);
                    stopRepeatingTask();


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        else if(requestCode == 2){
            if(data != null) {
                Uri selectedImage = data.getData();

                getPath(selectedImage);
                try {
                    InputStream is;
                    is = getActivity().getContentResolver().openInputStream(selectedImage);
                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    opts.inJustDecodeBounds = true;
                    //BitmapFactory.decodeStream(bis,null,opts);
                    BitmapFactory.decodeStream(is, null, opts);

                    //The new size we want to scale to
                    final int REQUIRED_SIZE = 200;

                    //Find the correct scale value. It should be the power of 2.
                    int scale = 1;
                    while (opts.outWidth / scale / 2 >= REQUIRED_SIZE || opts.outHeight / scale / 2 >= REQUIRED_SIZE)
                        scale *= 2;

                    opts.inSampleSize = scale;
                    opts.inJustDecodeBounds = false;
                    is = null;
                    System.gc();
                    InputStream is2 = getActivity().getContentResolver().openInputStream(selectedImage);

                    SecondReturnedImage = BitmapFactory.decodeStream(is2, null, opts);

                    secondProfile_imageview.setImageBitmap(SecondReturnedImage);
                    stopRepeatingTask();


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //獲取圖片的路徑
    public String getPath(Uri uri){
        String[] filePathColumn={MediaStore.Images.Media.DATA};

        Cursor cursor = getActivity().getContentResolver().query(uri, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex=cursor.getColumnIndex(filePathColumn[0]);
        return cursor.getString(columnIndex);
    }

}
