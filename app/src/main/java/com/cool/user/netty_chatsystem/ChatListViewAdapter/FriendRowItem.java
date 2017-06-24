package com.cool.user.netty_chatsystem.ChatListViewAdapter;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by user on 2017/1/13.
 */
public class FriendRowItem  implements Serializable {
    private String friendId;
    private String friendUserName;
    private String friendName;
    private Bitmap avatar;
    private String avatarName;
    private String content;
    private Long createTime;
    boolean fromMessagelist = false;
    private int friendFavorite;


    public FriendRowItem(String friendId, String friendUserName, String friendName){
        this.friendId = friendId;
        this.friendUserName = friendUserName;
        this.friendName = friendName;
    }

    public FriendRowItem(String friendId, String friendUserName, String friendName, Bitmap avatar){
        this.friendId = friendId;
        this.friendUserName = friendUserName;
        this.friendName = friendName;
        this.avatar = avatar;
        fromMessagelist = true;
    }


    public String getFriendId(){return friendId;}
    public void setFriendid(String friendId){this.friendId = friendId;}

    public String getFriendUserName(){return friendUserName;}
    public void setFriendUserName(String friendUserName){
        this.friendUserName = friendUserName;
    }

    public String getFriendName(){
        return friendName;
    }
    public void setFriendName(String friendName){
        this.friendName = friendName;
    }

    public Bitmap getAvatar(){return avatar;}
    public void setAvatar(Bitmap avatarUri){
        this.avatar = avatar;
    }

    public String getContent(){return content;}
    public void setContent(String content){
        this.content = content;
    }

    public String getAvatarName(){
        return avatarName;
    }
    public void setAvatarName(String avatarName){
        this.avatarName = avatarName;
    }

    public Long getCreateTime(){
        return createTime;
    }
    public void setCreateTime(Long createTime){
        this.createTime = createTime;
    }

    public int getFriendFavorite(){
        return friendFavorite;
    }
    public void setFriendFavorite(int friendFavorite){
        this.friendFavorite = friendFavorite;
    }

    public Boolean getFromMessagelist(){
        return fromMessagelist;
    }


    @Override
    public boolean equals(Object obj) {
        boolean bres = false;
        if (obj instanceof FriendRowItem) {
            FriendRowItem o = (FriendRowItem) obj;
            bres = (this.friendId.equals(o.getFriendId())) & (this.friendName.equals(o.getFriendName()));
        }
        return bres;
    }
}
