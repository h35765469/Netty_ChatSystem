package com.cool.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.Camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import com.cool.user.netty_chatsystem.R;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

import java.io.FileOutputStream;

public class FaceOverlayView extends View {
    private Bitmap mBitmap;
    private SparseArray<Face> mFaces;
    int picAddNum2 = 0;
    float xOffset2 = 0;
    double scale2 = 0;
    String mediaPath2;
    Bitmap resizedBitmap;
    int cx2;
    int cy2;

    public FaceOverlayView(Context context) {
        this(context, null);
    }

    public FaceOverlayView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FaceOverlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setBitmap( Bitmap bitmap ) {
        mBitmap = bitmap;
        FaceDetector detector = new FaceDetector.Builder( getContext() )
                .setTrackingEnabled(true)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setMode(FaceDetector.ACCURATE_MODE)
                .build();

        if (!detector.isOperational()) {
            //Handle contingency
        } else {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            mFaces = detector.detect(frame);
            detector.release();
        }
        logFaceData();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if ((mBitmap != null) && (mFaces != null)) {
            double viewWidth = canvas.getWidth();
            double viewHeight = canvas.getHeight();
            double imageWidth = mBitmap.getWidth();
            double imageHeight = mBitmap.getHeight();
            double scale  = Math.min(viewWidth / imageWidth, viewHeight / imageHeight);

            Rect destBounds = new Rect( 0, 0, (int) ( imageWidth * scale ), (int) ( imageHeight * scale ) );
            canvas.drawBitmap(mBitmap, null, destBounds, null);

            Bitmap bitmap = Bitmap.createBitmap( (int) ( imageWidth * scale ),(int) ( imageHeight * scale ), Bitmap.Config.ARGB_8888);
            Canvas canvas2 = new Canvas(bitmap);
            canvas2.drawBitmap(mBitmap, null, destBounds, null);
            canvas.drawBitmap(mBitmap, null, destBounds, null);

            Paint paint = new Paint();
            paint.setColor(Color.GREEN);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(5);
            int checkPicAdd = 0;
            for( int i = 0; i < mFaces.size(); i++ ) {
                Face face = mFaces.valueAt(i);

                for ( Landmark landmark : face.getLandmarks() ) {
                    float cx = (int) ( landmark.getPosition().x * scale );
                    float cy = (int) ( landmark.getPosition().y * scale );
//                    canvas.drawCircle( cx, cy, 10, paint );
//                    canvas2.drawCircle( cx, cy, 10, paint );
                    switch (landmark.getType()){
//                        case Landmark.LEFT_EYE: //1
//                            checkPicAdd = 1;
//                            if(checkPicAdd == picAddNum2){
//
//                            }
//                            break;
//                        case Landmark.RIGHT_EYE: //2
//                            break;
//                        case Landmark.BOTTOM_MOUTH: //3
//                            break;
//                        case Landmark.LEFT_MOUTH: //4
//                            break;
//                        case Landmark.RIGHT_MOUTH: //5
//                            break;
                        case Landmark.NOSE_BASE: //6
                            checkPicAdd = 6;
                            if(checkPicAdd == picAddNum2){
//                                double positionX = landmark.getPosition().x;
//                                double positionY = landmark.getPosition().y;
//                                movPic(positionX, positionY);
//                                canvas.drawBitmap(resizedBitmap, cx2, cy2, null);  //縮放
//                                canvas2.drawBitmap(resizedBitmap, cx2, cy2, null);  //縮放
                                Bitmap bmps = BitmapFactory.decodeResource(getResources(), R.drawable.m);
                                canvas.drawBitmap(bmps, (cx-(bmps.getWidth()/2)), (cy-(bmps.getHeight()/2)), paint);
                                canvas2.drawBitmap(bmps, (cx-(bmps.getWidth()/2)), (cy-(bmps.getHeight()/2)), paint);
                            }
                            break;
//                        case Landmark.LEFT_CHEEK: //7
//                            break;
//                        case Landmark.RIGHT_CHEEK: //8
//                            break;
//                        case Landmark.LEFT_EAR: //9
//                            break;
//                        case Landmark.LEFT_EAR_TIP: //10
//                            break;
//                        case Landmark.RIGHT_EAR: //11
//                            break;
//                        case Landmark.RIGHT_EAR_TIP: //12
//                            break;
                    }

                }
            }
            //保存全部图层
            canvas2.save(Canvas.ALL_SAVE_FLAG);
            canvas2.restore();
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(mediaPath2);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void picAddNum(int picAddNum, float xOffset, double scale , String mediaPath) {
        picAddNum2 = picAddNum;
        xOffset2 = xOffset;
        scale2 = scale;
        mediaPath2 = mediaPath;
    }
    public void movPic(double positionX, double positionY){
        Bitmap bitmapOrg = BitmapFactory.decodeResource(getResources(), R.drawable.m);
        int width = bitmapOrg.getWidth();
        int height = bitmapOrg.getHeight();
        // calculate the scale
        float scaleWidth = (float)((xOffset2/width));
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleWidth);
        // this will create image with new size
        resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0,width, height, matrix, true);
        cx2 = (int) (( (480-positionX)  * scale2 )-(resizedBitmap.getWidth()/2));
        cy2 = (int) (( positionY * scale2 )-(resizedBitmap.getHeight()/2));
    }
    private void drawFaceBox(Canvas canvas, double scale) {
        //paint should be defined as a member variable rather than
        //being created on each onDraw request, but left here for
        //emphasis.
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);

        float left = 0;
        float top = 0;
        float right = 0;
        float bottom = 0;

        for( int i = 0; i < mFaces.size(); i++ ) {
            Face face = mFaces.valueAt(i);

            left = (float) ( face.getPosition().x * scale );
            top = (float) ( face.getPosition().y * scale );
            right = (float) scale * ( face.getPosition().x + face.getWidth() );
            bottom = (float) scale * ( face.getPosition().y + face.getHeight() );

            canvas.drawRect( left, top, right, bottom, paint );
        }
    }

    private void logFaceData() {
        float smilingProbability;
        float leftEyeOpenProbability;
        float rightEyeOpenProbability;
        float eulerY;
        float eulerZ;
        for( int i = 0; i < mFaces.size(); i++ ) {
            Face face = mFaces.valueAt(i);

            smilingProbability = face.getIsSmilingProbability();
            leftEyeOpenProbability = face.getIsLeftEyeOpenProbability();
            rightEyeOpenProbability = face.getIsRightEyeOpenProbability();
            eulerY = face.getEulerY();
            eulerZ = face.getEulerZ();

            Log.e( "Tuts+ Face Detection", "Smiling: " + smilingProbability );
            Log.e( "Tuts+ Face Detection", "Left eye open: " + leftEyeOpenProbability );
            Log.e( "Tuts+ Face Detection", "Right eye open: " + rightEyeOpenProbability );
            Log.e( "Tuts+ Face Detection", "Euler Y: " + eulerY );
            Log.e("Tuts+ Face Detection", "Euler Z: " + eulerZ);
        }
    }
}
