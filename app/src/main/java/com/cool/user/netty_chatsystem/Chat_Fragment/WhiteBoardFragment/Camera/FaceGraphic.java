package com.cool.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.Camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Build;

import com.cool.user.netty_chatsystem.R;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;

/**
 * Created by user on 2016/12/23.
 */
class FaceGraphic extends GraphicOverlay.Graphic {
    private Context context;
    private static final float FACE_POSITION_RADIUS = 10.0f;
    private static final float ID_TEXT_SIZE = 40.0f;
    private static final float ID_Y_OFFSET = 50.0f;
    private static final float ID_X_OFFSET = -50.0f;
    private static final float BOX_STROKE_WIDTH = 5.0f;
    private static final int COLOR_CHOICES[] = {
            Color.BLUE,
            Color.CYAN,
            Color.GREEN,
            Color.MAGENTA,
            Color.RED,
            Color.WHITE,
            Color.YELLOW
    };
    private static int mCurrentColorIndex = 0;
    private Paint mFacePositionPaint;
    private Paint mIdPaint;
    private Paint mBoxPaint;
    private volatile Face mFace;
    private int mFaceId;
    Bitmap resizedBitmap;
    int cx2;
    int cy2;
    Face face;
    static float xOffset;
    float yOffset;
    float left;
    float top;
    float right;
    float bottom;
    double viewWidth;
    double viewHeight;
    double imageWidth;
    double imageHeight;
    static double scale;
    static float[][] landarray = new float[12][5];
    static int picAddNum =0;

    FaceGraphic(GraphicOverlay overlay, Context current) {
        super(overlay);
        this.context = current;
        mCurrentColorIndex = (mCurrentColorIndex + 1) % COLOR_CHOICES.length;
        final int selectedColor = COLOR_CHOICES[mCurrentColorIndex];
        mFacePositionPaint = new Paint();
        mFacePositionPaint.setColor(selectedColor);
        mIdPaint = new Paint();
        mIdPaint.setColor(selectedColor);
        mIdPaint.setTextSize(ID_TEXT_SIZE);
        mBoxPaint = new Paint();
        mBoxPaint.setColor(selectedColor);
        mBoxPaint.setStyle(Paint.Style.STROKE);
        mBoxPaint.setStrokeWidth(BOX_STROKE_WIDTH);
    }
    void setId(int id) {
        mFaceId = id;
    }
    /** Updates the face instance from the detection of the most recent frame.  Invalidates the relevant portions of the overlay to trigger a redraw. */
    void updateFace(Face face) {
        mFace = face;
        postInvalidate();
    }
    /** Draws the face annotations for position on the supplied canvas. */
    @Override
    public void draw(Canvas canvas) {
        face = mFace;
        if (face == null) {
            return;
        }
        // Draws a circle at the position of the detected face, with the face's track id below.
        float x = translateX(face.getPosition().x + face.getWidth() / 2);
        float y = translateY(face.getPosition().y + face.getHeight() / 2);
//        canvas.drawCircle(x, y, FACE_POSITION_RADIUS, mFacePositionPaint);
//        canvas.drawText("id: " + mFaceId, x + ID_X_OFFSET, y + ID_Y_OFFSET, mIdPaint);
//        canvas.drawText("happiness: " + String.format("%.2f", face.getIsSmilingProbability()), x - ID_X_OFFSET, y - ID_Y_OFFSET, mIdPaint);
//        canvas.drawText("right eye: " + String.format("%.2f", face.getIsRightEyeOpenProbability()), x + ID_X_OFFSET * 2, y + ID_Y_OFFSET * 2, mIdPaint);
//        canvas.drawText("left eye: " + String.format("%.2f", face.getIsLeftEyeOpenProbability()), x - ID_X_OFFSET*2, y - ID_Y_OFFSET*2, mIdPaint);


        // Draws a bounding box around the face.
        xOffset = scaleX(face.getWidth() / 2.0f);
        yOffset = scaleY(face.getHeight() / 2.0f);
        left = x - xOffset;
        top = y - yOffset;
        right = x + xOffset;
        bottom = y + yOffset;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawOval(left, top, right, bottom, mBoxPaint);
        } else {
            canvas.drawCircle(x, y, Math.max(xOffset, yOffset), mBoxPaint);
        }
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);

        viewWidth = canvas.getWidth();
        viewHeight = canvas.getHeight();
        imageWidth = 480;
        imageHeight = 640;
        scale = Math.min(viewWidth / imageWidth, viewHeight / imageHeight);
        land();
        for(int i=1;i<landarray.length ; i++){
            if(landarray[i][1] == 1){
                canvas.drawCircle( landarray[i][2], landarray[i][3], 10, mFacePositionPaint);
                if(landarray[i][4] == 1){
                    canvas.drawBitmap(resizedBitmap, cx2, cy2, mFacePositionPaint);  //縮放
                }
            }
        }
    }
    public void land(){
        face = mFace;
        for ( Landmark landmark : face.getLandmarks() ) {
            float cx = (float) ( (480-landmark.getPosition().x)  * scale );
            float cy = (float) ( landmark.getPosition().y * scale  );
            switch (landmark.getType()){
//                case Landmark.LEFT_EYE: //1
//                    landarray[1][1] = 1;
//                    landarray[1][2] = cx;
//                    landarray[1][3] = cy;
//                    break;
//                case Landmark.RIGHT_EYE: //2
//                    landarray[2][1] = 1;
//                    landarray[2][2] = cx;
//                    landarray[2][3] = cy;
//                    break;
//                case Landmark.BOTTOM_MOUTH: //3
//                    landarray[3][1] = 1;
//                    landarray[3][2] = cx;
//                    landarray[3][3] = cy;
//                    break;
//                case Landmark.LEFT_MOUTH: //4
//                    landarray[4][1] = 1;
//                    landarray[4][2] = cx;
//                    landarray[4][3] = cy;
//                    break;
//                case Landmark.RIGHT_MOUTH: //5
//                    landarray[5][1] = 1;
//                    landarray[5][2] = cx;
//                    landarray[5][3] = cy;
//                    break;
                case Landmark.NOSE_BASE: //6
                    landarray[6][1] = 1;
                    landarray[6][2] = cx;
                    landarray[6][3] = cy;
                    landarray[6][4] = 1;
                    picAddNum = 6;
                    double positionX = landmark.getPosition().x;
                    double positionY = landmark.getPosition().y;
                    movPic(positionX, positionY);
                    break;
//                case Landmark.LEFT_CHEEK: //7
//                    landarray[7][1] = 1;
//                    landarray[7][2] = cx;
//                    landarray[7][3] = cy;
//                    break;
//                case Landmark.RIGHT_CHEEK: //8
//                    landarray[8][1] = 1;
//                    landarray[8][2] = cx;
//                    landarray[8][3] = cy;
//                    break;
//                case Landmark.LEFT_EAR: //9
//                    landarray[9][1] = 1;
//                    landarray[9][2] = cx;
//                    landarray[9][3] = cy;
//                    break;
//                case Landmark.LEFT_EAR_TIP: //10
//                    landarray[10][1] = 1;
//                    landarray[10][2] = cx;
//                    landarray[10][3] = cy;
//                    break;
//                case Landmark.RIGHT_EAR: //11
//                    landarray[11][1] = 1;
//                    landarray[11][2] = cx;
//                    landarray[11][3] = cy;
//                    break;
//                case Landmark.RIGHT_EAR_TIP: //12
//                    landarray[12][1] = 1;
//                    landarray[12][2] = cx;
//                    landarray[12][3] = cy;
//                    break;
            }
        }
    }
    public void movPic(double positionX, double positionY){
        Bitmap bitmapOrg = BitmapFactory.decodeResource(context.getResources(), R.drawable.m);
        int width = bitmapOrg.getWidth();
        int height = bitmapOrg.getHeight();
        // calculate the scale
        float scaleWidth = (float)((xOffset/width));
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleWidth);
        // this will create image with new size
        resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0,width, height, matrix, true);
        cx2 = (int) (( (480-positionX)  * scale )-(resizedBitmap.getWidth()/2));
        cy2 = (int) (( positionY * scale )-(resizedBitmap.getHeight()/2));
    }
}