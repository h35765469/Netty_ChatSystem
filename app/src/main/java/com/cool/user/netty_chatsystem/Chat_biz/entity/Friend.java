package com.cool.user.netty_chatsystem.Chat_biz.entity;

/**
 * Created by user on 2016/7/21.
 */
public class Friend extends  BaseEntity {
    private String friendId;
    private String friendUserName;
    private String friendName = "";
    private String username;
    private String friendAvatarUri;
    private int isFavorite;
    private int isBlock;
    private int viewer = 0;
    private int status;//status為判斷好友狀態  0 : 代表待確認中 1 : 確認完畢 2 : 封鎖
    private String[] friendIdArray = new String[]{};
    private String[] friendArray = new String[]{};
    private String[] friendNameArray = new String[]{};
    private String[] friendAvatarUriArray = new String[]{};
    private int[] favoriteArray;
    private int[] viewerArray;
    private int[] statusArray;

    private User userObject;

    public Friend(){

    }

    public Friend(String username , String friendUserName){
        this.username = username;
        this.friendUserName = friendUserName;
    }

    public String getFriendId(){
        return friendId;
    }

    public void setFriendId(String friendId){
        this.friendId = friendId;
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

    public String[] getFriendIdArray(){
        return friendIdArray;
    }

    public void setFriendIdArray(String[] friendIdArray){
        this.friendIdArray = friendIdArray;
    }

    public String[] getFriendArray(){
        return friendArray;
    }

    public void setFriendArray(String[] friendArray){
        this.friendArray = friendArray;
    }

    public String[] getFriendNameArray(){return friendNameArray;}

    public void setFriendNameArray(String[] friendNameArray){
        this.friendNameArray = friendNameArray;
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

    public int getViewer(){
        return viewer;
    }

    public void setViewer(int viewer){
        this.viewer = viewer;
    }

    public int getStatus(){
        return status;
    }

    public void setStatus(int status){
        this.status = status;
    }

    public String getFriendAvatarUri(){
        return friendAvatarUri;
    }

    public void setFriendAvatarUri(String friendAvatarUri){
        this.friendAvatarUri = friendAvatarUri;
    }

    public int[] getFavoriteArray(){
        return favoriteArray;
    }

    public void setFavoriteArray(int[] favoriteArray){
        this.favoriteArray = favoriteArray;
    }

    public int[] getViewerArray(){
        return viewerArray;
    }

    public void setViewerArray(int[] viewerArray){
        this.viewerArray = viewerArray;
    }

    public int[] getStatusArray(){
        return statusArray;
    }

    public void setStatusArray(int[] statusArray){
        this.statusArray = statusArray;
    }

    public String[] getFriendAvatarUriArray(){
        return friendAvatarUriArray;
    }

    public void setFriendAvatarUriArray(String[] friendAvatarUriArray){
        this.friendAvatarUriArray = friendAvatarUriArray;
    }

    public User getUserObject(){
        return userObject;
    }

    public void setUserObject(User userObject){
        this.userObject = userObject;
    }

}
