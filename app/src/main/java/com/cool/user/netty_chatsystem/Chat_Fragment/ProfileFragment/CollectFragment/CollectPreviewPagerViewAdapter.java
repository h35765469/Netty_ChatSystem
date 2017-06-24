package com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.CollectFragment;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cool.user.netty_chatsystem.Chat_Client.handler.Client_UserHandler;
import com.cool.user.netty_chatsystem.Chat_MySQL.Config;
import com.cool.user.netty_chatsystem.Chat_Sqlite_ChatHistory.MyDBHelper;
import com.cool.user.netty_chatsystem.Chat_biz.entity.Friend;
import com.cool.user.netty_chatsystem.Chat_core.connetion.IMConnection;
import com.cool.user.netty_chatsystem.Chat_core.protocol.Commands;
import com.cool.user.netty_chatsystem.Chat_core.protocol.Handlers;
import com.cool.user.netty_chatsystem.Chat_core.transport.Header;
import com.cool.user.netty_chatsystem.Chat_core.transport.IMResponse;
import com.cool.user.netty_chatsystem.Chat_server.dto.FriendDTO;
import com.cool.user.netty_chatsystem.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

/**
 * Created by user on 2017/6/24.
 */

public class CollectPreviewPagerViewAdapter extends PagerAdapter{
    private boolean isMultiScr;
    Context context;
    String loginId;
    private int screenWidth;
    private int screenHeight;
    ArrayList<String> collectUrlArrayList;
    ArrayList<CollectData>collectDataArrayList;
    ArrayList<String> friendArrayList;
    DisplayImageOptions options;


    public CollectPreviewPagerViewAdapter(boolean isMultiScr) {
        this.isMultiScr = isMultiScr;
    }

    public CollectPreviewPagerViewAdapter(String loginId, int screenWidth, int screenHeight,
                                          ArrayList<String> collectUrlArrayList,  ArrayList<CollectData>collectDataArrayList, ArrayList<String> friendArrayList,
                                          Context context){
        this.loginId = loginId;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.collectUrlArrayList = collectUrlArrayList;
        this.collectDataArrayList = collectDataArrayList;
        this.friendArrayList = friendArrayList;
        this.context = context;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.logo)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.logo_red)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    @Override
    public int getCount() {
        return collectDataArrayList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(container.getContext()).inflate(R.layout.resource_collectpreview_pagerview, null);
        ImageView collectPreViewImg = (ImageView)relativeLayout.findViewById(R.id.collectPreViewImg);
        TextView backTxt = (TextView)relativeLayout.findViewById(R.id.backTxt);
        ImageView deleteCollectImg = (ImageView)relativeLayout.findViewById(R.id.deleteCollectImg);
        ImageView nextCollectImg = (ImageView)relativeLayout.findViewById(R.id.nextCollectImg);
        ImageView collectThinkImg = (ImageView)relativeLayout.findViewById(R.id.collectThinkImg);
        ImageView collectEffectImg = (ImageView)relativeLayout.findViewById(R.id.collectEffectImg);
        final ImageView collectAddFriendImg = (ImageView)relativeLayout.findViewById(R.id.collectAddFriendImg);
        ImageView showCollectEffectImg = (ImageView)relativeLayout.findViewById(R.id.showCollectEffectImg);
        ImageView collectProfileImg = (ImageView)relativeLayout.findViewById(R.id.collectProfileImg);
        TextView collectNameText = (TextView)relativeLayout.findViewById(R.id.randomNameText);
        container.addView(relativeLayout);

        if(differentiateFriendCondition(friendArrayList, collectDataArrayList.get(position))){
            collectAddFriendImg.setImageResource(R.drawable.logo);
        }

        collectAddFriendImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(differentiateFriendCondition(friendArrayList, collectDataArrayList.get(position))){
                    Toast.makeText(context, "已是朋友", Toast.LENGTH_SHORT).show();
                }else{
                    requestFriend(collectDataArrayList.get(position).getCollectId());
                    saveNewFriend(collectDataArrayList.get(position).getCollectId(), collectDataArrayList.get(position).getCollectUserName(), collectDataArrayList.get(position).getCollectNickName(), collectDataArrayList.get(position).getCollectProfile());
                    collectAddFriendImg.setImageResource(R.drawable.logo);
                }
            }
        });

        collectThinkImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context, R.style.selectorDialog);
                dialog.setContentView(R.layout.resource_think_dialog);
                TextView thinkText = (TextView) dialog.findViewById(R.id.thinkText);
                if (collectDataArrayList.get(position).getCollectThink().length() > 0) {
                    thinkText.setText(collectDataArrayList.get(position).getCollectThink());
                }

                // 由程式設定 Dialog 視窗外的明暗程度, 亮度從 0f 到 1f
                WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
                lp.dimAmount = 0.5f;
                dialog.getWindow().setAttributes(lp);
                dialog.show();
            }
        });

        //觀看大頭貼
        collectProfileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //bundle.putInt("position", position);
                /*FragmentManager fragmentManager = context.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                ProfilePreviewFragment profilePreviewFragment = new ProfilePreviewFragment();
                Bundle profileBundle = new Bundle();
                profileBundle.putString("profile", collectDataArrayList.get(position).getCollectProfile());
                profilePreviewFragment.setArguments(profileBundle);
                fragmentTransaction.replace(R.id.profileContainer, profilePreviewFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();*/
            }
        });

        //載入內容的圖片------------------------------------------------------
        ImageLoader.getInstance()
                .displayImage(collectUrlArrayList.get(position), collectPreViewImg, options, new SimpleImageLoadingListener() {
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
        //---------------------------------------------------

        //載入大頭貼---------------------------------------
        if(collectDataArrayList.get(position).getCollectProfile().length() > 0) {
            ImageLoader.getInstance()
                    .displayImage(Config.SERVER_PROFILE_ADDRESS + collectDataArrayList.get(position).getCollectProfile() + ".jpg", collectProfileImg, options, new SimpleImageLoadingListener() {
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
            //------------------------------------------------------------------
        }else{
            collectProfileImg.setImageResource(R.drawable.logo_red);
        }

        return relativeLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        RelativeLayout view = (RelativeLayout) object;
        container.removeView(view);
    }

    private boolean  differentiateFriendCondition(ArrayList<String>friendArrayList , CollectData collectData){
        if (friendArrayList.contains(collectData.getCollectId())) {
            return true;
        }
        return false;
    }

    // 請求朋友邀請
    public void requestFriend(String friendId){
        IMConnection connection = Client_UserHandler.getConnection();
        Friend friend = new Friend();
        friend.setId(loginId);
        friend.setFriendId(friendId);

        IMResponse resp = new IMResponse();
        Header header = new Header();
        header.setHandlerId(Handlers.USER);
        header.setCommandId(Commands.FRIEND_ADD_REQUEST);
        resp.setHeader(header);
        resp.writeEntity(new FriendDTO(friend));
        connection.sendResponse(resp);
    }

    //存取新朋友進去sqlite裡
    private void saveNewFriend(String friendId, String friendUserName, String friendName, String friendAvatarUri){
        //創建MyDBHelper對象
        MyDBHelper dbHelper = new MyDBHelper(context, "Chat.db", null, 1);
        //得到一個可讀的SQLiteDatabase對象
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        ContentValues cv = new ContentValues();

        //往ContentValues​​對象存放數據，鍵-值對模式
        cv.put("id", friendId);
        cv.put("friendusername", friendUserName);
        cv.put("friendname", friendName);
        cv.put("friendAvatarUri", friendAvatarUri);
        cv.put("status", 4);//status 0 : 代表認證中
        cv.put("viewer", 0);
        cv.put("favorite", 0);

        //調用insert方法，將數據插入數據庫
        db.insert("Friend", null, cv);

        db.close();
    }
}
