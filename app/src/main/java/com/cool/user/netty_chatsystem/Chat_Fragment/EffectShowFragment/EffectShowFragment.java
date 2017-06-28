package com.cool.user.netty_chatsystem.Chat_Fragment.EffectShowFragment;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
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

import com.cool.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.Chat_AnimationElement.BubbleEffect.BubbleView;
import com.cool.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.Chat_AnimationElement.HeartEffect.BezierEvaluator;
import com.cool.user.netty_chatsystem.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Random;

/**
 * Created by user on 2017/4/2.
 */

public class EffectShowFragment extends Fragment {
    private DisplayMetrics dm;
    private BubbleView bubbleView;
    RelativeLayout effetLayout;
    ImageView contentImg, thinkImg, effectImg, showEffectImg;
    int screenHeight, screenWidth;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.activity_effect_show , container, false);
        dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        bubbleView = new BubbleView(getActivity(),dm.widthPixels,dm.heightPixels);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;

        effetLayout = (RelativeLayout)rootView.findViewById(R.id.effectLayout);
        contentImg = (ImageView)rootView.findViewById(R.id.contentImg);
        thinkImg = (ImageView)rootView.findViewById(R.id.thinkImg);
        effectImg = (ImageView)rootView.findViewById(R.id.effectImg);
        showEffectImg = (ImageView)rootView.findViewById(R.id.showEffectImg);

        final DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.logo)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.logo_red)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        Bundle bundle = getArguments();
        String filePath = bundle.getString("filePath");
        final int effectMessage = bundle.getInt("effectMessage", -1);
        final String think = bundle.getString("think");

        ImageLoader.getInstance().displayImage("File:///" + filePath, contentImg, options, new SimpleImageLoadingListener() {
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

        effectImg.setEnabled(false);

        //動畫初始
        switch(effectMessage){
            case 0 :{
                bombEffect();
                break;
            }
            case 1 :{
                heartEffect();
                break;
            }
            case 2 :{
                bubbleEffect();
                break;
            }
            default:{
                Toast.makeText(getActivity(), "無特效呵呵! ", Toast.LENGTH_SHORT).show();
                effectImg.setEnabled(true);
            }
        }

        TextView backTxt = (TextView)rootView.findViewById(R.id.backTxt);
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(),"fonts/fontawesome-webfont.ttf");//設定back的按紐
        backTxt.setTypeface(font);
        backTxt.setText("\uf060");
        backTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        thinkImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getActivity(), R.style.selectorDialog);
                dialog.setContentView(R.layout.resource_think_dialog);
                TextView thinkText = (TextView) dialog.findViewById(R.id.thinkText);
                if (think.length() > 0) {
                    thinkText.setText(think);
                }
                // 由程式設定 Dialog 視窗外的明暗程度, 亮度從 0f 到 1f
                WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
                lp.dimAmount = 0.5f;
                dialog.getWindow().setAttributes(lp);
                dialog.show();
            }
        });

        effectImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                effectImg.setEnabled(false);
                switch(effectMessage){
                    case 0 :{
                        bombEffect();
                        break;
                    }
                    case 1 :{
                        heartEffect();
                        break;
                    }
                    case 2 :{
                        bubbleEffect();
                        break;
                    }
                    default:{
                        Toast.makeText(getActivity(), "無特效呵呵", Toast.LENGTH_SHORT).show();
                        effectImg.setEnabled(true);
                    }
                }
            }
        });

        return rootView;
    }

    //炸彈特效
    private void bombEffect(){
        showEffectImg.setImageDrawable(null);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.bombeffect);
        showEffectImg.setImageResource(R.drawable.animation_list_boom);


        //圖片大小
        myimageviewsize(showEffectImg, (int) (screenHeight / 1.7), (int) (screenHeight / 1.7));


        showEffectImg.clearAnimation();
        ((AnimationDrawable)(showEffectImg.getDrawable())).stop();

        // 重新将Frame動畫设置到第-1位，也就是重新開始
        ((AnimationDrawable)(showEffectImg.getDrawable())).selectDrawable(0);


        ((AnimationDrawable)(showEffectImg.getDrawable())).start();
        showEffectImg.startAnimation(animation);
        animation.setFillAfter(true);
        animation.setAnimationListener(new effectListener());
    }

    private void heartEffect(){
        for(int i = 0 ; i < 55 ; i++){
            playheart(i);
        }
    }

    private void bubbleEffect(){
        final BubbleView bubbleView = new BubbleView(getActivity(), screenWidth, screenHeight);
        effetLayout.addView(bubbleView);
        CountDownTimer countDownTimer = new CountDownTimer(6*1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                effetLayout.removeView(bubbleView);
                effectImg.setEnabled(true);
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
            effectImg.setEnabled(true);
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
    private void playheart(int count) {
        RelativeLayout.LayoutParams mDrawableLp = new RelativeLayout.LayoutParams((screenHeight/6), (screenHeight/6));
        mDrawableLp.addRule(RelativeLayout.CENTER_HORIZONTAL,
                RelativeLayout.TRUE);
        mDrawableLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                RelativeLayout.TRUE);
        Drawable[] mDrawables = new Drawable[4];
        Drawable mDrawablePink = ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.love1);
        Drawable mDrawableBlue = ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.love2);
        Drawable mDrawableGreen =ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.love3);
        Drawable mDrawableRed = ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.love4);
        mDrawables[0] = mDrawableBlue;
        mDrawables[1] = mDrawablePink;
        mDrawables[2] = mDrawableGreen;
        mDrawables[3] = mDrawableRed;

        Random random=new Random();
        ImageView heartImg =  new ImageView(getActivity());
        heartImg.setImageDrawable(mDrawables[random.nextInt(4)]);
        heartImg.setLayoutParams(mDrawableLp);
        effetLayout.addView(heartImg);

        ObjectAnimator alpha = ObjectAnimator.ofFloat(heartImg, "alpha", 0.2f, 1.0f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(heartImg,View.SCALE_X, 0.0f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(heartImg,View.SCALE_Y, 0.2f, 1.0f);
        AnimatorSet set = new AnimatorSet();
        set.setDuration(800);
        set.playTogether(alpha, scaleX, scaleY);

        BezierEvaluator bezierEvaluator = new BezierEvaluator(getPointf(2, screenWidth, screenHeight), getPointf(1, screenWidth, screenHeight));
        ValueAnimator va = ValueAnimator.ofObject(bezierEvaluator, new PointF((screenWidth - (screenHeight/6.0F)) / 2,
                screenHeight - (screenHeight/4.0F)), new PointF(random.nextInt(screenWidth/2),0));
        va.addUpdateListener(new UpdateListener(heartImg));
        va.setTarget(heartImg);

        va.setDuration(1500+random.nextInt(2000)*2);

        AnimatorSet finalSet = new AnimatorSet();
        finalSet.playSequentially(set);
        finalSet.playSequentially(set, va);
        finalSet.addListener(new HeartAnimatorlistener(heartImg, count));
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

        private View target;
        private int count;
        public HeartAnimatorlistener(View target, int count) {
            this.target = target;
            this.count = count;
        }

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            effetLayout.removeView((target));
            if(count == 54){
                effectImg.setEnabled(true);
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
