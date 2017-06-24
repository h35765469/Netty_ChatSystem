package com.cool.user.netty_chatsystem.Chat_Fragment.CameraFragment;

import android.Manifest;
import android.support.annotation.RequiresPermission;

import com.cool.user.netty_chatsystem.Chat_Fragment.CameraFragment.internal.ui.BaseAnncaFragment;


public class CameraFragment extends BaseAnncaFragment {

    @RequiresPermission(Manifest.permission.CAMERA)
    public static CameraFragment newInstance(Configuration configuration) {
        return (CameraFragment) BaseAnncaFragment.newInstance(new CameraFragment(), configuration);
    }

}
