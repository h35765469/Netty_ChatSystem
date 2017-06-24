package com.cool.user.netty_chatsystem.Chat_Fragment.WorldShareFragment.FriendContent;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by user on 2017/2/27.
 */
public class FriendContentData implements Parcelable{
    private String ownerId;
    private String ownerNickName;
    private String ownerProfileName;
    private String content;
    private String think;
    private int effect;

    public FriendContentData(){

    }

    public FriendContentData(Parcel in){
        ownerId = in.readString();
        ownerNickName = in.readString();
        ownerProfileName = in.readString();
        content = in.readString();
        think = in.readString();
        effect = in.readInt();
    }

    public String getOwnerId(){
        return ownerId;
    }
    public void setOwnerId(String ownerId){
        this.ownerId = ownerId;
    }

    public String getOwnerNickName(){
        return ownerNickName;
    }
    public void setOwnerNickName(String ownerNickName){
        this.ownerNickName = ownerNickName;
    }

    public String getOwnerProfileName(){
        return ownerProfileName;
    }
    public void setOwnerProfileName(String ownerProfileName){
        this.ownerProfileName = ownerProfileName;
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

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ownerId);
        dest.writeString(ownerNickName);
        dest.writeString(ownerProfileName);
        dest.writeString(content);
        dest.writeString(think);
        dest.writeInt(effect);
    }

    public static final Parcelable.Creator<FriendContentData> CREATOR = new Parcelable.Creator<FriendContentData>() {
        public FriendContentData createFromParcel(Parcel in) {
            return new FriendContentData(in);
        }

        public FriendContentData[] newArray(int size) {
            return new FriendContentData[size];

        }
    };

}
