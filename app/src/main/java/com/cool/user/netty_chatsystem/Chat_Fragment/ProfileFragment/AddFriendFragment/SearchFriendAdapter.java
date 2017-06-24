package com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.AddFriendFragment;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cool.user.netty_chatsystem.Chat_Client.handler.Client_UserHandler;
import com.cool.user.netty_chatsystem.ChatListViewAdapter.FriendRowItem;
import com.cool.user.netty_chatsystem.Chat_MySQL.Config;
import com.cool.user.netty_chatsystem.Chat_Sqlite_ChatHistory.MyDBHelper;
import com.cool.user.netty_chatsystem.Chat_biz.entity.Friend;
import com.cool.user.netty_chatsystem.Chat_core.connetion.IMConnection;
import com.cool.user.netty_chatsystem.Chat_core.protocol.Commands;
import com.cool.user.netty_chatsystem.Chat_core.protocol.Handlers;
import com.cool.user.netty_chatsystem.Chat_core.transport.Header;
import com.cool.user.netty_chatsystem.Chat_core.transport.IMResponse;
import com.cool.user.netty_chatsystem.Chat_server.dto.FriendDTO;
import com.cool.user.netty_chatsystem.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by user on 2016/10/10.
 */
public class SearchFriendAdapter extends BaseAdapter {

    private Context context;
    private List<FriendRowItem> friendRowItemList;
    private ArrayList<String> loadFriendNameArrayList;
    private String username;
    private String loginId;
    private String nickName;
    DisplayImageOptions options;

    public SearchFriendAdapter(Context context , List<FriendRowItem>friendRowItemList , String username, String loginId, String nickName, ArrayList<String>loadFriendNameArrayList){
        this.context = context;
        this.friendRowItemList = friendRowItemList;
        this.username = username;
        this.loginId = loginId;
        this.nickName = nickName;
        this.loadFriendNameArrayList = loadFriendNameArrayList;
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(context));

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.logo)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.logo_red)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    @Override
    public View getView(final int position , View convertView , ViewGroup parent){
        final ViewHolder holder;

        LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if(convertView==null){
            convertView = mInflater.inflate(R.layout.resource_searchfriend_listview,null);
            holder = new ViewHolder();
            holder.searchFriendProfileImg = (de.hdodenhof.circleimageview.CircleImageView)convertView.findViewById(R.id.searchFriendProfileImg);
            holder.searchFriendText = (TextView)convertView.findViewById(R.id.searchFriendText);
            holder.searchFriendInviteImg = (ImageView)convertView.findViewById(R.id.searchFriendInviteImg);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }


        final FriendRowItem friendRowItem = (FriendRowItem)getItem(position);
        if(loadFriendNameArrayList.contains(friendRowItem.getFriendName()) || nickName.equals(friendRowItem.getFriendName())){
            holder.searchFriendInviteImg.setVisibility(View.GONE);
        }

        if(friendRowItem.getAvatarName().length() > 0) {
            ImageLoader.getInstance().displayImage(Config.SERVER_PROFILE_ADDRESS + friendRowItem.getAvatarName() + ".jpg", holder.searchFriendProfileImg, options, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                }
            }, new ImageLoadingProgressListener() {
                @Override
                public void onProgressUpdate(String imageUri, View view, int current, int total) {
                }
            });
        }else{
            holder.searchFriendProfileImg.setImageResource(R.drawable.logo_red);
        }

        holder.searchFriendText.setText(friendRowItem.getFriendName());
        holder.searchFriendInviteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Client_UserHandler.getConnection() != null) {
                    if(!loadFriendNameArrayList.contains(friendRowItem.getFriendName())) {
                        requestFriend(friendRowItem.getFriendId());
                        saveNewFriend(friendRowItem.getFriendId(), friendRowItem.getFriendUserName(), friendRowItem.getFriendName(), friendRowItem.getAvatarName());
                        loadFriendNameArrayList.add(friendRowItem.getFriendName());//新添家朋友進arraylist
                        notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(context, "目前無網路，請確認連線狀態", Toast.LENGTH_SHORT).show();
                }
            }
        });




        return convertView;
    }

    private class ViewHolder{
        CircleImageView searchFriendProfileImg;
        TextView searchFriendText;
        ImageView searchFriendInviteImg;
    }

    @Override
    public int getCount(){
        return friendRowItemList.size();
    }

    @Override
    public Object getItem(int position){
        return friendRowItemList.get(position);
    }

    @Override
    public long getItemId(int position){
        return friendRowItemList.indexOf(getItem(position));
    }

    public void cleanAll(){
        friendRowItemList.clear();
    }


    // 請求朋友邀請
    public void requestFriend(String friendId){
        IMConnection connection = Client_UserHandler.getConnection();
        Friend friend = new Friend();
        friend.setId(loginId);
        friend.setFriendId(friendId);

        IMResponse resp = new IMResponse();
        Header header = new Header();
        header.setHandlerId(Handlers.USER);
        header.setCommandId(Commands.FRIEND_ADD_REQUEST);
        resp.setHeader(header);
        resp.writeEntity(new FriendDTO(friend));
        connection.sendResponse(resp);
    }

    private void saveNewFriend(String friendId, String friendUserName, String friendName, String friendAvatarUri){
        //創建MyDBHelper對象
        MyDBHelper dbHelper = new MyDBHelper(context, "Chat.db", null, 1);
        //得到一個可讀的SQLiteDatabase對象
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        ContentValues cv = new ContentValues();

        //往ContentValues​​對象存放數據，鍵-值對模式
        cv.put("id", friendId);
        cv.put("friendusername", friendUserName);
        cv.put("friendname", friendName);
        cv.put("friendAvatarUri", friendAvatarUri);
        cv.put("status", 4);//status 0 : 代表認證中
        cv.put("viewer", 0);
        cv.put("favorite", 0);

        //調用insert方法，將數據插入數據庫
        db.insert("Friend", null, cv);

        db.close();
    }
}
