package com.cool.user.netty_chatsystem.Chat_biz.entity.file;

import com.cool.user.netty_chatsystem.Chat_biz.entity.BaseEntity;

import java.io.Serializable;

/**
 * Created by user on 2016/6/8.
 */
public class FileData extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 4010249994097151671L;
    //總包數
    private int sumCountPackage;
    //當前包數
    private int  countPackage;
    private byte[] bytes;
    private int bytesLength;
    //發送人id
    private String sendId;
    //接收人id
    private String receiveId;
    //發送的時間
    private long sendTime;
    //接收的時間
    private long receiveTime;
    private String toId;
    private String sendNickName;
    private String receiveNickName;

    public int getSumCountPackage() {
        return this.sumCountPackage;
    }

    public void setSumCountPackage(int sumCountPackage) {
        this.sumCountPackage = sumCountPackage;
    }

    public int getCountPackage() {
        return this.countPackage;
    }

    public void setCountPackage(int countPackage) {
        this.countPackage = countPackage;
    }

    public byte[] getBytes() {
        return this.bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public int getBytesLength(){
        return this.bytesLength;
    }

    public void setBytesLength(int bytesLength){
        this.bytesLength = bytesLength;
    }

    public String getSendId() {
        return sendId;
    }

    public void setSendId(String sendId) {
        this.sendId = sendId;
    }

    public String getReceiveId() {
        return receiveId;
    }

    public void setReceiveId(String receiveId) {
        this.receiveId = receiveId;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public long getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(long  receiveTime) {
        this. receiveTime =  receiveTime;
    }

    public String getToId(){
        return toId;
    }

    public void setToId(String toId){
        this.toId = toId;
    }

    public String getSendNickName(){
        return sendNickName;
    }
    public void setSendNickName(String sendNickName){
        this.sendNickName = sendNickName;
    }

    public String getReceiveNickName(){
        return receiveNickName;
    }
    public void setReceiveNickName(String receiveNickName){
        this.receiveNickName = receiveNickName;
    }
}
