package com.example.user.netty_chatsystem.Chat_Listview_Friendlist;

/**
 * Created by user on 2016/3/2.
 */
public class RowItem {
    private int imageId;
    private String title;
    private String id;

    public RowItem(int imageId , String title , String id){
        this.imageId = imageId;
        this.title = title;
        this.id = id;
    }

    public int getImageId() {
        return imageId;
    }
    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }


    public String getId(){return id;}
    public void setId(String id){this.id = id;}
}
