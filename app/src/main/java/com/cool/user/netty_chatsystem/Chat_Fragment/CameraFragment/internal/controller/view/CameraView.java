package com.cool.user.netty_chatsystem.Chat_Fragment.CameraFragment.internal.controller.view;

import android.support.annotation.Nullable;
import android.view.View;

import com.cool.user.netty_chatsystem.Chat_Fragment.CameraFragment.CameraFragmentResultListener;
import com.cool.user.netty_chatsystem.Chat_Fragment.CameraFragment.Configuration;
import com.cool.user.netty_chatsystem.Chat_Fragment.CameraFragment.internal.utils.Size;


/*
 * Created by memfis on 7/6/16.
 */
public interface CameraView {

    void updateCameraPreview(Size size, View cameraPreview);

    void updateUiForMediaAction(@Configuration.MediaAction int mediaAction);

    void updateCameraSwitcher(int numberOfCameras);

    void onPhotoTaken(byte[] bytes, @Nullable CameraFragmentResultListener callback);

    void onVideoRecordStart(int width, int height);

    void onVideoRecordStop(@Nullable CameraFragmentResultListener callback);

    void releaseCameraPreview();

}
