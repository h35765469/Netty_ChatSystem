package com.cool.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.Chat_AnimationElement.BubbleEffect;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;

import com.cool.user.netty_chatsystem.R;

/**
 * Created by user on 2016/10/4.
 */
public class BubbleView extends View {
    public final static int DOWN = 0;
    public final static int UP = 1;
    public final static int LEFT = 2;
    public final static int RIGHT = 3;
    private static final float SMALL = 0.1f;
    private static final float BIG = 0.5f;
    private final int RANGE = 20;
    private final Bitmap mBitmap;
    private final int[] bitmapX;
    private final int[] bitmapY;
    private int screenWidth;//手機螢幕的寬
    private int screenHeight;//手機螢幕的高
    private final static int RANDOM_SPEED = 7;
    private final static int BUBBLE_COUNT = 70;

    private int randomSpeed;//隨機移動的pixel
    private int bubbleCount;//泡泡的數量
    private int direction;
    private Paint mPaint;

    public BubbleView(Context context, int screenWidth, int screenHeight){
        super(context);
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        mBitmap = ((BitmapDrawable) this.getResources().getDrawable(
                R.drawable.bubble)).getBitmap();

        bubbleCount = BUBBLE_COUNT;
        randomSpeed = RANDOM_SPEED;
        direction = RIGHT;
        bitmapX = new int[bubbleCount];
        bitmapY = new int[bubbleCount];

        for(int i=0;i<bubbleCount;i++){
            bitmapX[i] = (int)(Math.random()*this.screenWidth+RANGE);
            bitmapY[i] = (int)(Math.random()*this.screenHeight+RANGE);
        }
    }
    private Bitmap bubbleSize(float size){
        Matrix matrix = new Matrix();
        matrix.postScale(size, size);
        Bitmap resizedBitmap = Bitmap.createBitmap(mBitmap,
                0, 0, mBitmap.getWidth(), mBitmap.getHeight(),matrix,true);
        return resizedBitmap;
    }
    private Bitmap bubbleRotation(int angle){
        Matrix matrix = new Matrix();
        matrix.setRotate(angle);
        Bitmap RotateBitmap = Bitmap.createBitmap(mBitmap,
                0, 0, mBitmap.getWidth(), mBitmap.getHeight(),matrix,true);
        return RotateBitmap;
    }
    public void setDirection(int direction){
        this.direction = direction;
    }
    public void decidedDirection(){

        for(int i=0;i<bubbleCount;i++){

            if(bitmapX[i]>=-1*RANGE && bitmapX[i] <=screenWidth+RANGE &&
                    bitmapY[i]>=-1*RANGE && bitmapY[i]<=screenHeight+RANGE){

                switch(direction){
                    case DOWN:
                        bitmapX[i] += Math.random()*randomSpeed;
                        bitmapY[i] += Math.random()*randomSpeed;
                        break;
                    case UP:
                        bitmapX[i] += Math.random()*randomSpeed;
                        bitmapY[i] -= Math.random()*randomSpeed;
                        break;
                    case LEFT:
                        bitmapX[i] -= Math.random()*randomSpeed;
                        bitmapY[i] += Math.random()*randomSpeed;
                        break;
                    case RIGHT:
                        bitmapX[i] -= Math.random()*randomSpeed;
                        bitmapY[i] -= Math.random()*randomSpeed;
                        break;
                }
            }
            else{
                bitmapX[i] = (int)(Math.random()*screenWidth);
                bitmapY[i] = (int)(Math.random()*screenHeight);
            }

        }
    }
    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        mPaint = new Paint();
        mPaint.setAntiAlias( true );
        for(int i=0;i<bubbleCount;i++){
            if(i%4 == 0){
                int angle = (int)(Math.random()*2);
                canvas.drawBitmap(bubbleRotation(angle*180),
                        bitmapX[i], bitmapY[i], mPaint);
            }
            else if(i%4 == 1){
                canvas.drawBitmap(bubbleSize(SMALL),
                        bitmapX[i], bitmapY[i], mPaint);
            }
            else if(i%4 == 2){
                canvas.drawBitmap(bubbleSize(BIG),
                        bitmapX[i], bitmapY[i], mPaint);
            }
            else{
                canvas.drawBitmap(mBitmap, bitmapX[i], bitmapY[i], mPaint);
            }
        }
        decidedDirection();
        invalidate();
    }
}
