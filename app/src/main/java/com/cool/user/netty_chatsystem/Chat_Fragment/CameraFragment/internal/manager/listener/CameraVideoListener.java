package com.cool.user.netty_chatsystem.Chat_Fragment.CameraFragment.internal.manager.listener;


import com.cool.user.netty_chatsystem.Chat_Fragment.CameraFragment.internal.utils.Size;
import com.cool.user.netty_chatsystem.Chat_Fragment.CameraFragment.CameraFragmentResultListener;

import java.io.File;

/*
 * Created by memfis on 8/14/16.
 */
public interface CameraVideoListener {
    void onVideoRecordStarted(Size videoSize);

    void onVideoRecordStopped(File videoFile, CameraFragmentResultListener callback);

    void onVideoRecordError();
}
