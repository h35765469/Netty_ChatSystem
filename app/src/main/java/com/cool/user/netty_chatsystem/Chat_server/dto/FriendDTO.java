package com.cool.user.netty_chatsystem.Chat_server.dto;

import com.cool.user.netty_chatsystem.Chat_biz.entity.Friend;
import com.cool.user.netty_chatsystem.Chat_core.transport.DataBuffer;
import com.cool.user.netty_chatsystem.Chat_core.transport.IMSerializer;

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
        buffer.writeString(friend.getId());
        buffer.writeString(friend.getUserName());
        buffer.writeString(friend.getFriendName());
        buffer.writeString(friend.getFriendUserName());
        buffer.writeString(friend.getFriendAvatarUri());
        buffer.writeStringArray(friend.getFriendArray());
        buffer.writeStringArray(friend.getFriendNameArray());
        buffer.writeInt(friend.getIsFavorite());
        buffer.writeInt(friend.getIsBlock());
        buffer.writeInt(friend.getViewer());
        buffer.writeIntArray(friend.getFavoriteArray());
        buffer.writeIntArray(friend.getViewerArray());
        buffer.writeIntArray(friend.getStatusArray());
        buffer.writeStringArray(friend.getFriendAvatarUriArray());
        buffer.writeStringArray(friend.getFriendIdArray());
        buffer.writeString(friend.getFriendId());
        return buffer;
    }

    public void decode(DataBuffer buffer, short version) {
        if(friend == null) {
            friend = new Friend();
        }
        friend.setId(buffer.readString());
        friend.setUserName(buffer.readString());
        friend.setFriendName(buffer.readString());
        friend.setFriendUserName(buffer.readString());
        friend.setFriendAvatarUri(buffer.readString());
        friend.setFriendArray(buffer.readStringArray());
        friend.setFriendNameArray(buffer.readStringArray());
        friend.setIsFavorite(buffer.readInt());
        friend.setIsBlock(buffer.readInt());
        friend.setViewer(buffer.readInt());
        friend.setFavoriteArray(buffer.readIntArray());
        friend.setViewerArray(buffer.readIntArray());
        friend.setStatusArray(buffer.readIntArray());
        friend.setFriendAvatarUriArray(buffer.readStringArray());
        friend.setFriendIdArray(buffer.readStringArray());
        friend.setFriendId(buffer.readString());
    }
}
