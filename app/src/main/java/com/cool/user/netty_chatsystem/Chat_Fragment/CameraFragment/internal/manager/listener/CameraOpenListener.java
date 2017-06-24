package com.cool.user.netty_chatsystem.Chat_Fragment.CameraFragment.internal.manager.listener;


import com.cool.user.netty_chatsystem.Chat_Fragment.CameraFragment.internal.utils.Size;

/*
 * Created by memfis on 8/14/16.
 */
public interface CameraOpenListener<CameraId, SurfaceListener> {
    void onCameraOpened(CameraId openedCameraId, Size previewSize, SurfaceListener surfaceListener);

    void onCameraOpenError();
}
