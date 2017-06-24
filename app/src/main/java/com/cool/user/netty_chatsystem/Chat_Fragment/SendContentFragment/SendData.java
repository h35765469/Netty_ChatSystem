package com.cool.user.netty_chatsystem.Chat_Fragment.SendContentFragment;

/**
 * Created by user on 2017/2/2.
 */
public class SendData {

    private String id;
    private String username;
    private String name;

    public SendData(){

    }

    public SendData(String name){
        this.name = name;
    }

    public String getId(){
        return id;
    }
    public void setId(String id){
        this.id = id;
    }

    public String getUsername(){
        return username;
    }
    public void setUsername(String username){
        this.username = username;
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        boolean bres = false;
        if (obj instanceof SendData) {
            SendData o = (SendData) obj;
            bres = (this.name.equals(o.getName()));
        }
        return bres;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
