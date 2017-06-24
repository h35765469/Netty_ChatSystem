package com.cool.user.netty_chatsystem.Chat_biz.entity;

/**
 * Created by user on 2016/7/6.
 */
public class OfflineMessage  extends  BaseEntity {
    private String[] offlineMessageArray;
    private long[] createTimeArray;

    public String[] getOfflineMessageArray(){
        return offlineMessageArray;
    }

    public void setOfflineMessageArray(String[] offlineMessageArray){
        this.offlineMessageArray = offlineMessageArray;
    }

    public long[] getCreateTimeArray(){
        return createTimeArray;
    }
    public void setCreateTimeArray(long[] createTimeArray){
        this.createTimeArray = createTimeArray;
    }
}
