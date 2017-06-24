package com.cool.user.netty_chatsystem.Chat_DrawBoard.Accessory_picture;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.cool.user.netty_chatsystem.Chat_DrawBoard.DrawBoardFragement;


/**
 * Created by user on 2016/4/19.
 */
public class TouchView extends View {

    private static final int INVALID_POINTER_ID = -1;

    private final String TAG = "TESTESTEST";

    private Drawable mImage;

    public void setmImage(Drawable image){
        mImage = image;
    }

    // width and height of original image
    private float mImageWidth;
    private float mImageHeight;

    // when image is scaled, we use this to calculate the bounds of the image
    private int mImageWidthScaled;
    private int mImageHeightScaled;

    public float mPosX = 150;
    public float mPosY = 300;

    public float getmPosX() {
        return mPosX;
    }

    public void setmPosX(float mPosX) {
        this.mPosX = mPosX;
    }

    public float getmPosY() {
        return mPosY;
    }

    public void setmPosY(float mPosY) {
        this.mPosY = mPosY;
    }

    private float mLastTouchX;
    private float mLastTouchY;

    private Paint mBorderLeftLine;
    private Paint mBorderTopLine;
    private Paint mBorderRightLine;
    private Paint mBorderBottomLine;

    private int mActivePointerId = INVALID_POINTER_ID;
    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1f;

    // this is to tell MainActivity what view number I am in the array.
    private int mNumberView;

    // this is what draws the red line around the TouchView to tell the user
    // this one is currently selected
    private boolean mSelected = false;

    public void setmSelected(boolean mSelected) {
        this.mSelected = mSelected;
    }

    private DrawBoardFragement mStyle;

    // this is to keep a reference to the original image, so when we ship, we can tar
    // the images that are needed.
    private String mUri;

    public void setImageLocation(String path){
        this.mUri = path;
    }


    public TouchView(Context context, DrawBoardFragement  style, BitmapDrawable image, int count, float scaleFactor){
        super(context);
        this.mImage = image;
        mImageWidth = image.getBitmap().getWidth();
        mImageHeight = image.getBitmap().getHeight();
        mImageWidthScaled = (int) (mImageWidth*scaleFactor);
        mImageHeightScaled = (int) (mImageHeight*scaleFactor);
        this.mNumberView = count;
        this.mStyle = style;
        this.mScaleFactor = scaleFactor;
        init(context);
    }

    private void init(Context context) {
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        mBorderLeftLine = new Paint();
        mBorderRightLine = new Paint();
        mBorderBottomLine = new Paint();
        mBorderTopLine = new Paint();
        setBorderParams(Color.RED, 2);

    }

    private void setBorderParams(int color, float width) {
        mBorderLeftLine.setColor(color);
        mBorderLeftLine.setStrokeWidth(width);
        mBorderRightLine.setColor(color);
        mBorderRightLine.setStrokeWidth(width);
        mBorderBottomLine.setColor(color);
        mBorderBottomLine.setStrokeWidth(width);
        mBorderTopLine.setColor(color);
        mBorderTopLine.setStrokeWidth(width);

        //getIntrinsic Width() 和 Height 為將圖片實際長寬(px)轉為dp所取出來的值
        //setBounds 指定一個矩形區塊 , 只在此區塊畫圖
        mImage.setBounds(0, 0, mImage.getIntrinsicWidth(), mImage.getIntrinsicHeight());

    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();

        //translate為移動我們畫布的原坐標系 , 預設為(0,0)
        canvas.translate(mPosX, mPosY);
        canvas.scale(mScaleFactor, mScaleFactor);
        mImage.draw(canvas);
        if (mSelected){
            //public void drawLine (float startX, float startY, float stopX, float stopY, Paint paint)
            canvas.drawLine(0,
                    0,
                    mImage.getIntrinsicWidth(),
                    0,
                    mBorderTopLine);
            canvas.drawLine(0, mImage.getIntrinsicHeight(),
                    mImage.getIntrinsicWidth(),
                    mImage.getIntrinsicHeight(),
                    mBorderBottomLine);
            canvas.drawLine(0,
                    0,
                    0,
                    mImage.getIntrinsicHeight(),
                    mBorderLeftLine);
            canvas.drawLine(mImage.getIntrinsicWidth(),
                    0,
                    mImage.getIntrinsicWidth(),
                    mImage.getIntrinsicHeight(),
                    mBorderRightLine);
        }
        canvas.restore();
    }

    public boolean onTouchEvent(MotionEvent event) {
        //ImageView view = (ImageView) v;
        mScaleDetector.onTouchEvent(event);
        boolean intercept = false;
        //boolean defaultResult = onTouchEvent(event);

        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                mLastTouchX = event.getX();
                mLastTouchY = event.getY();
                mActivePointerId = event.getPointerId(0);
                // this should mean that I accept the touch out of all my views
            /*if (((mLastTouchX >= mPosX) && (mLastTouchX <= mPosX+ mImage.getIntrinsicWidth())
                    && (mLastTouchY >= mPosY) && (mLastTouchY <= mPosY + mImage.getIntrinsicHeight())))*/
                if (((mLastTouchX >= mPosX) && (mLastTouchX <= mPosX+ mImageWidthScaled/2)
                        && (mLastTouchY >= mPosY) && (mLastTouchY <= mPosY + mImageHeightScaled/2))){
                    Log.i(TAG, "My view is here: " + mStyle);
                    intercept = true;
                    mSelected = true;
                    mStyle.setmCurrentView(mNumberView);

                }

                Log.i(TAG,"Action down");
                Log.i(TAG,"x is: "+mLastTouchX);
                Log.i(TAG,"y is: "+mLastTouchY);
                break;
            case MotionEvent.ACTION_UP:
                setFocusable(false);
                mImageWidthScaled = (int) (mImageWidth*mScaleFactor);
                mImageHeightScaled = (int) (mImageHeight*mScaleFactor);
            /*          mPosX = (int) event.getX();
            mPosY = (int) event.getY();*/
                mActivePointerId = INVALID_POINTER_ID;
                // stop the red rectangle from being drawn around the View
                mSelected = false;
                break;
            case MotionEvent.ACTION_CANCEL:
                mActivePointerId = INVALID_POINTER_ID;
                // stop the red rectangle from being drawn around the View
                mSelected = false;
                break;
            case MotionEvent.ACTION_MOVE:
                final int pointerIndex = event.findPointerIndex(mActivePointerId);
                final float x = event.getX(pointerIndex);
                final float y = event.getY(pointerIndex);
                if (!mScaleDetector.isInProgress()) {
                    final float dx = x - mLastTouchX;
                    final float dy = y - mLastTouchY;

                    mPosX += dx;
                    mPosY += dy;

                    // Invalidate to request a redraw
                    invalidate();
                }
                else{
                    Log.i(TAG,"Now scaling is happening");
                }
                // Remember this touch position for the next move event
                mLastTouchX = x;
                mLastTouchY = y;

                break;

            //POINTER_UP  is used for multitouch events and mainly here for stretching the image when a user uses multitouch.
            case MotionEvent.ACTION_POINTER_UP:
                final int pointerIndex2 = (event.getAction() & MotionEvent.ACTION_POINTER_ID_MASK)
                        >> MotionEvent.ACTION_POINTER_ID_SHIFT;
                final int pointerId = event.getPointerId(pointerIndex2);
                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex2 == 0 ? 1 : 0;
                    mLastTouchX = event.getX(newPointerIndex);
                    mLastTouchY = event.getY(newPointerIndex);
                    mActivePointerId = event.getPointerId(newPointerIndex);
                }
                break;
            default:
                //return defaultResult;
        }
        return intercept;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.05f, Math.min(mScaleFactor, 5.0f));
            invalidate();
            Log.i(TAG,"New Image size: widht: "+mImage.getIntrinsicWidth()+" height: "+mImage.getIntrinsicHeight());
            return true;
        }
    }

    public void greyScaler() {
        ColorMatrix cm = new ColorMatrix();
        cm.set(new float[] {
                .21f, .71f, .07f, 0, 0,
                .21f, .71f, .07f, 0, 0,
                .21f, .71f, .07f, 0, 0,
                0, 0, 0, 1, 0 });
        mImage.setColorFilter(new ColorMatrixColorFilter(cm));
        invalidate();
    }

}
