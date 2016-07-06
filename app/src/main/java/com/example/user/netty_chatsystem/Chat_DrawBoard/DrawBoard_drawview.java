package com.example.user.netty_chatsystem.Chat_DrawBoard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by user on 2016/4/6.
 */
public class DrawBoard_drawview extends View {

    public static final int LINE = 1;
    public static final int RECTANGLE = 3;
    public static final int SQUARE = 4;
    public static final int CIRCLE = 5;
    public static final int TRIANGLE = 6;
    public static final int SMOOTHLINE = 2;

    public static final float TOUCH_TOLERANCE = 4;
    public static final float TOUCH_STROKE_WIDTH = 5;

    public int mCurrentShape;

    protected Path mPath;
    protected Paint mPaint;
    protected Paint mPaintFinal;
    protected Bitmap mBitmap;
    public Canvas mCanvas;
    private FileOutputStream fos;

    /**
     * Indicates if you are drawing
     */
    public boolean isDrawing = false;

    //偵測橡皮擦是否開啟
    public boolean isEraser = true;

    /**
     * Indicates if the drawing is ended
     */
    protected boolean isDrawingEnded = false;


    protected float mStartX;
    protected float mStartY;

    protected float mx;
    protected float my;

    public DrawBoard_drawview(Context context) {
        super(context);
        init();
    }

    public DrawBoard_drawview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawBoard_drawview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmap, 0, 0, mPaint);

        if (isDrawing){
            switch (mCurrentShape) {
                case LINE:
                    onDrawLine(canvas);
                    break;
                case RECTANGLE:
                    onDrawRectangle(canvas);
                    break;
                case SQUARE:
                    onDrawSquare(canvas);
                    break;
                case CIRCLE:
                    onDrawCircle(canvas);
                    break;
                case TRIANGLE:
                    onDrawTriangle(canvas);
                    break;
            }
        }
    }

    //undo

    //清空canvas
    public void CleanCanvas(){
        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mCanvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));

        //重新更新介面
        invalidate();

    }

    //橡皮擦
    public void Eraser(){
        if(isEraser) {
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }else{
            mPaint.setXfermode(null);
        }
    }

    //儲存圖片
    public void PictureSave(){
        long now = System.currentTimeMillis();

        try
        {
            fos = new FileOutputStream(String.format(Environment.getExternalStorageDirectory().getAbsolutePath()+"/edited_%d.png",now));
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            fos = null;
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





    protected void init() {
        mPath = new Path();

        mPaint = new Paint(Paint.DITHER_FLAG);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(getContext().getResources().getColor(android.R.color.holo_blue_dark));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(TOUCH_STROKE_WIDTH);


        mPaintFinal = new Paint(Paint.DITHER_FLAG);
        mPaintFinal.setAntiAlias(true);
        mPaintFinal.setDither(true);
        mPaintFinal.setColor(getContext().getResources().getColor(android.R.color.holo_orange_dark));
        mPaintFinal.setStyle(Paint.Style.STROKE);
        mPaintFinal.setStrokeJoin(Paint.Join.ROUND);
        mPaintFinal.setStrokeCap(Paint.Cap.ROUND);
        mPaintFinal.setStrokeWidth(TOUCH_STROKE_WIDTH);


    }

    public void reset() {
        mPath = new Path();
        countTouch=0;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mx = event.getX();
        my = event.getY();
        switch (mCurrentShape) {
            case LINE:
                onTouchEventLine(event);
                break;
            case SMOOTHLINE:
                onTouchEventSmoothLine(event);
                break;
            case RECTANGLE:
                onTouchEventRectangle(event);
                break;
            case SQUARE:
                onTouchEventSquare(event);
                break;
            case CIRCLE:
                onTouchEventCircle(event);
                break;
            case TRIANGLE:
                onTouchEventTriangle(event);
                break;
        }
        return true;
    }



    //------------------------------------------------------------------
    // Line
    //------------------------------------------------------------------

    private void onDrawLine(Canvas canvas) {

        float dx = Math.abs(mx - mStartX);
        float dy = Math.abs(my - mStartY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            canvas.drawLine(mStartX, mStartY, mx, my, mPaint);
        }
    }

    private void onTouchEventLine(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDrawing = true;
                mStartX = mx;
                mStartY = my;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isDrawing = false;
                mCanvas.drawLine(mStartX, mStartY, mx, my, mPaintFinal);
                invalidate();
                break;
        }
    }

    //------------------------------------------------------------------
    // Smooth Line
    //------------------------------------------------------------------


    private void onTouchEventSmoothLine(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDrawing = true;
                mStartX = mx;
                mStartY = my;

                mPath.reset();
                mPath.moveTo(mx, my);

                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:

                float dx = Math.abs(mx - mStartX);
                float dy = Math.abs(my - mStartY);
                if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                    mPath.quadTo(mStartX, mStartY, (mx + mStartX) / 2, (my + mStartY) / 2);
                    mStartX = mx;
                    mStartY = my;
                }
                mCanvas.drawPath(mPath, mPaint);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isDrawing = false;
                mPath.lineTo(mStartX, mStartY);
                mPath.reset();
                invalidate();
                break;
        }
    }

    //------------------------------------------------------------------
    // Triangle
    //------------------------------------------------------------------

    int countTouch =0;
    float basexTriangle =0;
    float baseyTriangle =0;

    private void onDrawTriangle(Canvas canvas){

        if (countTouch<3){
            canvas.drawLine(mStartX,mStartY,mx,my,mPaint);
        }else if (countTouch==3){
            canvas.drawLine(mx,my,mStartX,mStartY,mPaint);
            canvas.drawLine(mx,my,basexTriangle,baseyTriangle,mPaint);
        }
    }

    private void onTouchEventTriangle(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                countTouch++;
                if (countTouch==1){
                    isDrawing = true;
                    mStartX = mx;
                    mStartY = my;
                } else if (countTouch==3){
                    isDrawing = true;
                }
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                countTouch++;
                isDrawing = false;
                if (countTouch<3){
                    basexTriangle=mx;
                    baseyTriangle=my;
                    mCanvas.drawLine(mStartX,mStartY,mx,my,mPaintFinal);
                } else if (countTouch>=3){
                    mCanvas.drawLine(mx,my,mStartX,mStartY,mPaintFinal);
                    mCanvas.drawLine(mx,my,basexTriangle,baseyTriangle,mPaintFinal);
                    countTouch =0;
                }
                invalidate();
                break;
        }
    }

    //------------------------------------------------------------------
    // Circle
    //------------------------------------------------------------------

    private void onDrawCircle(Canvas canvas){
        canvas.drawCircle(mStartX, mStartY, calculateRadius(mStartX, mStartY, mx, my), mPaint);
    }

    private void onTouchEventCircle(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDrawing = true;
                mStartX = mx;
                mStartY = my;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isDrawing = false;
                mCanvas.drawCircle(mStartX, mStartY, calculateRadius(mStartX,mStartY,mx,my), mPaintFinal);
                invalidate();
                break;
        }
    }

    /**
     *
     * @return
     */
    protected float calculateRadius(float x1, float y1, float x2, float y2) {

        return (float) Math.sqrt(
                Math.pow(x1 - x2, 2) +
                        Math.pow(y1 - y2, 2)
        );
    }

    //------------------------------------------------------------------
    // Rectangle
    //------------------------------------------------------------------

    private void onDrawRectangle(Canvas canvas) {
        drawRectangle(canvas,mPaint);
    }

    private void onTouchEventRectangle(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDrawing = true;
                mStartX = mx;
                mStartY = my;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isDrawing = false;
                drawRectangle(mCanvas,mPaintFinal);
                invalidate();
                break;
        }
        ;
    }

    private void drawRectangle(Canvas canvas,Paint paint){
        float right = mStartX > mx ? mStartX : mx;
        float left = mStartX > mx ? mx : mStartX;
        float bottom = mStartY > my ? mStartY : my;
        float top = mStartY > my ? my : mStartY;
        canvas.drawRect(left, top , right, bottom, paint);
    }

    //------------------------------------------------------------------
    // Square
    //------------------------------------------------------------------

    private void onDrawSquare(Canvas canvas) {
        onDrawRectangle(canvas);
    }

    private void onTouchEventSquare(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDrawing = true;
                mStartX = mx;
                mStartY = my;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                adjustSquare(mx, my);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isDrawing = false;
                adjustSquare(mx, my);
                drawRectangle(mCanvas,mPaintFinal);
                invalidate();
                break;
        }
    }

    /**
     * Adjusts current coordinates to build a square
     * @param x
     * @param y
     */
    protected void adjustSquare(float x, float y) {
        float deltaX = Math.abs(mStartX - x);
        float deltaY = Math.abs(mStartY - y);

        float max = Math.max(deltaX, deltaY);

        mx = mStartX - x < 0 ? mStartX + max : mStartX - max;
        my = mStartY - y < 0 ? mStartY + max : mStartY - max;
    }


}
