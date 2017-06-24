package com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.StickerFragment;

/**
 * Created by user on 2016/12/21.
 */
//儲存sticker的資料
class StickerData {
    String stickerPath;

    StickerData(String stickerPath){
        this.stickerPath = stickerPath;
    }

    public String getStickerPath(){
        return stickerPath;
    }

    public void setStickerPath(String stickerPath){
        this.stickerPath = stickerPath;
    }
}
