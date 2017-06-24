package com.cool.user.netty_chatsystem.Chat_server.dto;


import com.cool.user.netty_chatsystem.Chat_biz.entity.file.ServerFile;
import com.cool.user.netty_chatsystem.Chat_core.transport.DataBuffer;
import com.cool.user.netty_chatsystem.Chat_core.transport.IMSerializer;

/**
 * Created by user on 2016/6/8.
 */
public class FileDTO implements IMSerializer {

    private ServerFile serverFile;

    public FileDTO(){

    }

    public FileDTO(ServerFile serverFile){
        this.serverFile = serverFile;
    }

    public ServerFile getServerFile() {
        return  serverFile;
    }

    public void setServerFile(ServerFile  serverFile) {
        this. serverFile =  serverFile;
    }

    public DataBuffer encode(short version){
        DataBuffer buffer = new DataBuffer();
        buffer.writeString(serverFile.getId());
        buffer.writeString(serverFile.getToId());
        buffer.writeByteArray(serverFile.getBytes());
        buffer.writeString(serverFile.getFileName());
        buffer.writeLong(serverFile.getFileSize());
        buffer.writeInt(serverFile.getSumCountPackage());
        buffer.writeInt(serverFile.getCountPackage());
        buffer.writeString(serverFile.getSendId());
        buffer.writeString(serverFile.getReceiveId());
        buffer.writeString(serverFile.getSendNickName());
        buffer.writeString(serverFile.getReceiveNickName());
        buffer.writeLong(serverFile.getSendTime());
        buffer.writeLong(serverFile.getReceiveTime());
        buffer.writeInt(serverFile.getEffectMessage());
        buffer.writeString(serverFile.getThink());
        return buffer;
    }

    public void decode(DataBuffer buffer , short version){
        if(serverFile == null){
            serverFile = new ServerFile();
        }
        serverFile.setId(buffer.readString());
        serverFile.setToId(buffer.readString());
        serverFile.setBytes(buffer.readByteArray());
        serverFile.setFileName(buffer.readString());
        serverFile.setFileSize(buffer.readLong());
        serverFile.setSumCountPackage(buffer.readInt());
        serverFile.setCountPackage(buffer.readInt());
        serverFile.setSendId(buffer.readString());
        serverFile.setReceiveId(buffer.readString());
        serverFile.setSendNickName(buffer.readString());
        serverFile.setReceiveNickName(buffer.readString());
        serverFile.setSendTime(buffer.readLong());
        serverFile.setReceiveTime(buffer.readLong());
        serverFile.setEffectMessage(buffer.readInt());
        serverFile.setThink(buffer.readString());
    }
}
