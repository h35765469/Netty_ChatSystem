package com.example.user.netty_chatsystem.Chat_Listview_Friendlist;

import java.io.Serializable;

/**
 * Created by user on 2016/3/2.
 */
public class RowItem  implements Serializable {
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

    @Override
    public boolean equals(Object obj) {
        boolean bres = false;
        if (obj instanceof RowItem) {
            RowItem o = (RowItem) obj;
            bres = (this.imageId==o.imageId) & (this.title.equals(o.title)) & (this.id.equals(o.id));
        }
        return bres;
    }
}
