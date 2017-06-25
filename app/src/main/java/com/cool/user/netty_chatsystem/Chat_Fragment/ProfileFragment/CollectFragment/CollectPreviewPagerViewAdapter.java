package com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.CollectFragment;

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
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cool.user.netty_chatsystem.Chat_Client.handler.Client_UserHandler;
import com.cool.user.netty_chatsystem.Chat_Fragment.UItraViewPager.UltraViewPager;
import com.cool.user.netty_chatsystem.Chat_Fragment.UItraViewPager.UltraViewPagerView;
import com.cool.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.Chat_AnimationElement.BubbleEffect.BubbleView;
import com.cool.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.Chat_AnimationElement.HeartEffect.BezierEvaluator;
import com.cool.user.netty_chatsystem.Chat_MySQL.Config;
import com.cool.user.netty_chatsystem.Chat_MySQL.DBConnector;
import com.cool.user.netty_chatsystem.Chat_Sqlite_ChatHistory.MyDBHelper;
import com.cool.user.netty_chatsystem.Chat_biz.entity.Friend;
import com.cool.user.netty_chatsystem.Chat_core.connetion.IMConnection;
import com.cool.user.netty_chatsystem.Chat_core.protocol.Commands;
import com.cool.user.netty_chatsystem.Chat_core.protocol.Handlers;
import com.cool.user.netty_chatsystem.Chat_core.transport.Header;
import com.cool.user.netty_chatsystem.Chat_core.transport.IMResponse;
import com.cool.user.netty_chatsystem.Chat_server.dto.FriendDTO;
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
    UltraViewPagerView ultraViewPager;

    RelativeLayout relativeLayout;
    ImageView collectPreViewImg ;
    TextView backTxt ;
    ImageView deleteCollectImg;
    ImageView collectThinkImg ;
    ImageView collectEffectImg ;
    ImageView collectAddFriendImg;
    ImageView showCollectEffectImg;
    ImageView collectProfileImg;
    TextView collectNameText;


    public CollectPreviewPagerViewAdapter(boolean isMultiScr) {
        this.isMultiScr = isMultiScr;
    }

    public CollectPreviewPagerViewAdapter(String loginId, int screenWidth, int screenHeight,
                                          ArrayList<String> collectUrlArrayList, ArrayList<CollectData>collectDataArrayList, ArrayList<String> friendArrayList,
                                          Context context, UltraViewPagerView ultraViewPager){
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
        this.ultraViewPager = ultraViewPager;
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
    public int getItemPosition(Object object){
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        relativeLayout = (RelativeLayout) LayoutInflater.from(container.getContext()).inflate(R.layout.resource_collectpreview_pagerview, null);
        collectPreViewImg = (ImageView)relativeLayout.findViewById(R.id.collectPreViewImg);
        backTxt = (TextView)relativeLayout.findViewById(R.id.backTxt);
        deleteCollectImg = (ImageView)relativeLayout.findViewById(R.id.deleteCollectImg);
        collectThinkImg = (ImageView)relativeLayout.findViewById(R.id.collectThinkImg);
        collectEffectImg = (ImageView)relativeLayout.findViewById(R.id.collectEffectImg);
        collectAddFriendImg = (ImageView)relativeLayout.findViewById(R.id.collectAddFriendImg);
        showCollectEffectImg = (ImageView)relativeLayout.findViewById(R.id.showCollectEffectImg);
        collectProfileImg = (ImageView)relativeLayout.findViewById(R.id.collectProfileImg);
        collectNameText = (TextView)relativeLayout.findViewById(R.id.randomNameText);
        container.addView(relativeLayout);

        if(differentiateFriendCondition(friendArrayList, collectDataArrayList.get(position))){
            collectAddFriendImg.setImageResource(R.drawable.logo);
        }

        backTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)context).getSupportFragmentManager().popBackStack();
            }
        });

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

        collectEffectImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectEffectImg.setEnabled(false);
                ultraViewPager.setScroll(false);

                switch (collectDataArrayList.get(position).getCollectEffect()) {
                    case 0:
                        bombEffect();
                        break;
                    case 1:
                        heartEffect();
                        break;
                    case 2:
                        bubbleEffect();
                        break;
                    default:{
                        Toast.makeText(context, "無特效呵呵!", Toast.LENGTH_SHORT).show();
                        collectEffectImg.setEnabled(true);
                        ultraViewPager.setScroll(true);
                    }
                }
            }
        });

        deleteCollectImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog deleteDialog = new Dialog(context, R.style.selectorDialog);
                deleteDialog.setContentView(R.layout.resource_wordandbutton_dialog);
                deleteDialog.show();
                ImageView yesImg = (ImageView)deleteDialog.findViewById(R.id.yesImg);
                ImageView noImg = (ImageView)deleteDialog.findViewById(R.id.noImg);
                TextView descriptionTxv = (TextView)deleteDialog.findViewById(R.id.descriptionTxv);
                descriptionTxv.setText("刪除收藏?");
                yesImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(position < collectDataArrayList.size()) {
                            //deleteCollectContentInSqlite(collectDataArrayList.get(position).getCollectId(), collectDataArrayList.get(position).getCollectContent());
                            //deleteCollectContentInRemoteMySql(collectDataArrayList.get(position).getCollectId(), collectDataArrayList.get(position).getCollectContent());

                            if (position == 0) {
                                System.out.println("position 0" + position);
                                collectDataArrayList.remove(position);
                                collectUrlArrayList.remove(position);
                                if(collectDataArrayList.isEmpty()){
                                    //getActivity().getSupportFragmentManager().popBackStack();
                                }
                            }else if ((position + 1) != collectUrlArrayList.size()) {
                                System.out.println("position + 1 " + position);
                                collectDataArrayList.remove(position);
                                collectUrlArrayList.remove(position);


                            } else {
                                System.out.println("其他 " + position);
                                collectDataArrayList.remove(position);
                                collectUrlArrayList.remove(position);

                            }

                            /*if (position < collectDataArrayList.size()) {
                                collectNameText.setText(collectDataArrayList.get(position).getCollectNickName());
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

                            }*/
                        }else{
                            //getActivity().getSupportFragmentManager().popBackStack();
                        }
                        deleteDialog.dismiss();
                        ultraViewPager.getAdapter().notifyDataSetChanged();
                    }
                });

                noImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteDialog.dismiss();
                    }
                });

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

    private void deleteCollectContentInSqlite(String collectId, String content){
        //創建MyDBHelper對象
        MyDBHelper dbHelper = new MyDBHelper(context , "Chat.db", null, 1);
        //得到一個可讀的SQLiteDatabase對象
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        db.delete("Collect", "id=" + "\"" + collectId + "\"" + " and " + "content = " + "\"" + content + "\"" , null);

        //關閉數據庫
        db.close();
    }

    private void deleteCollectContentInRemoteMySql(String ownerid, String content){
        try {
            ArrayList<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("collectid", loginId));
            params.add(new BasicNameValuePair("ownerid", ownerid));
            params.add(new BasicNameValuePair("content", content));
            String result = DBConnector.executeQuery("", Config.COLLECT_DELETE_URL, params);
            /*
                      SQL 結果有多筆資料時使用JSONArray
                      只有一筆資料時直接建立JSONObject物件
                      JSONObject jsonData = new JSONObject(result);
                      */
            //JSONArray jsonArray = new JSONArray(result);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void bombEffect(){
        showCollectEffectImg.setImageDrawable(null);
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.bombeffect);
        showCollectEffectImg.setImageResource(R.drawable.animation_list_boom);

        //圖片大小
        myimageviewsize(showCollectEffectImg, (int) (screenWidth / 1.7), (int) (screenHeight / 1.7));


        showCollectEffectImg.clearAnimation();
        ((AnimationDrawable)(showCollectEffectImg.getDrawable())).stop();

        // 重新将Frame動畫设置到第-1位，也就是重新開始
        ((AnimationDrawable)(showCollectEffectImg.getDrawable())).selectDrawable(0);


        ((AnimationDrawable)(showCollectEffectImg.getDrawable())).start();
        showCollectEffectImg.startAnimation(animation);
        animation.setFillAfter(true);
        animation.setAnimationListener(new effectListener());
    }

    private void heartEffect(){
        for(int i = 0 ; i < 55 ; i++){
            playheart(relativeLayout, screenWidth, screenHeight, i);
        }
    }

    private void bubbleEffect(){
        final BubbleView bubbleView = new BubbleView(context, screenWidth, screenHeight);
        relativeLayout.addView(bubbleView);
        CountDownTimer countDownTimer = new CountDownTimer(6*1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                relativeLayout.removeView(bubbleView);
                collectEffectImg.setEnabled(true);
                ultraViewPager.setScroll(true);
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
            collectEffectImg.setEnabled(true);
            ultraViewPager.setScroll(true);

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
                collectEffectImg.setEnabled(true);
                ultraViewPager.setScroll(true);
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
