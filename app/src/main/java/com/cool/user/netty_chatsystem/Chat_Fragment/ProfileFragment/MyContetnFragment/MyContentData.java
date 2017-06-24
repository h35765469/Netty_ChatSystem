package com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.MyContetnFragment;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by user on 2017/3/8.
 */
public class MyContentData implements Parcelable{
    private String content;
    private String think;
    private int effect;
    private int collectCount;
    private int unReadCount = 0;

    public MyContentData(){

    }

    public MyContentData(Parcel in){
        content = in.readString();
        think = in.readString();
        effect = in.readInt();
        collectCount = in.readInt();
        unReadCount = in.readInt();

    }

    public String getContent(){
        return content;
    }
    public void setContent(String content){
        this.content = content;
    }

    public String getThink(){
        return think;
    }
    public void setThink(String think){
        this.think = think;
    }

    public int getEffect(){
        return effect;
    }
    public void setEffect(int effect){
        this.effect = effect;
    }

    public int getCollectCount(){
        return collectCount;
    }
    public void setCollectCount(int collectCount){
        this.collectCount = collectCount;
    }

    public int getUnReadCount(){
        return unReadCount;
    }
    public void setUnReadCount(int unReadCount){
        this.unReadCount = unReadCount;
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(content);
        dest.writeString(think);
        dest.writeInt(effect);
        dest.writeInt(collectCount);
        dest.writeInt(unReadCount);
    }

    public static final Parcelable.Creator<MyContentData> CREATOR = new Parcelable.Creator<MyContentData>() {
        public MyContentData createFromParcel(Parcel in) {
            return new MyContentData(in);
        }

        public MyContentData[] newArray(int size) {
            return new MyContentData[size];

        }
    };

    @Override
    public boolean equals(Object obj) {
        boolean bres = false;
        if (obj instanceof MyContentData) {
            MyContentData o = (MyContentData) obj;
            bres = (this.content.equals(o.getContent()));
        }
        return bres;
    }

}
