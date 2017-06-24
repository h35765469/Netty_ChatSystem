package com.cool.user.netty_chatsystem.ChatListViewAdapter;

import java.io.Serializable;

/**
 * Created by user on 2016/3/2.
 */
public class RowItem  implements Serializable {
    private String whoId;
    private String whoUserName;
    private String content="";
    private long createTime;

    public RowItem(String whoId, String whoUserName){
        this.whoId = whoId;
        this.whoUserName = whoUserName;
    }

    public String getWhoId(){
        return whoId;
    }
    public void setWhoId(String whoId){
        this.whoId = whoId;
    }

    public String getWhoUserName(){
        return whoUserName;
    }
    public void setWhoUserName(String whoUserName){
        this.whoUserName = whoUserName;
    }

    public String getContent(){return content;}
    public void setContent(String content){this.content = content;}

    public long getCreateTime(){
        return createTime;
    }
    public void setCreateTime(long createTime){
        this.createTime = createTime;
    }


    @Override
    public boolean equals(Object obj) {
        boolean bres = false;
        if (obj instanceof RowItem) {
            RowItem o = (RowItem) obj;
            bres = (this.whoUserName.equals(o.whoUserName)) & (this.whoId.equals(o.whoId));
        }
        return bres;
    }
}
