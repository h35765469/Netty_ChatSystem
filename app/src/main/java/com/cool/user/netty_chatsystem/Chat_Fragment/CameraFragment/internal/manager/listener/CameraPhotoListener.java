package com.cool.user.netty_chatsystem.Chat_Fragment.CameraFragment.internal.manager.listener;


import com.cool.user.netty_chatsystem.Chat_Fragment.CameraFragment.CameraFragmentResultListener;

import java.io.File;

/*
 * Created by memfis on 8/14/16.
 */
public interface CameraPhotoListener {
    void onPhotoTaken(byte[] bytes, File photoFile, CameraFragmentResultListener callback);

    void onPhotoTakeError();
}
