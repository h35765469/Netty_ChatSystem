package com.example.user.netty_chatsystem.Chat_server.dto;

import com.example.user.netty_chatsystem.Chat_biz.entity.Friend;
import com.example.user.netty_chatsystem.Chat_core.transport.DataBuffer;
import com.example.user.netty_chatsystem.Chat_core.transport.IMSerializer;

/**
 * Created by user on 2016/7/21.
 */
public class FriendDTO implements IMSerializer {
    private Friend friend;

    public FriendDTO(){

    }

    public FriendDTO(Friend friend){
        this.friend = friend;
    }

    public Friend getFriend(){
        return friend;
    }

    public void setFriend(Friend friend){
        this.friend = friend;
    }

    public DataBuffer encode(short version) {
        DataBuffer buffer = new DataBuffer();
        buffer.writeString(friend.getUserName());
        buffer.writeString(friend.getFriendName());
        buffer.writeString(friend.getFriendUserName());
        buffer.writeStringArray(friend.getFriendArray());
        buffer.writeInt(friend.getIsFavorite());
        buffer.writeInt(friend.getIsBlock());
        buffer.writeIntArray(friend.getFavoriteArray());
        buffer.writeIntArray(friend.getBlockArray());
        return buffer;
    }

    public void decode(DataBuffer buffer, short version) {
        if(friend == null) {
            friend = new Friend();
        }
        friend.setUserName(buffer.readString());
        friend.setFriendName(buffer.readString());
        friend.setFriendUserName(buffer.readString());
        friend.setFriendArray(buffer.readStringArray());
        friend.setIsFavorite(buffer.readInt());
        friend.setIsBlock(buffer.readInt());
        friend.setFavoriteArray(buffer.readIntArray());
        friend.setBlockArray(buffer.readIntArray());
    }
}
