package com.cool.user.netty_chatsystem.Chat_Fragment.CameraFragment;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cool.user.netty_chatsystem.Chat_Animation.BrokenEffect.BrokenAnimator;
import com.cool.user.netty_chatsystem.Chat_Animation.BrokenEffect.BrokenCallback;
import com.cool.user.netty_chatsystem.Chat_Animation.BrokenEffect.BrokenConfig;
import com.cool.user.netty_chatsystem.Chat_Fragment.CameraFragment.internal.enums.MediaAction;
import com.cool.user.netty_chatsystem.Chat_Fragment.CameraFragment.internal.ui.view.AspectFrameLayout;
import com.cool.user.netty_chatsystem.Chat_Fragment.CameraFragment.internal.utils.ImageLoader;
import com.cool.user.netty_chatsystem.Chat_Fragment.CameraFragment.internal.utils.Utils;
import com.cool.user.netty_chatsystem.Chat_Fragment.SendContentFragment.SendContentFragment;
import com.cool.user.netty_chatsystem.Chat_Animation.BrokenEffect.BrokenTouchListener;
import com.cool.user.netty_chatsystem.Chat_Animation.BrokenEffect.BrokenView;
import com.cool.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.Chat_AnimationElement.BubbleEffect.BubbleView;
import com.cool.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.Chat_AnimationElement.HeartEffect.BezierEvaluator;
import com.cool.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.adapter.SnappyRecycleView;
import com.cool.user.netty_chatsystem.Chat_MySQL.Config;
import com.cool.user.netty_chatsystem.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by user on 2017/3/2.
 */
public class PreviewFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "PreviewActivity";

    public static final int ACTION_CONFIRM = 900;
    public static final int ACTION_RETAKE = 901;
    public static final int ACTION_CANCEL = 902;

    private final static String MEDIA_ACTION_ARG = "media_action_arg";
    private final static String FILE_PATH_ARG = "file_path_arg";
    private final static String RESPONSE_CODE_ARG = "response_code_arg";
    private final static String VIDEO_POSITION_ARG = "current_video_position";
    private final static String VIDEO_IS_PLAYED_ARG = "is_played";
    private final static String MIME_TYPE_VIDEO = "video";
    private final static String MIME_TYPE_IMAGE = "image";

    private int mediaAction;
    private String previewFilePath;

    private SurfaceView surfaceView;
    private FrameLayout photoPreviewContainer;
    private ImageView imagePreview, selectEffectImg;
    private AspectFrameLayout videoPreviewContainer;

    private MediaController mediaController;
    private MediaPlayer mediaPlayer;

    private int currentPlaybackPosition = 0;
    private boolean isVideoPlaying = true;

    int effectMessage = -1;//選擇訊息特效
    boolean enableEffect = true;
    HorizontalAdapter horizontalAdapter;//訊息特效列表
    int whichFragment;
    EditText  shareSurpriseEdit;
    byte[] bitmapBytes;



    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater ,ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.activity_preview, container, false);

        surfaceView = (SurfaceView) rootView.findViewById(R.id.video_preview);
        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mediaController == null) return false;
                if (mediaController.isShowing()) {
                    mediaController.hide();
                } else {
                    mediaController.show();
                }
                return false;
            }
        });

        videoPreviewContainer = (AspectFrameLayout) rootView.findViewById(R.id.previewAspectFrameLayout);
        photoPreviewContainer = (FrameLayout) rootView.findViewById(R.id.photo_preview_container);

        ImageView cancelImg = (ImageView)rootView.findViewById(R.id.cancelImg);
        ImageView sendImg = (ImageView)rootView.findViewById(R.id.sendImg);
        ImageView saveImg = (ImageView)rootView.findViewById(R.id.saveImg);
        selectEffectImg = (ImageView)rootView.findViewById(R.id.selectEffectImg);
        shareSurpriseEdit = (EditText)rootView.findViewById(R.id.shareSurpriseEdit);

        cancelImg.setOnClickListener(this);
        sendImg.setOnClickListener(this);
        saveImg.setOnClickListener(this);
        selectEffectImg.setOnClickListener(this);
        shareSurpriseEdit.setOnClickListener(this);

        Bundle args = getArguments();

        mediaAction = args.getInt(MEDIA_ACTION_ARG);
        previewFilePath = args.getString(FILE_PATH_ARG);
        whichFragment = args.getInt("whichFragment");
        if(mediaAction == MediaAction.ACTION_PHOTO){
            bitmapBytes = args.getByteArray("bitmapBytes");
        }

        if (mediaAction == Configuration.MEDIA_ACTION_VIDEO) {
            displayVideo(savedInstanceState);
        } else if (mediaAction == Configuration.MEDIA_ACTION_PHOTO) {
            displayImage();
        } else {
            String mimeType = Utils.getMimeType(previewFilePath);
            if (mimeType.contains(MIME_TYPE_VIDEO)) {
                displayVideo(savedInstanceState);
            } else if (mimeType.contains(MIME_TYPE_IMAGE)) {
                displayImage();
            }
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveVideoParams(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (mediaController != null) {
            mediaController.hide();
            mediaController = null;
        }
    }

    private void displayImage() {
        videoPreviewContainer.setVisibility(View.GONE);
        surfaceView.setVisibility(View.GONE);
        showImagePreview();
    }

    private void showImagePreview() {
        imagePreview = new ImageView(getActivity());
        ImageLoader.Builder builder = new ImageLoader.Builder(getActivity());
        builder.load(previewFilePath).build().into(imagePreview);
        photoPreviewContainer.removeAllViews();
        photoPreviewContainer.addView(imagePreview);
    }

    private void displayVideo(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            loadVideoParams(savedInstanceState);
        }
        photoPreviewContainer.setVisibility(View.GONE);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                showVideoPreview(holder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
    }

    private void showVideoPreview(SurfaceHolder holder) {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(previewFilePath);
            mediaPlayer.setDisplay(holder);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaController = new MediaController(getActivity());
                    mediaController.setAnchorView(surfaceView);
                    mediaController.setMediaPlayer(new MediaController.MediaPlayerControl() {
                        @Override
                        public void start() {
                            mediaPlayer.start();
                        }

                        @Override
                        public void pause() {
                            mediaPlayer.pause();
                        }

                        @Override
                        public int getDuration() {
                            return mediaPlayer.getDuration();
                        }

                        @Override
                        public int getCurrentPosition() {
                            return mediaPlayer.getCurrentPosition();
                        }

                        @Override
                        public void seekTo(int pos) {
                            mediaPlayer.seekTo(pos);
                        }

                        @Override
                        public boolean isPlaying() {
                            return mediaPlayer.isPlaying();
                        }

                        @Override
                        public int getBufferPercentage() {
                            return 0;
                        }

                        @Override
                        public boolean canPause() {
                            return true;
                        }

                        @Override
                        public boolean canSeekBackward() {
                            return true;
                        }

                        @Override
                        public boolean canSeekForward() {
                            return true;
                        }

                        @Override
                        public int getAudioSessionId() {
                            return mediaPlayer.getAudioSessionId();
                        }
                    });

                    int videoWidth = mp.getVideoWidth();
                    int videoHeight = mp.getVideoHeight();

                    videoPreviewContainer.setAspectRatio((double) videoWidth / videoHeight);

                    mediaPlayer.start();
                    mediaPlayer.seekTo(currentPlaybackPosition);

                    if (!isVideoPlaying)
                        mediaPlayer.pause();
                }
            });
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    return true;
                }
            });
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            Log.e(TAG, "Error media player playing video.");
        }
    }

    private void saveVideoParams(Bundle outState) {
        if (mediaPlayer != null) {
            outState.putInt(VIDEO_POSITION_ARG, mediaPlayer.getCurrentPosition());
            outState.putBoolean(VIDEO_IS_PLAYED_ARG, mediaPlayer.isPlaying());
        }
    }

    private void loadVideoParams(Bundle savedInstanceState) {
        currentPlaybackPosition = savedInstanceState.getInt(VIDEO_POSITION_ARG, 0);
        isVideoPlaying = savedInstanceState.getBoolean(VIDEO_IS_PLAYED_ARG, true);
    }

    @Override
    public void onClick(View view) {
        Intent resultIntent = new Intent();
        if (view.getId() == R.id.cancelImg) {
            deleteMediaFile();
            if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0){
                getActivity().getSupportFragmentManager().popBackStack();
            }
        } else if (view.getId() == R.id.sendImg) {
            //deleteMediaFile();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.addToBackStack(null);
            Bundle bundle = new Bundle();
            bundle.putByteArray("bitmapBytes", bitmapBytes);
            bundle.putInt("effectMessage", effectMessage);
            bundle.putString("thinking", shareSurpriseEdit.getText().toString());
            bundle.putString("previewFilePath", previewFilePath);
            bundle.putInt("whichFragment", whichFragment);
            SendContentFragment sendContentFragment = new SendContentFragment();
            sendContentFragment.setArguments(bundle);
            fragmentTransaction.replace(whichFragment, sendContentFragment);
            fragmentTransaction.commit();

        } else if (view.getId() == R.id.saveImg) {
            deleteMediaFile();
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), Config.IMAGE_DIRECTORY_NAME);
            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d("sendContentFragment", "Oops! Failed create " + Config.IMAGE_DIRECTORY_NAME + " directory");
                }
            }

            File file;
            file = new File(mediaStorageDir.getPath() + File.separator + "WowChat_" + timeStamp + ".jpg");

            final Bitmap bitmap = ((BitmapDrawable)imagePreview.getDrawable()).getBitmap();

            try{
                FileOutputStream fos = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);//Bitmap類別的compress方法產生檔案
                bos.flush();
                bos.close();
            }catch (Exception e){
                e.printStackTrace();
            }

            Toast.makeText(getActivity(), "儲存成功", Toast.LENGTH_SHORT).show();


        }else if (view.getId() == R.id.selectEffectImg){
            enableEffect = true;
            final Dialog dialog = new Dialog(getActivity(), R.style.selectorDialog);
            dialog.setContentView(R.layout.resource_effectmessage_dialog);
            ImageView showEffectMessageImg = (ImageView)dialog.findViewById(R.id.showEffectMessageImg);
            SnappyRecycleView snappyRecycleView = (SnappyRecycleView)dialog.findViewById(R.id.snappyRecycleView);
            RelativeLayout effectMessageRelativeLayout = (RelativeLayout)dialog.findViewById(R.id.effectMessageRelativeLayout);
            ImageView backImg = (ImageView)dialog.findViewById(R.id.backImg);
            backImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            ArrayList<Integer> horizontalList=new ArrayList<>();
            horizontalList.add(R.drawable.boxempty);
            horizontalList.add(R.drawable.bomb);
            horizontalList.add(R.drawable.love1);
            horizontalList.add(R.drawable.bubble);
            //horizontalList.add(R.drawable.smile);
            int spanCount = horizontalList.size(); // 3 columns
            int spacing = 20; // 50px
            boolean includeEdge = false;
            snappyRecycleView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
            horizontalAdapter = new HorizontalAdapter(horizontalList, showEffectMessageImg, effectMessageRelativeLayout);
            LinearLayoutManager horizontalLayoutManagaer
                    = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            snappyRecycleView.setLayoutManager(horizontalLayoutManagaer);
            snappyRecycleView.setAdapter(horizontalAdapter);
            // 由程式設定 Dialog 視窗外的明暗程度, 亮度從 0f 到 1f
            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            lp.dimAmount = 0.0f;
            dialog.getWindow().setAttributes(lp);
            dialog.show();
        }else if (view.getId() == R.id.shareSurpriseEdit){

        }
    }


    private boolean deleteMediaFile() {
        File mediaFile = new File(previewFilePath);
        return mediaFile.delete();
    }

    public static String getMediaFilePatch(@NonNull Intent resultIntent) {
        return resultIntent.getStringExtra(FILE_PATH_ARG);
    }

    public static boolean isResultConfirm(@NonNull Intent resultIntent) {
        return ACTION_CONFIRM == resultIntent.getIntExtra(RESPONSE_CODE_ARG, -1);
    }

    public static boolean isResultRetake(@NonNull Intent resultIntent) {
        return ACTION_RETAKE == resultIntent.getIntExtra(RESPONSE_CODE_ARG, -1);
    }

    public static boolean isResultCancel(@NonNull Intent resultIntent) {
        return ACTION_CANCEL == resultIntent.getIntExtra(RESPONSE_CODE_ARG, -1);
    }

    //訊息特效的dialog所需要的recycle adapter
    class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.MyViewHolder> {

        private List<Integer> horizontalList;
        private ImageView showEffectMessageImg;
        private RelativeLayout effectMessageRelativeLayout;
        private int selectPosition;

        class MyViewHolder extends RecyclerView.ViewHolder {
            public ImageView effectMessageItemImg;

            public MyViewHolder(View view) {
                super(view);
                effectMessageItemImg = (ImageView) view.findViewById(R.id.effectMessageItemImg);

            }
        }


        public HorizontalAdapter(List<Integer> horizontalList, ImageView showEffectMessageImg, RelativeLayout effectMessageRelativeLayout) {
            this.horizontalList = horizontalList;
            this.showEffectMessageImg = showEffectMessageImg;
            this.effectMessageRelativeLayout = effectMessageRelativeLayout;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.resourcee_whiteboard_item_recycleview, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.effectMessageItemImg.setImageResource(horizontalList.get(position));
            if (effectMessage == position - 1) {
                holder.effectMessageItemImg.setBackgroundResource(R.drawable.effect_select_border_style);
            } else {
                holder.effectMessageItemImg.setBackgroundColor(Color.TRANSPARENT);
            }

            if(enableEffect){
                holder.effectMessageItemImg.setEnabled(true);
            }else{
                holder.effectMessageItemImg.setEnabled(false);
            }


            holder.effectMessageItemImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //螢幕的高
                    DisplayMetrics dm = new DisplayMetrics();
                    getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
                    int ScreenHeight = dm.heightPixels;
                    int ScreenWidth = dm.widthPixels;

                    //動畫初始
                    switch (position) {
                        case 0: {
                            selectEffectImg.setImageResource(R.drawable.boxempty);
                            effectMessage = -1;
                            selectPosition = 0;
                            enableEffect = true;
                            break;
                        }
                        case 1: {
                            showEffectMessageImg.setImageDrawable(null);
                            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.bombeffect);
                            showEffectMessageImg.setImageResource(R.drawable.animation_list_boom);

                            //圖片大小
                            myimageviewsize(showEffectMessageImg, (int) (ScreenHeight / 1.7), (int) (ScreenHeight / 1.7));


                            showEffectMessageImg.clearAnimation();
                            ((AnimationDrawable) (showEffectMessageImg.getDrawable())).stop();

                            // 重新将Frame動畫设置到第-1位，也就是重新開始
                            ((AnimationDrawable) (showEffectMessageImg.getDrawable())).selectDrawable(0);


                            ((AnimationDrawable) (showEffectMessageImg.getDrawable())).start();
                            showEffectMessageImg.startAnimation(animation);
                            animation.setFillAfter(true);
                            animation.setAnimationListener(new effectListener(holder));
                            effectMessage = 0;//設定訊息炸彈特效
                            selectPosition = 1;//選定的位置
                            enableEffect = false;
                            break;
                        }
                        case 2: {
                            for (int i = 0; i < 55; i++) {
                                playheart(effectMessageRelativeLayout, ScreenWidth, ScreenHeight, i, holder);
                            }
                            effectMessage = 1;//設定訊息愛心特效
                            selectPosition = 2;//選定的位置
                            enableEffect = false;
                            break;
                        }
                        case 3: {
                            final BubbleView bubbleView = new BubbleView(getActivity(), dm.widthPixels, dm.heightPixels);
                            effectMessageRelativeLayout.addView(bubbleView);
                            CountDownTimer countDownTimer = new CountDownTimer(6 * 1000, 1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {

                                }

                                @Override
                                public void onFinish() {
                                    effectMessageRelativeLayout.removeView(bubbleView);
                                    enableEffect = true;
                                    horizontalAdapter.notifyDataSetChanged();
                                }
                            };
                            countDownTimer.start();
                            effectMessage = 2;//設定訊息泡泡特效
                            selectPosition = 3;//選定的位置
                            enableEffect = false;
                            break;
                        }
                        case 4: {
                            holder.effectMessageItemImg.setImageResource(R.drawable.logo_red);
                            Paint whitePaint = new Paint();
                            whitePaint.setColor(0xffffffff);
                            BrokenView brokenView = BrokenView.add2Window(getActivity());
                            final BrokenTouchListener whiteListener = new BrokenTouchListener.Builder(brokenView).
                                    setPaint(whitePaint).
                                    build();
                            BrokenConfig config = new BrokenConfig();
                            config.region = new Region(showEffectMessageImg.getLeft(),
                                    showEffectMessageImg.getTop(),
                                    showEffectMessageImg.getRight(),
                                    showEffectMessageImg.getBottom());

                            showEffectMessageImg.getLayoutParams().width = 200;
                            showEffectMessageImg.getLayoutParams().height = 170;

                            Point point = new Point((int) 200, 200);
                            BrokenAnimator brokenAnim = whiteListener.getBrokenView().getAnimator(showEffectMessageImg);
                            if (brokenAnim == null)
                                brokenAnim = whiteListener.getBrokenView().createAnimator(showEffectMessageImg, point, config);
                            if (brokenAnim == null) {

                            }
                            if (!brokenAnim.isStarted()) {
                                brokenAnim.start();
                                whiteListener.getBrokenView().onBrokenStart(showEffectMessageImg);
                                BrokenCallback brokenCallback = new BrokenCallback() {
                                    @Override
                                    public void onFallingEnd(View v) {
                                        super.onFallingEnd(v);
                                        whiteListener.getBrokenView().reset();
                                        holder.effectMessageItemImg.setEnabled(false);
                                        showEffectMessageImg.setBackgroundColor(Color.RED);
                                    }
                                };
                                whiteListener.getBrokenView().setCallback(brokenCallback);
                            }

                            effectMessage = 3;//設定訊息玻璃特效
                            selectPosition = 4;//選定的位置
                            break;
                        }
                    }
                    selectEffectImg.setImageResource(horizontalList.get(position));
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return horizontalList.size();
        }
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    //播放愛心動畫
    private void playheart(RelativeLayout effectMessageRelativeLayout, int ScreenWidth, int ScreenHeight, int count, HorizontalAdapter.MyViewHolder holder) {
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
        finalSet.addListener(new HeartAnimatorlistener(heartImg, effectMessageRelativeLayout, count, holder));
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
        HorizontalAdapter.MyViewHolder holder;
        public effectListener(HorizontalAdapter.MyViewHolder holder){
            this.holder = holder;
        }

        @Override
        public void onAnimationEnd(Animation arg0) {
            // TODO Auto-generated method stub
            enableEffect = true;
            horizontalAdapter.notifyDataSetChanged();
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

    //愛心特效的動畫listener
    private class HeartAnimatorlistener implements Animator.AnimatorListener {
        RelativeLayout effectMessageRelativeLayout;
        int count;
        HorizontalAdapter.MyViewHolder holder;

        private View target;
        public HeartAnimatorlistener(View target, RelativeLayout effectMessageRelativeLayout, int count, HorizontalAdapter.MyViewHolder holder) {
            this.target = target;
            this.effectMessageRelativeLayout = effectMessageRelativeLayout;
            this.count = count;
            this.holder = holder;
        }

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            effectMessageRelativeLayout.removeView((target));
            if(count == 54){
                enableEffect = true;
                horizontalAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }

}
