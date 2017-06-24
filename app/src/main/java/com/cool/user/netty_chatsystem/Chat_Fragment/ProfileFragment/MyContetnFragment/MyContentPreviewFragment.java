package com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.MyContetnFragment;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cool.user.netty_chatsystem.Chat_Client.handler.Client_UserHandler;
import com.cool.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.Chat_AnimationElement.BubbleEffect.BubbleView;
import com.cool.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.Chat_AnimationElement.HeartEffect.BezierEvaluator;
import com.cool.user.netty_chatsystem.Chat_MySQL.Config;
import com.cool.user.netty_chatsystem.Chat_MySQL.DBConnector;
import com.cool.user.netty_chatsystem.Chat_Sharepreference.SharePreferenceManager;
import com.cool.user.netty_chatsystem.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by user on 2017/3/1.
 */
public class MyContentPreviewFragment extends Fragment {
    int position;
    ImageView showMyContentEffectImg, myContentEffectImg, nextContentImg;
    RelativeLayout myContentPreviewRelativeLayout;
    int ScreenHeight;
    int ScreenWidth;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater ,ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_mycontentpreview , container, false);
        myContentPreviewRelativeLayout = (RelativeLayout)rootView.findViewById(R.id.myContentPreviewRelativeLayout);
        TextView backTxt = (TextView)rootView.findViewById(R.id.backTxt);
        final ImageView myContentPreviewImg = (ImageView)rootView.findViewById(R.id.myContentPreviewImg);
        final ImageView deleteContentImg = (ImageView)rootView.findViewById(R.id.deleteMyContentImg);
        nextContentImg = (ImageView)rootView.findViewById(R.id.nextMyContentImg);
        ImageView myContentThinkImg = (ImageView)rootView.findViewById(R.id.myContentThinkImg);
        myContentEffectImg = (ImageView)rootView.findViewById(R.id.myContentEffectImg);
        ImageView unReadImg = (ImageView)rootView.findViewById(R.id.unReadImg);
        showMyContentEffectImg = (ImageView)rootView.findViewById(R.id.showMyContentEffectImg);

        //螢幕的寬高
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        ScreenHeight = dm.heightPixels;
        ScreenWidth = dm.widthPixels;

        //StrictMode 最主要的觀念就是主執行緒(對岸稱 主線程) 應只專注於處理 UI 而已，其他的工作都應另建 thread 去達成。----
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());
        //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------

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
        position = bundle.getInt("position");
        final String[] contentUrlArray = bundle.getStringArray("contentUrlArray");
        final ArrayList<MyContentData>myContentDataArrayList = bundle.getParcelableArrayList("myContentDataArrayList");
        final ArrayList<String> contentUrlArrayList = new ArrayList<>();
        for(int i = 0 ; i < contentUrlArray.length ; i++){
            contentUrlArrayList.add(contentUrlArray[i]);
        }


        SharePreferenceManager sharePreferenceManager = new SharePreferenceManager(getActivity());
        final String loginId = sharePreferenceManager.getLoginId();

        Typeface font = Typeface.createFromAsset(getActivity().getAssets(),"fonts/fontawesome-webfont.ttf");//設定back的按紐
        backTxt.setTypeface(font);
        backTxt.setText("\uf060");
        backTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0){
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });

        unReadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getActivity(),R.style.selectorDialog);
                dialog.setContentView(R.layout.resource_mycontent_unread_dialog);
                ListView unReadListView = (ListView)dialog.findViewById(R.id.unReadListView);
                ArrayList<String>unReadArrayList = loadUnRead(loginId, myContentDataArrayList.get(position).getContent());
                UnReadAdapter unReadAdapter = new UnReadAdapter(getActivity(), unReadArrayList);
                unReadListView.setAdapter(unReadAdapter);

                // 由程式設定 Dialog 視窗外的明暗程度, 亮度從 0f 到 1f
                WindowManager.LayoutParams lp=dialog.getWindow().getAttributes();
                lp.dimAmount=0.2f;
                dialog.getWindow().setAttributes(lp);
                dialog.show();
            }
        });

        deleteContentImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog deleteDialog = new Dialog(getActivity(), R.style.selectorDialog);
                deleteDialog.setContentView(R.layout.resource_wordandbutton_dialog);
                deleteDialog.show();
                ImageView yesImg = (ImageView)deleteDialog.findViewById(R.id.yesImg);
                ImageView noImg = (ImageView)deleteDialog.findViewById(R.id.noImg);
                TextView descriptionTxv = (TextView)deleteDialog.findViewById(R.id.descriptionTxv);
                descriptionTxv.setText("刪除驚喜?");
                yesImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(Client_UserHandler.getConnection() !=null) {
                            new deleteMyContentInRemoteMySql(loginId, myContentDataArrayList.get(position).getContent()).execute();
                            if (position == 0) {
                                System.out.println("position 0" + position);
                                contentUrlArrayList.remove(position);
                                myContentDataArrayList.remove(position);
                            } else if ((position + 1) != myContentDataArrayList.size()) {
                                System.out.println("position + 1 " + position);
                                contentUrlArrayList.remove(position);
                                myContentDataArrayList.remove(position);
                                position++;
                                System.out.println("after position + 1 " + position);

                            } else {
                                System.out.println("其他 " + position);
                                contentUrlArrayList.remove(position);
                                myContentDataArrayList.remove(position);
                                position--;
                                System.out.println("after 其他 " + position);
                            }

                            if(position < contentUrlArrayList.size()) {
                                ImageLoader.getInstance()
                                        .displayImage(contentUrlArrayList.get(position), myContentPreviewImg, options, new SimpleImageLoadingListener() {
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
                                getActivity().getSupportFragmentManager().popBackStack();
                            }

                            deleteDialog.dismiss();
                        }else{
                            Toast.makeText(getActivity(), "無法刪除驚喜，請確認離線狀態", Toast.LENGTH_SHORT).show();
                        }
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

        nextContentImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position < contentUrlArrayList.size()) {
                    if((position + 1) != contentUrlArrayList.size()){
                        position++;
                    }else if(position + 1 == contentUrlArrayList.size()){
                        getActivity().getSupportFragmentManager().popBackStack();
                    }

                    if(position < contentUrlArrayList.size()) {
                        ImageLoader.getInstance()
                                .displayImage(contentUrlArrayList.get(position), myContentPreviewImg, options, new SimpleImageLoadingListener() {
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
                    }
                }
            }
        });

        myContentThinkImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getActivity(), R.style.selectorDialog);
                dialog.setContentView(R.layout.resource_think_dialog);
                TextView thinkText = (TextView) dialog.findViewById(R.id.thinkText);
                if (myContentDataArrayList.get(position).getThink().length() > 0) {
                    thinkText.setText(myContentDataArrayList.get(position).getThink());
                }

                // 由程式設定 Dialog 視窗外的明暗程度, 亮度從 0f 到 1f
                WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
                lp.dimAmount = 0.5f;
                dialog.getWindow().setAttributes(lp);
                dialog.show();
            }
        });

        myContentEffectImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myContentEffectImg.setEnabled(false);
                nextContentImg.setEnabled(false);

                switch(myContentDataArrayList.get(position).getEffect()){
                    case 0: bombEffect();
                        break;
                    case 1: heartEffect();
                        break;
                    case 2: bubbleEffect();
                        break;
                    default:
                        Toast.makeText(getActivity(), "無特效呵呵!", Toast.LENGTH_SHORT).show();
                        myContentEffectImg.setEnabled(true);
                        nextContentImg.setEnabled(true);
                        break;
                }
            }
        });

        ImageLoader.getInstance()
                .displayImage(contentUrlArrayList.get(position), myContentPreviewImg, options, new SimpleImageLoadingListener() {
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

        return rootView;
    }

    //讀取未讀朋友名單
    private ArrayList<String>loadUnRead(String loginId, String content){
        ArrayList<String> unReadArrayList = new ArrayList<>();
        if(Client_UserHandler.getConnection() != null) {
            try {
                ArrayList<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("ownerid", loginId));
                params.add(new BasicNameValuePair("content", content));
                String result = DBConnector.executeQuery("", Config.MYCONTENT_UNREAD_LOAD_URL, params);
            /*
                      SQL 結果有多筆資料時使用JSONArray
                      只有一筆資料時直接建立JSONObject物件
                      JSONObject jsonData = new JSONObject(result);
                      */
                if (!result.equals("\"\"\n")) {
                    JSONArray jsonArray = new JSONArray(result);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        unReadArrayList.add(jsonObject.getString("nickname"));
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return unReadArrayList;
    }

    class UnReadAdapter extends BaseAdapter {

        private ArrayList<String> unReadArrayList;

        private LayoutInflater inflater;


        UnReadAdapter(Context context, ArrayList<String> unReadArrayList) {
            inflater = LayoutInflater.from(context);
            this.unReadArrayList = unReadArrayList;
        }

        @Override
        public int getCount() {
            return unReadArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            View view = convertView;
            if (view == null) {
                view = inflater.inflate(R.layout.resource_unread_listview_dialog , parent, false);
                holder = new ViewHolder();
                assert view != null;
                holder.unReadText = (TextView)view.findViewById(R.id.unReadText);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.unReadText.setText(unReadArrayList.get(position));

            return view;
        }


        class ViewHolder {
            TextView unReadText;
        }
    }


    //刪除遠端的sticker data
    class deleteMyContentInRemoteMySql extends AsyncTask<String, String, Void>
    {

        String content;
        String loginId;
        InputStream is = null ;
        public deleteMyContentInRemoteMySql(String loginId, String content){
            this.loginId = loginId;
            this.content = content;
        }

        protected void onPreExecute()
        {

        }
        @Override
        protected Void doInBackground(String... params)
        {
            try
            {
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("ownerid", loginId));
                nameValuePairs.add(new BasicNameValuePair("content", content));
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Config.RANDOMCONTENT_DELETE_URL);
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                is = entity.getContent();
            }
            catch(Exception e){
                Log.e("log_tag", "Error in http connection" + e.toString());
            }

            return null;
        }
        protected void postExecute(Void v)
        {
            Toast.makeText(getActivity(), "刪除成功", Toast.LENGTH_LONG).show();
        }
    }


    private void bombEffect(){
        showMyContentEffectImg.setImageDrawable(null);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.bombeffect);
        showMyContentEffectImg.setImageResource(R.drawable.animation_list_boom);

        //圖片大小
        myimageviewsize(showMyContentEffectImg, (int) (ScreenHeight / 1.7), (int) (ScreenHeight / 1.7));


        showMyContentEffectImg.clearAnimation();
        ((AnimationDrawable)(showMyContentEffectImg.getDrawable())).stop();

        // 重新将Frame動畫设置到第-1位，也就是重新開始
        ((AnimationDrawable)(showMyContentEffectImg.getDrawable())).selectDrawable(0);


        ((AnimationDrawable)(showMyContentEffectImg.getDrawable())).start();
        showMyContentEffectImg.startAnimation(animation);
        animation.setFillAfter(true);
        animation.setAnimationListener(new effectListener());
    }

    private void heartEffect(){
        for(int i = 0 ; i < 55 ; i++){
            playheart(myContentPreviewRelativeLayout, ScreenWidth, ScreenHeight, i);
        }
    }

    private void bubbleEffect(){
        final BubbleView bubbleView = new BubbleView(getActivity(), ScreenWidth, ScreenHeight);
        myContentPreviewRelativeLayout.addView(bubbleView);
        CountDownTimer countDownTimer = new CountDownTimer(6*1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                myContentPreviewRelativeLayout.removeView(bubbleView);
                myContentEffectImg.setEnabled(true);
                nextContentImg.setEnabled(true);
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
            myContentEffectImg.setEnabled(true);
            nextContentImg.setEnabled(true);
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
                myContentEffectImg.setEnabled(true);
                nextContentImg.setEnabled(true);
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
