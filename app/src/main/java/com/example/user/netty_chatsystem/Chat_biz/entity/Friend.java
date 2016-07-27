package com.example.user.netty_chatsystem.Chat_biz.entity;

/**
 * Created by user on 2016/7/21.
 */
public class Friend {
    private String friendUserName;
    private String friendName;
    private String username;
    private String[] friendArray;
    private int isFavorite = 0;
    private int isBlock = 0;
    private int[] favoriteArray = {0};
    private int[] blockArray = {0};


    public Friend(){

    }

    public Friend(String username , String friendUserName){
        this.username = username;
        this.friendUserName = friendUserName;
    }

    public String getFriendUserName(){
        return friendUserName;
    }

    public void setFriendUserName(String friendUserName){
        this.friendUserName = friendUserName;
    }

    public String getFriendName(){
        return friendName;
    }

    public void setFriendName(String friendName){
        this.friendName = friendName;
    }

    public String getUserName(){
        return username;
    }

    public void setUserName(String username){
        this.username = username;
    }

    public String[] getFriendArray(){
        return friendArray;
    }

    public void setFriendArray(String[] friendArray){
        this.friendArray = friendArray;
    }


    public int getIsFavorite(){
        return isFavorite;
    }

    public void setIsFavorite(int isFavorite){
        this.isFavorite = isFavorite;
    }

    public int getIsBlock(){
        return isBlock;
    }

    public void setIsBlock(int isBlock){
        this.isBlock = isBlock;
    }

    public int[] getFavoriteArray(){
        return favoriteArray;
    }

    public void setFavoriteArray(int[] favoriteArray){
        this.favoriteArray = favoriteArray;
    }

    public int[] getBlockArray(){
        return blockArray;
    }

    public void setBlockArray(int[] blockArray){
        this.blockArray = blockArray;
    }


}
