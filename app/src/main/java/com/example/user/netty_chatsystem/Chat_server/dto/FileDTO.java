package com.example.user.netty_chatsystem.Chat_server.dto;


import com.example.user.netty_chatsystem.Chat_biz.entity.file.ServerFile;
import com.example.user.netty_chatsystem.Chat_core.transport.DataBuffer;
import com.example.user.netty_chatsystem.Chat_core.transport.IMSerializer;

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
        buffer.writeByteArray(serverFile.getBytes());
        buffer.writeString(serverFile.getFileName());
        buffer.writeLong(serverFile.getFileSize());
        buffer.writeInt(serverFile.getSumCountPackage());
        buffer.writeInt(serverFile.getCountPackage());
        buffer.writeString(serverFile.getSendId());
        buffer.writeString(serverFile.getReceiveId());
        buffer.writeLong(serverFile.getSendTime());
        buffer.writeLong(serverFile.getReceiveTime());
        return buffer;
    }

    public void decode(DataBuffer buffer , short version){
        if(serverFile == null){
            serverFile = new ServerFile();
        }
        serverFile.setBytes(buffer.readByteArray());
        serverFile.setFileName(buffer.readString());
        serverFile.setFileSize(buffer.readLong());
        serverFile.setSumCountPackage(buffer.readInt());
        serverFile.setCountPackage(buffer.readInt());
        serverFile.setSendId(buffer.readString());
        serverFile.setReceiveId(buffer.readString());
        serverFile.setSendTime(buffer.readLong());
        serverFile.setReceiveTime(buffer.readLong());

    }
}
