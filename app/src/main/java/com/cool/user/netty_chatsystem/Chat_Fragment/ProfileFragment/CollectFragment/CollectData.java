package com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.CollectFragment;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by user on 2016/12/21.
 */
public class CollectData implements Parcelable  {
    String collectId;
    String collectContent;
    String collectUserName;
    String collectNickName;
    String collectProfile;
    Bitmap content;
    String collectThink;
    int collectEffect;

    public CollectData(){
    }

    public CollectData(Parcel in){
        collectId = in.readString();
        collectContent = in.readString();
        collectUserName = in.readString();
        collectNickName = in.readString();
        collectProfile = in.readString();
        collectThink = in.readString();
        collectEffect = in.readInt();
    }

    public String getCollectId(){
        return collectId;
    }
    public void setCollectId(String collectId){
        this.collectId = collectId;
    }

    public String getCollectContent(){
        return collectContent;
    }
    public void setCollectContent(String collectContent){
        this.collectContent = collectContent;
    }

    public String getCollectUserName(){
        return collectUserName;
    }

    public void setCollectUserName(String collectUserName) {
        this.collectUserName = collectUserName;
    }

    public String getCollectNickName(){
        return collectNickName;
    }
    public void setCollectNickName(String collectNickName){
        this.collectNickName = collectNickName;
    }

    public String getCollectProfile(){
        return collectProfile;
    }
    public void setCollectProfile(String collectProfile){
        this.collectProfile = collectProfile;
    }

    public Bitmap getContent(){
        return content;
    }
    public void setContent(Bitmap content){
        this.content = content;
    }

    public String getCollectThink(){
        return collectThink;
    }
    public void setCollectThink(String collectThink){
        this.collectThink = collectThink;
    }

    public int getCollectEffect(){
        return collectEffect;
    }
    public void setCollectEffect(int collectEffect){
        this.collectEffect = collectEffect;
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(collectId);
        dest.writeString(collectContent);
        dest.writeString(collectUserName);
        dest.writeString(collectNickName);
        dest.writeString(collectProfile);
        dest.writeString(collectThink);
        dest.writeInt(collectEffect);
    }

    public static final Parcelable.Creator<CollectData> CREATOR = new Parcelable.Creator<CollectData>() {
        public CollectData createFromParcel(Parcel in) {
            return new CollectData(in);
        }

        public CollectData[] newArray(int size) {
            return new CollectData[size];

        }
    };

}
