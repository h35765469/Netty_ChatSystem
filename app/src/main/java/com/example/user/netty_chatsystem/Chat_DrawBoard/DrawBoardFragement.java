package com.example.user.netty_chatsystem.Chat_DrawBoard;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.example.user.netty_chatsystem.Chat_DrawBoard.Accessory_picture.StickerImageView;
import com.example.user.netty_chatsystem.Chat_DrawBoard.Accessory_picture.StickerTextView;
import com.example.user.netty_chatsystem.Chat_DrawBoard.DrawBoard.DrawableView;
import com.example.user.netty_chatsystem.Chat_DrawBoard.DrawBoard.DrawableViewConfig;
import com.example.user.netty_chatsystem.Chat_DrawBoard.DrawBoard.penColor.colorpicker.ColorPickerDialog;
import com.example.user.netty_chatsystem.Chat_DrawBoard.DrawBoard.penColor.colorpicker.ColorPickerSwatch;
import com.example.user.netty_chatsystem.R;
import com.facebook.share.ShareApi;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

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

    ImageView drawboard_cleanboard_imageview;
    ImageView drawboard_effect_imageview;
    ImageView drawboard_send_imageview;
    ImageView drawboard_pen_imageview;
    ImageView drawboard_word_imageview ;
    ImageView drawboard_accessory_imageview;
    ImageView drawboard_back_imageview;
    ImageView drawboard_store_imageview;

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

    private static DrawBoardFragement mDrawBoardFragement;
    private PageTwoFragment pageTwoFragment;

    FragmentManager fragmentManager;

    //筆的顏色
    private int selectedColor;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater ,ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.drawboard_fragment, container, false);

        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mDrawBoardFragement = this;
        pageTwoFragment =new PageTwoFragment();

        fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        initUi(rootView ,  fragmentTransaction);

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
        drawboard_cleanboard_imageview = (ImageView)rootView.findViewById(R.id.drawboard_cleanboard_imageivew);
        drawboard_effect_imageview = (ImageView)rootView.findViewById(R.id.drawboard_effect_imageveiw);
        drawboard_send_imageview = (ImageView)rootView.findViewById(R.id.drawboard_send_imageview);
        drawboard_pen_imageview = (ImageView)rootView.findViewById(R.id.drawboard_pen_imageivew);
        drawboard_word_imageview = (ImageView)rootView.findViewById(R.id.drawboard_word_imageview);
        drawboard_accessory_imageview = (ImageView)rootView.findViewById(R.id.drawboard_accessory_imageview);
        drawboard_layout = (RelativeLayout)rootView.findViewById(R.id.drawboard_layout);
        drawboard_back_imageview = (ImageView)rootView.findViewById(R.id.drawboard_back_imageview);
        drawboard_store_imageview = (ImageView)rootView.findViewById(R.id.drawboard_store_imageview);

        config.setStrokeColor(getResources().getColor(android.R.color.black));
        config.setShowCanvasBounds(true);
        config.setStrokeWidth(20.0f);
        config.setMinZoom(1.0f);
        config.setMaxZoom(3.0f);
        config.setCanvasHeight(metrics.heightPixels);
        config.setCanvasWidth(metrics.widthPixels);
        drawableView.setConfig(config);

        drawboard_cleanboard_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentTransaction.replace(R.id.cameraContainer, pageTwoFragment);
                fragmentTransaction.commit();
            }
        });

        drawboard_pen_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                        drawboard_back_imageview.setVisibility(View.VISIBLE);
                        drawboard_pen_imageview.setBackgroundColor(color);
                    }

                });

                drawboard_back_imageview.setVisibility(View.VISIBLE);
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
                drawableView.undo();
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
                SharePhoto photo = new SharePhoto.Builder()
                        .setBitmap(image)
                        .build();
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .build();
                ShareApi.share(content, null);
                Toast.makeText(getActivity(),"ffffff",Toast.LENGTH_SHORT).show();
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

    //儲存圖片
    public void PictureSave(){
        long now = System.currentTimeMillis();

        try
        {
            hideButton();
            fos = new FileOutputStream(String.format(Environment.getExternalStorageDirectory().getAbsolutePath()+"/edited_%d.png",now));
            drawableView.obtainBitmap().compress(Bitmap.CompressFormat.PNG, 100, fos);
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
        drawboard_cleanboard_imageview.setVisibility(View.INVISIBLE);
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
        drawboard_cleanboard_imageview.setVisibility(View.VISIBLE);
        drawboard_effect_imageview.setVisibility(View.VISIBLE) ;
        drawboard_send_imageview.setVisibility(View.VISIBLE) ;
        drawboard_pen_imageview.setVisibility(View.VISIBLE) ;
        drawboard_word_imageview.setVisibility(View.VISIBLE) ;
        drawboard_accessory_imageview.setVisibility(View.VISIBLE) ;
        drawboard_back_imageview.setVisibility(View.VISIBLE) ;
        drawboard_store_imageview.setVisibility(View.VISIBLE);
    }

    public String getPath(Uri uri){
        String[] filePathColumn={MediaStore.Images.Media.DATA};

        Cursor cursor=getActivity().getContentResolver().query(uri, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex=cursor.getColumnIndex(filePathColumn[0]);
        Log.i(TAG, "Image path is:" + cursor.getString(columnIndex));
        return cursor.getString(columnIndex);
    }

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
                //TouchView newView = new TouchView(getActivity(),mDrawBoardFragement,new BitmapDrawable(returnedImage),mViewsCount,1f);
                //newView.setImageLocation(getPath(selectedImage));
                //newView.setClickable(true);
                // below is to ensure red border is drawn on new selected image
                //newView.setmSelected(true);
                //mViewsArray.add(newView);
                stickerImageViews.add(stickerImageView);
                //drawboard_layout.addView(mViewsArray.get(mViewsCount));
                drawboard_layout.addView(stickerImageView);
                //newView.invalidate();
                //mViewsCount+=1;
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

                //TouchView newView = new TouchView(getActivity(),mDrawBoardFragement,new BitmapDrawable(returnedImage),mViewsCount,1f);
                //newView.setImageLocation(getPath(selectedImage));
                //newView.setClickable(true);
                // below is to ensure red border is drawn on new selected image
                //newView.setmSelected(true);
                //mViewsArray.add(newView);
                //drawboard_layout.addView(mViewsArray.get(mViewsCount));
                drawboard_layout.addView(stickerImageView);
                //newView.invalidate();
                //mViewsCount+=1;
            } catch (FileNotFoundException e) {

            }
            catch (NullPointerException e){
            }
        }
    }
}
