/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cool.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.Camera;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.cool.user.netty_chatsystem.Chat_MySQL.Config;
import com.cool.user.netty_chatsystem.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/** Activity for the face tracker app.  This app detects faces with the rear facing camera, and draws overlay graphics to indicate the position, size, and ID of each face. */
public final class FaceTrackerActivity extends AppCompatActivity {
    private static final String TAG = "FaceTracker";
    private CameraSource mCameraSource = null;
    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;
    private Button button;
    private static final int RC_HANDLE_GMS = 9001;
    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    DisplayMetrics metrics;
    public static String mediaName;
    public static String mediaPath;
    //==============================================================================================
    // Activity Methods
    //==============================================================================================
    /** Initializes the UI and initiates the creation of a face detector. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_facetracker);
        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        //mGraphicOverlay = (GraphicOverlay) findViewById(R.id.faceOverlay);
        button = (Button)findViewById(R.id.button);
       metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        // Check for the camera permission before accessing the camera.  If the permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }
        button.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                mCameraSource.takePicture(null, new CameraSource.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] bytes) {
                        Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);    //產生Bitmap物件

                        Matrix matrix = new Matrix();  //把照片轉270度成新的檔案rotatedBitmap再儲存
                        matrix.postRotate(270);
                        Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);

                        FileOutputStream fos = null;
                        try {

                            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), Config.IMAGE_DIRECTORY_NAME);
                            // Create the storage directory if it does not exist
                            if (!mediaStorageDir.exists()) {
                                if (!mediaStorageDir.mkdirs()) {
                                    Log.d(TAG, "Oops! Failed create " + Config.IMAGE_DIRECTORY_NAME + " directory");
                                }
                            }
                            // Create a media file name
                            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                            File file;
                            file = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
                            mediaName = "IMG_" + timeStamp + ".jpg ";
                            mediaPath = file.getPath();

                            fos = new FileOutputStream(file);
                            BufferedOutputStream bos = new BufferedOutputStream(fos);
                            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);   //Bitmap類別的compress方法產生檔案
                            bos.flush();
                            bos.close();
                            if (ContextCompat.checkSelfPermission(FaceTrackerActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                mCameraSource.start();  //因為takePicture會停止預覽功能，故重新呼叫startPreview啟動預覽
                            } else {
                                mCameraSource.start();
                            }

                            // 指定要呼叫的 Activity Class
                            Intent newAct = new Intent();
                            newAct.setClass(FaceTrackerActivity.this, MainActivity2.class);
                            newAct.putExtra("picAddNum", FaceGraphic.picAddNum);
                            newAct.putExtra("xOffset", FaceGraphic.xOffset);
                            newAct.putExtra("scale", FaceGraphic.scale);
                            newAct.putExtra("mediaName",mediaName);
                            newAct.putExtra("mediaPath",mediaPath);
                            // 呼叫新的 Activity Class
                            startActivity(newAct);
                            //setResult(RESULT_OK,newAct);
                            finish();
                        } catch (Exception e) {
                        }
                    }
                });
            }
        });

        ImageView changeCameraImg = (ImageView)findViewById(R.id.changeCameraImg);
        changeCameraImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        // Toast.makeText(this, "metrics.heightPixels" + metrics.heightPixels + " metrics.widthPixels" + metrics.widthPixels, Toast.LENGTH_LONG).show();
    }
    /** Handles the requesting of the camera permission.  This includes showing a "Snackbar" message of why the permission is needed then sending the request. */
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");
        final String[] permissions = new String[]{Manifest.permission.CAMERA};
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }
        final Activity thisActivity = this;
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };
    }
    /** Creates and starts the camera.  Note that this uses a higher resolution in comparison to other detection examples to enable the barcode detector to detect small barcodes at long distances. */
    private void createCameraSource() {
        Context context = getApplicationContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setMode(FaceDetector.FAST_MODE)
                .setTrackingEnabled(false)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setProminentFaceOnly(true)
                .build();
        /*detector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                        .build());*/

//        detector.setProcessor(
//                new LargestFaceFocusingProcessor(detector, new GraphicFaceTracker(mGraphicOverlay)));
        if (!detector.isOperational()) {
            // Note: The first time that an app using face API is installed on a device, GMS will
            // download a native library to the device in order to do detection.  Usually this
            // completes before the app is run for the first time.  But if that download has not yet
            // completed, then the above call will not detect any faces.
            //
            // isOperational() can be used to check if the required native library is currently
            // available.  The detector will automatically become operational once the library
            // download completes on device.
            Log.w(TAG, "Face detector dependencies are not yet available.");
        }
        mCameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(640,480)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(30.0f)
                .setAutoFocusEnabled(true)
                .build();

    }
    /** Restarts the camera. */
    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }
    /** Stops the camera. */
    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
    }
    /** Releases the resources associated with the camera source, the associated detector, and the rest of the processing pipeline. */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }
    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }
        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            createCameraSource();
            return;
        }
        Log.e(TAG, "Permission not granted: results len = " + grantResults.length + " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Face Tracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }
    //==============================================================================================
    // Camera Source Preview
    //==============================================================================================
    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() {
        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }
        if (mCameraSource != null) {
            try {
                //mPreview.start(mCameraSource, mGraphicOverlay);
                mPreview.start(mCameraSource);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }
    //==============================================================================================
    // Graphic Face Tracker
    //==============================================================================================
    /**
     * Factory for creating a face tracker to be associated with a new face.  The multiprocessor uses this factory to create face trackers as needed -- one for each individual. */
    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new GraphicFaceTracker(mGraphicOverlay);
        }
    }
    /** Face tracker for each detected individual. This maintains a face graphic within the app's associated face overlay. */
    private class GraphicFaceTracker extends Tracker<Face> {
        Context ct = getApplicationContext();
        private GraphicOverlay mOverlay;
        private FaceGraphic mFaceGraphic;
        GraphicFaceTracker(GraphicOverlay overlay) {
            mOverlay = overlay;
            mFaceGraphic = new FaceGraphic(overlay,ct);
        }
        /** Start tracking the detected face instance within the face overlay. */
        @Override
        public void onNewItem(int faceId, Face item) {
            mFaceGraphic.setId(faceId);
        }
        /** Update the position/characteristics of the face within the overlay. */
        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
            mOverlay.add(mFaceGraphic);
            mFaceGraphic.updateFace(face);
        }
        /** Hide the graphic when the corresponding face was not detected.  This can happen for intermediate frames temporarily (e.g., if the face was momentarily blocked from view). */
        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            mOverlay.remove(mFaceGraphic);
        }
        /** Called when the face is assumed to be gone for good. Remove the graphic annotation from the overlay. */
        @Override
        public void onDone() {
            mOverlay.remove(mFaceGraphic);
        }
    }
}