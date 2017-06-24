package com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.PersonalSettingFragment.BlockFriendFragment;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cool.user.netty_chatsystem.Chat_Client.handler.Client_UserHandler;
import com.cool.user.netty_chatsystem.Chat_Sqlite_ChatHistory.MyDBHelper;
import com.cool.user.netty_chatsystem.Chat_biz.entity.Friend;
import com.cool.user.netty_chatsystem.Chat_core.connetion.IMConnection;
import com.cool.user.netty_chatsystem.Chat_core.protocol.Commands;
import com.cool.user.netty_chatsystem.Chat_core.protocol.Handlers;
import com.cool.user.netty_chatsystem.Chat_core.transport.Header;
import com.cool.user.netty_chatsystem.Chat_core.transport.IMResponse;
import com.cool.user.netty_chatsystem.Chat_server.dto.FriendDTO;
import com.cool.user.netty_chatsystem.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user on 2016/9/23.
 */
public class BlockFriendAdapter extends BaseAdapter {
    private ArrayList<HashMap<String, Object>> blockFriendList;
    private LayoutInflater layoutInflater;
    private Context context;
    private String loginId;

    private ViewHolder viewHolder;

    private class ViewHolder {
        TextView blockFriendText;
        ImageView cancelBlockFriendImg;
    }

    public BlockFriendAdapter(Context c, ArrayList<HashMap<String, Object>> blockFriendList , String loginId) {
        this.context = c;
        this.blockFriendList = blockFriendList;
        layoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.loginId = loginId;
    }

    @Override
    public int getCount() {
        return blockFriendList.size();
    }

    @Override
    public HashMap<String,Object> getItem(int position) {
        return blockFriendList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView != null) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = layoutInflater.inflate(R.layout.resource_blockfriend_listview, null);
            viewHolder = new ViewHolder();
            viewHolder.blockFriendText = (TextView) convertView.findViewById(R.id.blockFriendText);
            viewHolder.cancelBlockFriendImg = (ImageView) convertView.findViewById(R.id.cancelBlockFriendImg);
            convertView.setTag(viewHolder);
        }

        HashMap<String, Object> blockFriendInfo = blockFriendList.get(position);
        if (blockFriendInfo != null) {
            final String blockFriendId = (String) blockFriendInfo.get("blockFriendId");
            final String blockFriendName = (String)blockFriendInfo.get("blockName");
            viewHolder.blockFriendText.setText(blockFriendName);
            viewHolder.cancelBlockFriendImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelBlockFriend(blockFriendId);
                    deleteBlockFriendList(blockFriendId, blockFriendName);
                    updateFriendStatusInSqlite(blockFriendId);
                    notifyDataSetChanged();
                }
            });
        }

        return convertView;
    }

    public ArrayList<HashMap<String,Object>> getBlockFriendList(){
        return blockFriendList;
    }


    //取消封鎖
    private void cancelBlockFriend(String friendId){
        IMConnection connection = Client_UserHandler.getConnection();
        Friend friend = new Friend();
        friend.setId(loginId);
        friend.setFriendId(friendId);
        friend.setFriendName("");
        friend.setIsBlock(1);

        IMResponse resp = new IMResponse();
        Header header = new Header();
        header.setHandlerId(Handlers.USER);
        header.setCommandId(Commands.FRIEND_BLOCK_REQUEST);
        resp.setHeader(header);
        resp.writeEntity(new FriendDTO(friend));
        connection.sendResponse(resp);

    }

    //刪除blockFriendList裡的被取消的好友
    private void deleteBlockFriendList(String blockFriendId, String blockFriendName){
        HashMap<String,Object>blockFriend = new HashMap<String,Object>();
        blockFriend.put("blockFriendId", blockFriendId);
        blockFriend.put("blockName", blockFriendName);
        blockFriendList.remove(blockFriend);
    }

    //更改sqlite裡Friend的status(解除封鎖)
    private void updateFriendStatusInSqlite(String friendId){
        //創建MyDBHelper對象
        MyDBHelper dbHelper = new MyDBHelper(context , "Chat.db", null, 1);
        //得到一個可讀的SQLiteDatabase對象
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //生成ContentValues​​對象 //key:列名，value:想插入的值
        ContentValues cv = new ContentValues();
        cv.put("status",1);//status 1 : 代表正常好友
        db.update("Friend", cv, "id" + "=\"" + friendId + "\"", null);
        db.close();
    }

}
