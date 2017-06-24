package com.cool.user.netty_chatsystem.Chat_DrawBoard;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cool.user.netty_chatsystem.Chat_Client.handler.Client_UserHandler;
import com.cool.user.netty_chatsystem.Chat_DrawBoard.Accessory_picture.StickerImageView;
import com.cool.user.netty_chatsystem.Chat_DrawBoard.Accessory_picture.StickerTextView;
import com.cool.user.netty_chatsystem.Chat_DrawBoard.DrawBoard.DrawableView;
import com.cool.user.netty_chatsystem.Chat_DrawBoard.DrawBoard.DrawableViewConfig;
import com.cool.user.netty_chatsystem.Chat_DrawBoard.DrawBoard.penColor.colorpicker.ColorPickerDialog;
import com.cool.user.netty_chatsystem.Chat_DrawBoard.DrawBoard.penColor.colorpicker.ColorPickerSwatch;
import com.cool.user.netty_chatsystem.Chat_Animation.BrokenEffect.BrokenTouchListener;
import com.cool.user.netty_chatsystem.Chat_Animation.BrokenEffect.BrokenView;
import com.cool.user.netty_chatsystem.Chat_Sqlite_ChatHistory.MyDBHelper;
import com.cool.user.netty_chatsystem.Chat_biz.entity.file.ServerFile;
import com.cool.user.netty_chatsystem.Chat_core.connetion.IMConnection;
import com.cool.user.netty_chatsystem.Chat_core.protocol.Commands;
import com.cool.user.netty_chatsystem.Chat_core.protocol.Handlers;
import com.cool.user.netty_chatsystem.Chat_core.transport.Header;
import com.cool.user.netty_chatsystem.Chat_core.transport.IMResponse;
import com.cool.user.netty_chatsystem.Chat_server.dto.FileDTO;
import com.cool.user.netty_chatsystem.R;
import com.mingle.entity.MenuEntity;
import com.mingle.sweetpick.DimEffect;
import com.mingle.sweetpick.SweetSheet;
import com.mingle.sweetpick.ViewPagerDelegate;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by user on 2016/4/12.
 */
public class  DrawBoardFragement extends Fragment {

    public static DrawableView drawableView;
    private DrawableViewConfig config = new DrawableViewConfig();
    private FileOutputStream fos;
    protected Bitmap mBitmap;
    private static DisplayMetrics metrics = new DisplayMetrics();

    private final static String TAG = "TESTESTESTEST";

    // to take a picture
    private static final int CAMERA_PIC_REQUEST = 1111;
    private static final int GALLERY_PIC_REQUEST = 1112;
    //換取傳送過來的圖片路徑
    String mediaFileUri;
    //原始圖片drawable
    Drawable originalPicture;

    //傳送資料用得變數
    private int dataLength = 1024;
    private int sumCountpackage = 0;
    private String login_id;
    private String friend_id;


    //選取訊息特效的狀態條
    private SweetSheet sweetSheet;
    private RelativeLayout relativeLayout;

    //破玻璃特效
    private BrokenView brokenView;
    private BrokenTouchListener colorfulListener;
    private BrokenTouchListener whiteListener;
    private Paint whitePaint;
    private boolean effectEnable = true;


    ImageView drawboard_effect_imageview;
    ImageView drawboard_send_imageview;
    ImageView drawboard_pen_imageview;
    ImageView drawboard_word_imageview ;
    ImageView drawboard_accessory_imageview;
    ImageView drawboard_back_imageview;
    ImageView drawboard_store_imageview;
    ImageView effectImg;

    TextView filterNameText;

    private RelativeLayout drawboard_layout;

    // current view is the current selected view - hopefully this will work ok
    private int mCurrentView = 0;

    public int getmCurrentView() {
        return mCurrentView;
    }

    public void setmCurrentView(int mCurrentView) {
        this.mCurrentView = mCurrentView;
        mViewsArray.get(mCurrentView).bringToFront();
    }

    // this tells me how many views I currently have.
    private int mViewsCount = 0;

    private int[] mViewsCount_array = {0};
    private boolean[] mViewsCount_use_array;

    private ArrayList<View> mViewsArray = new ArrayList<View>();
    private ArrayList<StickerImageView> stickerImageViews = new ArrayList<StickerImageView>();

    FragmentManager fragmentManager;

    //筆的顏色
    private int selectedColor;

    //可左右滑動更換濾鏡的變數
    private GestureDetectorCompat gestureDetectorCompat;
    private static final int SWIPE_DISTANCE_THRESHOLD = 125;
    private static final int SWIPE_VELOCITY_THRESHOLD = 75;



    public SweetSheet getSweetSheet(){
        return sweetSheet;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater ,ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.drawboard_fragment, container, false);

        //Character_Activity character_activity = new Character_Activity();
        //character_activity.mViewPager.setSwipeLocked(true);

        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        initUi(rootView, fragmentTransaction);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onResume(){
        super.onResume();
    }



    private void initUi(View rootView , final FragmentTransaction fragmentTransaction) {
        drawableView = (DrawableView) rootView.findViewById(R.id.paintView);
        drawboard_effect_imageview = (ImageView)rootView.findViewById(R.id.drawboard_effect_imageveiw);
        drawboard_send_imageview = (ImageView)rootView.findViewById(R.id.drawboard_send_imageview);
        drawboard_pen_imageview = (ImageView)rootView.findViewById(R.id.drawboard_pen_imageivew);
        drawboard_word_imageview = (ImageView)rootView.findViewById(R.id.drawboard_word_imageview);
        drawboard_accessory_imageview = (ImageView)rootView.findViewById(R.id.drawboard_accessory_imageview);
        drawboard_back_imageview = (ImageView)rootView.findViewById(R.id.drawboard_back_imageview);
        drawboard_store_imageview = (ImageView)rootView.findViewById(R.id.drawboard_store_imageview);
        drawboard_layout = (RelativeLayout)rootView.findViewById(R.id.drawboard_layout);
        relativeLayout = (RelativeLayout)rootView.findViewById(R.id.icon_layout);

        //展現特效的ImageView
        effectImg = (ImageView) rootView.findViewById(R.id.effectImg);

        //展現filter名字的textview
        filterNameText = (TextView)rootView.findViewById(R.id.filterNameText);

        drawableView.setEnabled(false);

        config.setStrokeColor(getResources().getColor(android.R.color.black));
        config.setShowCanvasBounds(true);
        config.setStrokeWidth(10.0f);
        config.setMinZoom(1.0f);
        config.setMaxZoom(3.0f);
        config.setCanvasHeight(metrics.heightPixels);
        config.setCanvasWidth(metrics.widthPixels);
        drawableView.setConfig(config);

        //將拍照圖片用在drawableView上
        Bundle bundle = getArguments();
        mediaFileUri = bundle.getString("mediaFile").substring(bundle.getString("mediaFile").indexOf("///") + 2);
        //originalPicture = new BitmapDrawable(getResources(), HelpUtil.getBitmapByUrl(mediaFileUri));
        //drawableView.setBackground(originalPicture);

        if(bundle.getInt("type") == 1){
            login_id = bundle.getString("login_id");
            friend_id = bundle.getString("friend_id");
        }

        //以下為可左右滑動relativelayout來改變濾鏡
        gestureDetectorCompat = new GestureDetectorCompat(getActivity(),new MyGestureListener());
        drawboard_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetectorCompat.onTouchEvent(event);
                return true;
            }
        });



        drawboard_effect_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupEffectViewpager();
            }
        });

        drawboard_pen_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawableView.setEnabled(true);

                selectedColor = ContextCompat.getColor(getActivity(), R.color.flamingo);
                FragmentTransaction fragmentTransactionPen = fragmentManager.beginTransaction();

                int[] mColors = getResources().getIntArray(R.array.default_rainbow);

                final ColorPickerDialog dialog = ColorPickerDialog.newInstance(R.string.color_picker_default_title,
                        mColors,
                        selectedColor,
                        5, // Number of columns
                        ColorPickerDialog.SIZE_SMALL);
                dialog.show(fragmentTransactionPen, "dialog");

                dialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {

                    @Override
                    public void onColorSelected(int color) {
                        selectedColor = color;
                        config.setStrokeColor(color);
                        dialog.dismiss();
                    }

                });

            }
        });

        drawboard_word_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StickerTextView stickerTextView = new StickerTextView(getActivity());
                drawboard_layout.addView(stickerTextView);
            }
        });


        drawboard_back_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                config.setStrokeColor(Color.TRANSPARENT);

            }
        });

        drawboard_store_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureSave();
            }
        });

        drawboard_accessory_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Select:");
                final CharSequence[] chars = {"Take Picture", "Choose from Gallery"};
                builder.setItems(chars, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
                                } else if (which == 1) {
                                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                                    startActivityForResult(intent, GALLERY_PIC_REQUEST);
                                }
                                dialog.dismiss();
                            }

                        }
                );
                builder.show();
            }
        });

        drawboard_send_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap image = loadBitmapFromView(drawboard_layout);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] bytes = stream.toByteArray();

                if ((bytes.length % dataLength == 0))
                    sumCountpackage = bytes.length / dataLength;
                else
                    sumCountpackage = (bytes.length / dataLength) + 1;

                Log.i("TAG", "文件總長度:" + bytes.length);
                final ServerFile serverFile = new ServerFile();
                serverFile.setSumCountPackage(sumCountpackage);
                serverFile.setCountPackage(1);
                serverFile.setBytes(bytes);
                serverFile.setSendId(login_id);
                serverFile.setReceiveId(friend_id);
                serverFile.setFileName(Build.MANUFACTURER + "-" + UUID.randomUUID() + ".jpg");
                IMConnection connection = Client_UserHandler.getConnection();
                IMResponse resp = new IMResponse();
                Header header = new Header();
                header.setHandlerId(Handlers.MESSAGE);
                header.setCommandId(Commands.USER_FILE_REQUEST);
                resp.setHeader(header);
                resp.writeEntity(new FileDTO(serverFile));
                connection.sendResponse(resp);
                System.out.println("文件已經讀取完畢");

                //Find the dir to save cached images**************************************************************************
                File cacheDir;
                if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
                    //Creates a new File instance from a parent abstract pathname and a child pathname string.
                    cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),"TTImages_cache");
                else
                    cacheDir= getActivity().getCacheDir();
                if(!cacheDir.exists())
                    cacheDir.mkdirs();

                cacheDir = new File(cacheDir , String.valueOf(image.toString().hashCode()));

                saveSqliteHistory(cacheDir.getAbsolutePath(),0,"1");

                try {
                    RandomAccessFile randomAccessFile = new RandomAccessFile(cacheDir, "rw");
                    randomAccessFile.write(bytes);
                }catch(Exception e){
                    e.printStackTrace();
                }
                //******************************************************************************************

                getActivity().setResult(-1);
                getActivity().finish();
            }
        });





        /*Button strokeWidthMinusButton = (Button)  rootView.findViewById(R.id.strokeWidthMinusButton);
        Button strokeWidthPlusButton = (Button)  rootView.findViewById(R.id.strokeWidthPlusButton);
        Button changeColorButton = (Button)  rootView.findViewById(R.id.changeColorButton);
        Button undoButton = (Button)  rootView.findViewById(R.id.undoButton);

        config.setStrokeColor(getResources().getColor(android.R.color.black));
        config.setShowCanvasBounds(true);
        config.setStrokeWidth(20.0f);
        config.setMinZoom(1.0f);
        config.setMaxZoom(3.0f);
        config.setCanvasHeight(1080);
        config.setCanvasWidth(1920);
        drawableView.setConfig(config);

        strokeWidthPlusButton.setOnClickListener(new View.OnClickListener() {

            @Override public void onClick(View v) {
                config.setStrokeWidth(config.getStrokeWidth() + 10);
            }
        });
        strokeWidthMinusButton.setOnClickListener(new View.OnClickListener() {

            @Override public void onClick(View v) {
                config.setStrokeWidth(config.getStrokeWidth() - 10);
            }
        });
        changeColorButton.setOnClickListener(new View.OnClickListener() {

            @Override public void onClick(View v) {
                Random random = new Random();
                config.setStrokeColor(
                        Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256)));
            }
        });
        undoButton.setOnClickListener(new View.OnClickListener() {

            @Override public void onClick(View v) {
                drawableView.undo();
            }
        });*/

    }

    //儲存資料進sqlite裡
    private void saveSqliteHistory(String messageText , int me , String type){
        //創建MyDBHelper對象
        MyDBHelper dbHelper = new MyDBHelper(getActivity(), "Chat.db", null, 1);
        //得到一個可讀的SQLiteDatabase對象
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //生成ContentValues​​對象 //key:列名，value:想插入的值
        ContentValues cv = new ContentValues();

        //往ContentValues​​對象存放數據，鍵-值對模式
        if(me == 0) {
            cv.put("from_id", login_id);
            cv.put("to_id", friend_id);
        }else{
            cv.put("from_id", friend_id);
            cv.put("to_id" , login_id);
        }
        cv.put("content", messageText);
        cv.put("type" , type);

        //調用insert方法，將數據插入數據庫
        db.insert("Message", null, cv);
        //關閉數據庫
        db.close();

    }

    //初始化破玻璃特效頁面
    private void initBrokenLayout(){
        brokenView = BrokenView.add2Window(getActivity());

        whitePaint = new Paint();
        whitePaint.setColor(0xffffffff);


        colorfulListener = new BrokenTouchListener.Builder(brokenView).
                setComplexity(8).
                setBreakDuration(500).
                setFallDuration(1000).
                setCircleRiftsRadius(20).
                build();
        whiteListener = new BrokenTouchListener.Builder(brokenView).
                setComplexity(8).
                setBreakDuration(500).
                setFallDuration(1000).
                setCircleRiftsRadius(20).
                setPaint(whitePaint).
                build();

        relativeLayout.setOnTouchListener(colorfulListener);

        brokenView.setEnable(effectEnable);
    }

    //儲存圖片
    public void PictureSave(){
        long now = System.currentTimeMillis();

        try
        {
            hideButton();
            fos = new FileOutputStream(String.format(Environment.getExternalStorageDirectory().getAbsolutePath()+"/edited_%d.png",now));
            //drawableView.obtainBitmap().compress(Bitmap.CompressFormat.PNG, 100, fos);
            loadBitmapFromView(drawboard_layout).compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            fos = null;
            Toast.makeText(getActivity(),"save successfully",Toast.LENGTH_SHORT).show();
            showButton();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (fos != null)
            {
                try
                {
                    fos.close();
                    fos = null;
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }


    //將layout的版面樣式轉成圖片
    public static Bitmap loadBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap(metrics.widthPixels,
                metrics.heightPixels,
                Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);

        //一個MeasureSpec封裝了父佈局傳遞給子佈局的佈局要求，每個MeasureSpec代表了一組寬度和高度的要求
        //三種模式：UNSPECIFIED(未指定),父元素部隊自元素施加任何束縛，子元素可以得到任意想要的大小
        //EXACTLY(完全)，父元素決定自元素的確切大小，子元素將被限定在給定的邊界里而忽略它本身大小
        //AT_MOST(至多)，子元素至多達到指定大小的值。

        /*v.measure(View.MeasureSpec.makeMeasureSpec(v.getLayoutParams().width,
                        View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(v.getLayoutParams().height,
                        View.MeasureSpec.EXACTLY));
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());*/

        v.draw(c);

        return b;
    }

    //藏起所有按鈕
    public void hideButton(){
        drawboard_effect_imageview.setVisibility(View.INVISIBLE) ;
        drawboard_send_imageview.setVisibility(View.INVISIBLE) ;
        drawboard_pen_imageview.setVisibility(View.INVISIBLE) ;
        drawboard_word_imageview.setVisibility(View.INVISIBLE) ;
        drawboard_accessory_imageview.setVisibility(View.INVISIBLE) ;

        drawboard_back_imageview.setVisibility(View.INVISIBLE) ;
        drawboard_store_imageview.setVisibility(View.INVISIBLE);
    }

    //顯示所有按鈕
    public void showButton(){
        drawboard_effect_imageview.setVisibility(View.VISIBLE) ;
        drawboard_send_imageview.setVisibility(View.VISIBLE) ;
        drawboard_pen_imageview.setVisibility(View.VISIBLE) ;
        drawboard_word_imageview.setVisibility(View.VISIBLE) ;
        drawboard_accessory_imageview.setVisibility(View.VISIBLE) ;
        drawboard_back_imageview.setVisibility(View.VISIBLE) ;
        drawboard_store_imageview.setVisibility(View.VISIBLE);
    }

    //獲取圖片的路徑
    public String getPath(Uri uri){
        String[] filePathColumn={MediaStore.Images.Media.DATA};

        Cursor cursor=getActivity().getContentResolver().query(uri, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex=cursor.getColumnIndex(filePathColumn[0]);
        Log.i(TAG, "Image path is:" + cursor.getString(columnIndex));
        return cursor.getString(columnIndex);
    }

    //設置訊息特效的狀態列
    private void setupEffectViewpager() {

        sweetSheet = new SweetSheet(relativeLayout);
        //从menu 中设置数据源
        sweetSheet.setDelegate(new ViewPagerDelegate());
        sweetSheet.setBackgroundEffect(new DimEffect(0.5f));
        sweetSheet.setOnMenuItemClickListener(new SweetSheet.OnMenuItemClickListener() {
            @Override
            public boolean onItemClick(int position, MenuEntity menuEntity1) {
                switch (position) {
                    case 0:
                        bombAnimation();
                        break;
                    case 1:
                        guessPicture();
                        break;
                }
                return false;
            }
        });

        sweetSheet.toggle();


    }

    //展現bomb特效
    public void bombAnimation(){
        effectImg.setImageDrawable(null);
        //動畫初始
        final Animation animation = AnimationUtils.loadAnimation(getActivity() , R.anim.bombeffect);

        //螢幕的高
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int ScreenHeight = dm.heightPixels;

        //圖片大小
        myimageviewsize(effectImg, (int) (ScreenHeight / 1.7), (int) (ScreenHeight / 1.7));
        System.out.println(effectImg);

        effectImg.clearAnimation();
        ((AnimationDrawable)(effectImg.getBackground())).stop();

        // 重新将Frame動畫设置到第-1位，也就是重新開始
        ((AnimationDrawable)(effectImg.getBackground())).selectDrawable(0);


        ((AnimationDrawable)(effectImg.getBackground())).start();
        effectImg.startAnimation(animation);
        animation.setFillAfter(true);
        animation.setAnimationListener(new effectListener());
    }

    //實施猜猜看特效
    public void guessPicture(){
        effectImg.setImageDrawable(originalPicture);
    }

    private void myimageviewsize(ImageView imgid, int evenWidth, int evenHight) {
        // TODO 自動產生的方法 Stub
        ViewGroup.LayoutParams params = imgid.getLayoutParams();  //需import android.view.ViewGroup.LayoutParams;
        params.width = evenWidth;
        params.height = evenHight;
        imgid.setLayoutParams(params);
    }

    //處理動畫的監聽器
    private class effectListener implements Animation.AnimationListener {

        @Override
        public void onAnimationEnd(Animation arg0) {
            // TODO Auto-generated method stub

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

    //為可左右滑動relativelayout來更換濾鏡的監聽器-----------------------------------------------------
    class MyGestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            float distanceX = e2.getX() - e1.getX();
            float distanceY = e2.getY() - e1.getY();
            if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) >
                    SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {

                //文字消失的動畫
                final Animation animFadeOut = AnimationUtils.loadAnimation(getActivity(),
                        R.anim.fade_out);

                // change picture to
                if (distanceX > 0) {
                    // start left increment
                    /*Toast.makeText(getActivity(), "left", Toast.LENGTH_SHORT).show();
                    Bitmap bitmap = pixelateFilter.changeToPixelate(HelpUtil.getBitmapByUrl(mediaFileUri),5);
                    Drawable picture = new BitmapDrawable(getResources(),bitmap);
                    drawableView.setBackground(picture);
                    filterNameText.setText("mysterious");
                    filterNameText.startAnimation(animFadeOut);*/

                }
                else {  // the left
                    // start right increment
                    /*Toast.makeText(getActivity() ,"right", Toast.LENGTH_SHORT).show();
                    Drawable picture = new BitmapDrawable(getResources(),HelpUtil.getBitmapByUrl(mediaFileUri));
                    drawableView.setBackground(picture);
                    filterNameText.setText("original");
                    filterNameText.startAnimation(animFadeOut);*/
                }
            }
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            // checks if we're touching for more than 2f. I like to have this implemented, to prevent
            // jerky image motion, when not really moving my finger, but still touching. Optional.
            if (Math.abs(distanceY) > 2 || Math.abs(distanceX) > 2) {
                if(Math.abs(distanceX) > Math.abs(distanceY)) {
                    // move the filter left or right
                }
            }
            return true;
        }
    }
    //----------------------------------------------------------------------------------------------------------------


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // usedView is a bool that checks is a view was destroyed and this was reused.
        // if it wasn't reused, this means we create a new one.
        if (requestCode == CAMERA_PIC_REQUEST) {
            try{
                Uri selectedImage = data.getData();
                getPath(selectedImage);
                InputStream is;
                is = getActivity().getContentResolver().openInputStream(selectedImage);
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inJustDecodeBounds = true;
                //BitmapFactory.decodeStream(bis,null,opts);
                BitmapFactory.decodeStream(is,null,opts);

                //The new size we want to scale to
                final int REQUIRED_SIZE=200;

                //Find the correct scale value. It should be the power of 2.
                int scale=1;
                while(opts.outWidth/scale/2>=REQUIRED_SIZE || opts.outHeight/scale/2>=REQUIRED_SIZE)
                    scale*=2;

                Log.i(TAG,"Scale is: "+scale);
                opts.inSampleSize = scale;
                opts.inJustDecodeBounds = false;
                is = null;
                System.gc();
                InputStream is2 = getActivity().getContentResolver().openInputStream(selectedImage);

                Bitmap returnedImage = BitmapFactory.decodeStream(is2, null, opts);
                Log.i(TAG, "Image width from bitmap: " + returnedImage.getWidth());
                Log.i(TAG, "Image height from bitmap: " + returnedImage.getHeight());
                Log.i(TAG, "Creating another View");
                StickerImageView stickerImageView = new StickerImageView(getActivity());
                stickerImageView.setImageBitmap(returnedImage);
                stickerImageViews.add(stickerImageView);
                drawboard_layout.addView(stickerImageView);
            }
            catch(NullPointerException e){
                //Do nothing
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (requestCode == GALLERY_PIC_REQUEST){
            try {
                Uri selectedImage = data.getData();
                getPath(selectedImage);
                InputStream is;
                is = getActivity().getContentResolver().openInputStream(selectedImage);
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inJustDecodeBounds = true;
                //BitmapFactory.decodeStream(bis,null,opts);
                BitmapFactory.decodeStream(is,null,opts);

                //The new size we want to scale to
                final int REQUIRED_SIZE=200;

                //Find the correct scale value. It should be the power of 2.
                int scale=1;
                while(opts.outWidth/scale/2>=REQUIRED_SIZE || opts.outHeight/scale/2>=REQUIRED_SIZE)
                    scale*=2;

                Log.i(TAG,"Scale is: "+scale);
                opts.inSampleSize = scale;
                opts.inJustDecodeBounds = false;
                is = null;
                System.gc();
                InputStream is2 = getActivity().getContentResolver().openInputStream(selectedImage);

                Bitmap returnedImage = BitmapFactory.decodeStream(is2, null, opts);
                Log.i(TAG,"Image width from bitmap: "+returnedImage.getWidth());
                Log.i(TAG, "Image height from bitmap: " + returnedImage.getHeight());
                Log.i(TAG, "Creating another View");
                StickerImageView stickerImageView = new StickerImageView(getActivity());
                stickerImageView.setImageBitmap(returnedImage);
                drawboard_layout.addView(stickerImageView);
            } catch (FileNotFoundException e) {

            }
            catch (NullPointerException e){
            }
        }
    }
}
