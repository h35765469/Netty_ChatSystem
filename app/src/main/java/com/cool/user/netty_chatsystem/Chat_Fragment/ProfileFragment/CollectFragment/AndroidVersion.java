package com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.CollectFragment;

/**
 * Created by user on 2017/2/17.
 */
public class AndroidVersion {
    private String android_version_name;
    private String android_image_url;
    private int collectCount;

    public String getAndroid_version_name() {
        return android_version_name;
    }

    public void setAndroid_version_name(String android_version_name) {
        this.android_version_name = android_version_name;
    }

    public String getAndroid_image_url() {
        return android_image_url;
    }

    public void setAndroid_image_url(String android_image_url) {
        this.android_image_url = android_image_url;
    }

    public int getCollectCount(){
        return collectCount;
    }

    public void setCollectCount(int collectCount){
        this.collectCount = collectCount;
    }
}
