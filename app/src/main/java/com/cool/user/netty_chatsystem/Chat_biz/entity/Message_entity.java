package com.cool.user.netty_chatsystem.Chat_biz.entity;




/**
 * Created by Tony on 2/20/15.
 */

public class Message_entity extends BaseEntity {
    // uin
    private String from;
    private String fromNickName;
    private String to;
    private String toId;
    private String toNickName;

    private byte type;
    private int read = 0;
    private String message;
    private long createAt;
    private long readAt;

    public Message_entity(){

    }

    public Message_entity(String from, String to, String message){
        this.from = from;
        this.to = to;
        this.message = message;
    }


    public String getFrom() {
        return from;
    }
    public void setFrom(String from) {
        this.from = from;
    }

    public String getFromNickName(){
        return fromNickName;
    }
    public void setFromNickName(String fromNickName){
        this.fromNickName = fromNickName;
    }

    public String getToNickName(){
        return toNickName;
    }
    public void setToNickName(String toNickName){
        this.toNickName = toNickName;
    }

    public String getTo() {
        return to;
    }
    public void setTo(String to) {
        this.to = to;
    }

    public String getToId(){
        return toId;
    }
    public void setToId(String toId){
        this.toId = toId;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    public long getReadAt() {
        return readAt;
    }

    public void setReadAt(long readAt) {
        this.readAt = readAt;
    }




    public enum Type {
        SESSION_MSG(0), // 臨時會話消息
        USER_MSG(1),  // 好友消息
        ROOM_MSG(2);  // 群消息

        private byte mValue = 0;

        public byte getValue() {
            return mValue;
        }

        Type(int value) {
            mValue = (byte) value;
        }

        public static Type valueOfRaw(byte value) {
            for (Type type : Type.values()) {
                if (type.getValue() == value) {
                    return type;
                }
            }
            return SESSION_MSG;
        }
    }

}