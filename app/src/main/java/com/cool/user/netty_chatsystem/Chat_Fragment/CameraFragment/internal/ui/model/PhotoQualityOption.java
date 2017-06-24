package com.cool.user.netty_chatsystem.Chat_Fragment.CameraFragment.internal.ui.model;



/*
 * Created by memfis on 12/1/16.
 */

import com.cool.user.netty_chatsystem.Chat_Fragment.CameraFragment.Configuration;
import com.cool.user.netty_chatsystem.Chat_Fragment.CameraFragment.internal.utils.Size;

public class PhotoQualityOption implements CharSequence {

    @Configuration.MediaQuality
    private int mediaQuality;
    private String title;

    public PhotoQualityOption(@Configuration.MediaQuality int mediaQuality, Size size) {
        this.mediaQuality = mediaQuality;

        title = String.valueOf(size.getWidth()) + " x " + String.valueOf(size.getHeight());
    }

    @Configuration.MediaQuality
    public int getMediaQuality() {
        return mediaQuality;
    }

    @Override
    public int length() {
        return title.length();
    }

    @Override
    public char charAt(int index) {
        return title.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return title.subSequence(start, end);
    }

    @Override
    public String toString() {
        return title;
    }
}
