package com.cool.user.netty_chatsystem.Chat_Fragment.CameraFragment.internal.manager;

import android.content.Context;

import com.cool.user.netty_chatsystem.Chat_Fragment.CameraFragment.Configuration;
import com.cool.user.netty_chatsystem.Chat_Fragment.CameraFragment.ConfigurationProvider;
import com.cool.user.netty_chatsystem.Chat_Fragment.CameraFragment.internal.manager.listener.CameraCloseListener;
import com.cool.user.netty_chatsystem.Chat_Fragment.CameraFragment.internal.manager.listener.CameraOpenListener;
import com.cool.user.netty_chatsystem.Chat_Fragment.CameraFragment.internal.manager.listener.CameraPhotoListener;
import com.cool.user.netty_chatsystem.Chat_Fragment.CameraFragment.internal.manager.listener.CameraVideoListener;
import com.cool.user.netty_chatsystem.Chat_Fragment.CameraFragment.internal.utils.Size;
import com.cool.user.netty_chatsystem.Chat_Fragment.CameraFragment.CameraFragmentResultListener;

import java.io.File;

/*
 * Created by memfis on 8/14/16.
 */
public interface CameraManager<CameraId, SurfaceListener> {

    void initializeCameraManager(ConfigurationProvider configurationProvider, Context context);

    void openCamera(CameraId cameraId, CameraOpenListener<CameraId, SurfaceListener> cameraOpenListener);

    void closeCamera(CameraCloseListener<CameraId> cameraCloseListener);

    void setFlashMode(@Configuration.FlashMode int flashMode);

    void takePhoto(File photoFile, CameraPhotoListener cameraPhotoListener, CameraFragmentResultListener callback);

    void startVideoRecord(File videoFile, CameraVideoListener cameraVideoListener);

    Size getPhotoSizeForQuality(@Configuration.MediaQuality int mediaQuality);

    void stopVideoRecord(CameraFragmentResultListener callback);

    void releaseCameraManager();

    CameraId getCurrentCameraId();

    CameraId getFaceFrontCameraId();

    CameraId getFaceBackCameraId();

    int getNumberOfCameras();

    int getFaceFrontCameraOrientation();

    int getFaceBackCameraOrientation();

    boolean isVideoRecording();

    CharSequence[] getVideoQualityOptions();

    CharSequence[] getPhotoQualityOptions();

    void setCameraId(CameraId currentCameraId);
}
