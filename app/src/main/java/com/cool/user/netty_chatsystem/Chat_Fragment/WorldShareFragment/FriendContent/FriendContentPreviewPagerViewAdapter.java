package com.cool.user.netty_chatsystem.Chat_Fragment.WorldShareFragment.FriendContent;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cool.user.netty_chatsystem.Chat_Client.handler.Client_UserHandler;
import com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.CollectFragment.CollectData;
import com.cool.user.netty_chatsystem.Chat_Fragment.UItraViewPager.UltraViewPagerView;
import com.cool.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.Chat_AnimationElement.BubbleEffect.BubbleView;
import com.cool.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.Chat_AnimationElement.HeartEffect.BezierEvaluator;
import com.cool.user.netty_chatsystem.Chat_Fragment.WorldShareFragment.ProfilePreviewFragment;
import com.cool.user.netty_chatsystem.Chat_MySQL.Config;
import com.cool.user.netty_chatsystem.Chat_MySQL.DBConnector;
import com.cool.user.netty_chatsystem.Chat_Sqlite_ChatHistory.MyDBHelper;
import com.cool.user.netty_chatsystem.Chat_biz.entity.Message_entity;
import com.cool.user.netty_chatsystem.Chat_core.connetion.IMConnection;
import com.cool.user.netty_chatsystem.Chat_core.protocol.Commands;
import com.cool.user.netty_chatsystem.Chat_core.protocol.Handlers;
import com.cool.user.netty_chatsystem.Chat_core.transport.Header;
import com.cool.user.netty_chatsystem.Chat_core.transport.IMResponse;
import com.cool.user.netty_chatsystem.Chat_server.dto.MessageDTO;
import com.cool.user.netty_chatsystem.MainActivity;
import com.cool.user.netty_chatsystem.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by user on 2017/6/25.
 */

public class FriendContentPreviewPagerViewAdapter extends PagerAdapter {
    private boolean isMultiScr;
    private String loginId;
    private int screenWidth;
    private int screenHeight;
    ArrayList<FriendContentData> friendContentArrayList;
    ArrayList<CollectData> collectDataArrayList;
    ArrayList<Boolean> uiShowArrayList = new ArrayList<>();
    ArrayList<Boolean> isCollectClickArrayList = new ArrayList<>();
    Context context;
    DisplayImageOptions options;
    UltraViewPagerView ultraViewPagerView;
    ImageView collectFriendContentImg;
    ImageView friendContentPreviewImg;
    ImageView friendContentProfileImg;
    ImageView friendContentThinkImg;
    ImageView selectFriendContentEffectImg;
    TextView backTxt;

    public FriendContentPreviewPagerViewAdapter(boolean isMultiScr) {
        this.isMultiScr = isMultiScr;
    }

    public FriendContentPreviewPagerViewAdapter(String loginId, int screenWidth, int screenHeight,
                                                ArrayList<FriendContentData> friendContentArrayList , Context context,
                                                UltraViewPagerView ultraViewPagerView, ArrayList<CollectData> collectDataArrayList){
        this.loginId = loginId;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
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
        this.friendContentArrayList = friendContentArrayList;
        this.ultraViewPagerView = ultraViewPagerView;
        this.collectDataArrayList = collectDataArrayList;
        /*for(int i = 0; i < friendContentArrayList.size(); i++){
            uiShowArrayList.add(true);
        }*/
    }

    @Override
    public int getCount() {
        return friendContentArrayList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(Object object){
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(container.getContext()).inflate(R.layout.resource_friendcontentpreview_pagerview, null);
        relativeLayout.setTag("relativelayout"+ position);
        friendContentPreviewImg = (ImageView)relativeLayout.findViewById(R.id.friendContentPreviewImg);
        friendContentProfileImg = (ImageView)relativeLayout.findViewById(R.id.friendContentProfileImg);
        backTxt = (TextView)relativeLayout.findViewById(R.id.backTxt);
        collectFriendContentImg = (ImageView)relativeLayout.findViewById(R.id.collectFriendContentImg);
        collectFriendContentImg.setTag("collectFriendContentImg" + position);
        friendContentThinkImg = (ImageView)relativeLayout.findViewById(R.id.friendContentThinkImg);
        final ImageView friendContentEffectImg = (ImageView)relativeLayout.findViewById(R.id.friendContentEffectImg);
        friendContentEffectImg.setTag("friendContentEffectImg" + position);
        ImageView showFriendContentEffectImg = (ImageView)relativeLayout.findViewById(R.id.showFriendContentEffectImg);
        showFriendContentEffectImg.setTag("showFriendContentEffectImg" + position);
        final LinearLayout iconBar = (LinearLayout)relativeLayout.findViewById(R.id.iconbar);
        final RelativeLayout upBar = (RelativeLayout)relativeLayout.findViewById(R.id.upBar);
        TextView friendNameTxt = (TextView)relativeLayout.findViewById(R.id.friendNameTxt);

        container.addView(relativeLayout);

        friendNameTxt.setText(friendContentArrayList.get(position).getOwnerNickName());
        differentiateFriendCondition(collectDataArrayList, friendContentArrayList.get(position));

        Typeface font = Typeface.createFromAsset(context.getAssets(),"fonts/fontawesome-webfont.ttf");//設定back的按紐
        backTxt.setTypeface(font);
        backTxt.setText("\uf060");
        backTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)context).getSupportFragmentManager().popBackStack();
            }
        });

        /*friendContentPreviewImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(uiShowArrayList.get(position)){
                    iconBar.setVisibility(View.GONE);
                    upBar.setVisibility(View.GONE);
                    uiShowArrayList.set(position, false);
                }else{
                    iconBar.setVisibility(View.VISIBLE);
                    upBar.setVisibility(View.VISIBLE);
                    uiShowArrayList.set(position, true);
                }
            }
        });*/

        collectFriendContentImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Client_UserHandler.getConnection() != null) {
                    if(isCollectClickArrayList.get(position)) {
                        saveCollectContentToRemoteServe(friendContentArrayList.get(position).getOwnerId(), friendContentArrayList.get(position).getContent());
                        sendCollectNotification(friendContentArrayList.get(position).getOwnerId());
                        saveCollectFriendContentInSqlite(friendContentArrayList.get(position).getOwnerId(), friendContentArrayList.get(position).getContent());
                        ImageView selectCollectFriendContentImg = (ImageView)ultraViewPagerView.findViewWithTag("collectFriendContentImg"+position);
                        selectCollectFriendContentImg.setImageResource(R.drawable.agenda_color);
                        isCollectClickArrayList.set(position, false);
                    }else{
                        Toast.makeText(context, "你已收藏過此內容", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(context, "無法收藏，請確認連線狀態", Toast.LENGTH_SHORT).show();
                }
            }
        });

        friendContentThinkImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Client_UserHandler.getConnection() != null) {
                    final Dialog dialog = new Dialog(context, R.style.selectorDialog);
                    dialog.setContentView(R.layout.resource_think_dialog);
                    TextView thinkText = (TextView) dialog.findViewById(R.id.thinkText);
                    if (friendContentArrayList.get(position).getThink().length() > 0) {
                        thinkText.setText(friendContentArrayList.get(position).getThink());
                    }

                    // 由程式設定 Dialog 視窗外的明暗程度, 亮度從 0f 到 1f
                    WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
                    lp.dimAmount = 0.5f;
                    dialog.getWindow().setAttributes(lp);
                    dialog.show();
                }else{
                    Toast.makeText(context, "無法觀看心情，請確認連線狀態", Toast.LENGTH_SHORT).show();
                }
            }
        });

        friendContentEffectImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Client_UserHandler.getConnection() != null) {
                    selectFriendContentEffectImg = (ImageView)ultraViewPagerView.findViewWithTag("friendContentEffectImg"+position);
                    selectFriendContentEffectImg.setEnabled(false);
                    ultraViewPagerView.setScroll(false);

                    switch (friendContentArrayList.get(position).getEffect()) {
                        case 0: {
                            bombEffect(position);
                            break;
                        }
                        case 1: {
                            heartEffect(position);
                            break;
                        }
                        case 2: {
                            bubbleEffect(position);
                            break;
                        }
                        default: {
                            Toast.makeText(context, "無特效呵呵!", Toast.LENGTH_SHORT).show();
                            selectFriendContentEffectImg.setEnabled(true);
                            ultraViewPagerView.setScroll(true);
                        }
                    }
                }else{
                    Toast.makeText(context, "無法觀看特效，請確認連線狀態", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //觀看朋友大頭貼
        friendContentProfileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = ((MainActivity)context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                ProfilePreviewFragment profilePreviewFragment = new ProfilePreviewFragment();
                Bundle profileBundle = new Bundle();
                profileBundle.putString("profile", friendContentArrayList.get(position).getOwnerProfileName());
                profilePreviewFragment.setArguments(profileBundle);
                fragmentTransaction.replace(R.id.shareContentContainer, profilePreviewFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        //載入朋友內容------------------------------------------------------------
        ImageLoader.getInstance()
                .displayImage(Config.SERVER_ADDRESS + friendContentArrayList.get(position).getContent() + ".jpg", friendContentPreviewImg, options, new SimpleImageLoadingListener() {
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
        //------------------------------------------------------------------------------------

        if(friendContentArrayList.get(position).getOwnerProfileName().length() > 0) {
            //載入朋友大頭貼-----------------------------------
            ImageLoader.getInstance()
                    .displayImage(Config.SERVER_PROFILE_ADDRESS + friendContentArrayList.get(position).getOwnerProfileName() + ".jpg", friendContentProfileImg, options, new SimpleImageLoadingListener() {
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
            //----------------------------------------------------------
        }else{
            friendContentProfileImg.setImageResource(R.drawable.logo_red);
        }


        return relativeLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        RelativeLayout view = (RelativeLayout) object;
        container.removeView(view);
    }

    //儲存collect data 進入遠端mysql
    private void saveCollectContentToRemoteServe(String ownerid, String collectContent){
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("ownerid", ownerid));
        params.add(new BasicNameValuePair("collectid", loginId));
        params.add(new BasicNameValuePair("content", collectContent));
        String result = DBConnector.executeQuery("", Config.COLLECT_SAVE_URL, params);
    }

    //儲存收集的文章進入sqlite中
    private void saveCollectFriendContentInSqlite(String collectId, String collectContent){
        //創建MyDBHelper對象
        MyDBHelper dbHelper = new MyDBHelper(context , "Chat.db", null, 1);
        //得到一個可讀的SQLiteDatabase對象
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //生成ContentValues​​對象 //key:列名，value:想插入的值
        ContentValues cv = new ContentValues();

        cv.put("id", collectId);
        cv.put("content", collectContent);
        cv.put("isdelete", 0);

        db.insert("Collect", null, cv);
        db.close();
    }

    private void sendCollectNotification(String toId){
        IMConnection connection = Client_UserHandler.getConnection();
        Message_entity message_entity = new Message_entity();
        message_entity.setId(loginId);
        message_entity.setToId(toId);

        IMResponse resp = new IMResponse();
        Header header = new Header();
        header.setHandlerId(Handlers.MESSAGE);
        header.setCommandId(Commands.COLLECT_NOTIFICATION_REQUEST);
        resp.setHeader(header);
        resp.writeEntity(new MessageDTO(message_entity));
        connection.sendResponse(resp);

    }

    private void deleteFinishReadDataInRemoteMySql(String content){

        ArrayList<NameValuePair>params = new ArrayList<>();
        params.add(new BasicNameValuePair("readerid", loginId));
        params.add(new BasicNameValuePair("content", content));
        try{
            String result = DBConnector.executeQuery("",Config.FRIENDCONTENT_DELETE_URL, params);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //判斷是否已收藏過內容
    private void  differentiateFriendCondition(ArrayList<CollectData>collectDataArrayList, FriendContentData friendContentData){
        for (int i = 0; i < collectDataArrayList.size(); i++) {
            if (collectDataArrayList.get(i).getCollectId().equals(friendContentData.getOwnerId())) {
                if (collectDataArrayList.get(i).getCollectContent().equals(friendContentData.getContent())) {
                    collectFriendContentImg.setImageResource(R.drawable.agenda_color);
                    isCollectClickArrayList.add(false);
                }
            }else{
                isCollectClickArrayList.add(true);
            }
        }
    }

    private void bombEffect(int position){
        ImageView showFriendContentEffectImg = (ImageView)ultraViewPagerView.findViewWithTag("showFriendContentEffectImg" + position);
        showFriendContentEffectImg.setImageDrawable(null);
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.bombeffect);
        showFriendContentEffectImg.setImageResource(R.drawable.animation_list_boom);

        //圖片大小
        myimageviewsize(showFriendContentEffectImg, (int) (screenWidth / 1.7), (int) (screenHeight / 1.7));


        showFriendContentEffectImg.clearAnimation();
        ((AnimationDrawable)(showFriendContentEffectImg.getDrawable())).stop();

        // 重新将Frame動畫设置到第-1位，也就是重新開始
        ((AnimationDrawable)(showFriendContentEffectImg.getDrawable())).selectDrawable(0);


        ((AnimationDrawable)(showFriendContentEffectImg.getDrawable())).start();
        showFriendContentEffectImg.startAnimation(animation);
        animation.setFillAfter(true);
        animation.setAnimationListener(new effectListener());
    }

    private void heartEffect(int position){
        RelativeLayout relativeLayout = (RelativeLayout)ultraViewPagerView.findViewWithTag("relativelayout"+ position);
        for(int i = 0 ; i < 55 ; i++){
            playheart(relativeLayout, screenWidth, screenHeight, i);
        }
    }

    private void bubbleEffect(int position){
        final BubbleView bubbleView = new BubbleView(context, screenWidth, screenHeight);
        final RelativeLayout relativeLayout = (RelativeLayout)ultraViewPagerView.findViewWithTag("relativelayout"+ position);
        relativeLayout.addView(bubbleView);
        CountDownTimer countDownTimer = new CountDownTimer(6*1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                relativeLayout.removeView(bubbleView);
                selectFriendContentEffectImg.setEnabled(true);
                ultraViewPagerView.setScroll(true);
            }
        };
        countDownTimer.start();
    }

    //處理動畫的圖片大小
    private void myimageviewsize(ImageView imgid, int evenWidth, int evenHight) {
        // TODO 自動產生的方法 Stub
        ViewGroup.LayoutParams params = imgid.getLayoutParams();  //需import android.view.ViewGroup.LayoutParams;
        params.width = evenWidth;
        params.height = evenHight;
        imgid.setLayoutParams(params);
    }

    //處理炸彈動畫的監聽器
    private class effectListener implements Animation.AnimationListener {

        @Override
        public void onAnimationEnd(Animation arg0) {
            // TODO Auto-generated method stub
            selectFriendContentEffectImg.setEnabled(true);
            ultraViewPagerView.setScroll(true);
        }

        @Override
        public void onAnimationRepeat(Animation arg0) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onAnimationStart(Animation arg0) {
            // TODO Auto-generated method stub
        }
    }



    //播放愛心動畫
    private void playheart(RelativeLayout effectMessageRelativeLayout, int ScreenWidth, int ScreenHeight, int count) {
        RelativeLayout.LayoutParams mDrawableLp = new RelativeLayout.LayoutParams((ScreenHeight/6), (ScreenHeight/6));
        mDrawableLp.addRule(RelativeLayout.CENTER_HORIZONTAL,
                RelativeLayout.TRUE);
        mDrawableLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                RelativeLayout.TRUE);
        Drawable[] mDrawables = new Drawable[4];
        Drawable mDrawablePink = ContextCompat.getDrawable(context.getApplicationContext(), R.drawable.love1);
        Drawable mDrawableBlue = ContextCompat.getDrawable(context.getApplicationContext(), R.drawable.love2);
        Drawable mDrawableGreen =ContextCompat.getDrawable(context.getApplicationContext(), R.drawable.love3);
        Drawable mDrawableRed = ContextCompat.getDrawable(context.getApplicationContext(), R.drawable.love4);
        mDrawables[0] = mDrawableBlue;
        mDrawables[1] = mDrawablePink;
        mDrawables[2] = mDrawableGreen;
        mDrawables[3] = mDrawableRed;

        Random random=new Random();
        ImageView heartImg =  new ImageView(context);
        heartImg.setImageDrawable(mDrawables[random.nextInt(4)]);
        heartImg.setLayoutParams(mDrawableLp);
        effectMessageRelativeLayout.addView(heartImg);

        ObjectAnimator alpha = ObjectAnimator.ofFloat(heartImg, "alpha", 0.2f, 1.0f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(heartImg,View.SCALE_X, 0.0f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(heartImg,View.SCALE_Y, 0.2f, 1.0f);
        AnimatorSet set = new AnimatorSet();
        set.setDuration(800);
        set.playTogether(alpha, scaleX, scaleY);

        BezierEvaluator bezierEvaluator = new BezierEvaluator(getPointf(2, ScreenWidth, ScreenHeight), getPointf(1, ScreenWidth, ScreenHeight));
        ValueAnimator va = ValueAnimator.ofObject(bezierEvaluator, new PointF((ScreenWidth - (ScreenHeight/6.0F)) / 2,
                ScreenHeight - (ScreenHeight/4.0F)), new PointF(random.nextInt(ScreenWidth/2),0));
        va.addUpdateListener(new UpdateListener(heartImg));
        va.setTarget(heartImg);

        va.setDuration(1500+random.nextInt(2000)*2);

        AnimatorSet finalSet = new AnimatorSet();
        finalSet.playSequentially(set);
        finalSet.playSequentially(set, va);
        finalSet.addListener(new HeartAnimatorlistener(heartImg, effectMessageRelativeLayout, count));
        finalSet.start();
    }

    //處理愛心動畫得到點
    private PointF getPointf(int scale, int ScreenWidth, int ScreenHeight) {
        Random random=new Random();
        PointF pointF = new PointF();
        pointF.x = random.nextInt(ScreenWidth );
        pointF.y = random.nextInt(ScreenHeight)/ scale;
        return pointF;
    }

    //愛心特效的動畫listener
    private class HeartAnimatorlistener implements Animator.AnimatorListener {
        RelativeLayout effectMessageRelativeLayout;
        int count;

        private View target;
        public HeartAnimatorlistener(View target, RelativeLayout effectMessageRelativeLayout, int count) {
            this.target = target;
            this.effectMessageRelativeLayout = effectMessageRelativeLayout;
            this.count = count;
        }

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            effectMessageRelativeLayout.removeView((target));
            if(count == 54){
                selectFriendContentEffectImg.setEnabled(true);
                ultraViewPagerView.setScroll(true);
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }

    //愛心特效的上升動畫listener
    private class UpdateListener implements ValueAnimator.AnimatorUpdateListener {

        View target;

        public UpdateListener(View target) {
            this.target = target;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            PointF pointf = (PointF) animation.getAnimatedValue();
            target.setX(pointf.x);
            target.setY(pointf.y);
            target.setAlpha(1 - animation.getAnimatedFraction());
        }
    }
}
