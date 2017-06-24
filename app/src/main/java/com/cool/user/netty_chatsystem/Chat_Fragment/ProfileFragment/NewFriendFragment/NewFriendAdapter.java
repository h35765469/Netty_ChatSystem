package com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.NewFriendFragment;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cool.user.netty_chatsystem.Chat_Client.handler.Client_UserHandler;
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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user on 2016/9/21.
 */
public class NewFriendAdapter extends BaseAdapter {

    private ArrayList<HashMap<String,Object>> newFriendList;
    private LayoutInflater layoutInflater;
    private Context context;
    private TextView noNewFriendText;
    String username;
    String loginId;

    private ViewHolder viewHolder;

    private class ViewHolder{
        TextView newFriendText;
        ImageView makeNewFriendImg;
        ImageView cancelNewFriendImg;
    }

    public NewFriendAdapter(Context c , ArrayList<HashMap<String,Object>> newFriendList, String username, String loginId, TextView noNewFriendText){
        this.context = c;
        this.newFriendList = newFriendList;
        layoutInflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.username = username;
        this.loginId = loginId;
        this.noNewFriendText = noNewFriendText;
    }

    @Override
    public int getCount(){
        return newFriendList.size();
    }

    @Override
    public HashMap<String,Object> getItem(int position){
        return newFriendList.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        if(convertView != null){
            viewHolder = (ViewHolder)convertView.getTag();
        }else{
            convertView = layoutInflater.inflate(R.layout.resource_newfriend_listview,null);
            viewHolder = new ViewHolder();
            viewHolder.newFriendText = (TextView)convertView.findViewById(R.id.newFriendText);
            viewHolder.makeNewFriendImg = (ImageView)convertView.findViewById(R.id.makeNewFriendImg);
            viewHolder.cancelNewFriendImg = (ImageView)convertView.findViewById(R.id.cancelNewFriendImg);
            convertView.setTag(viewHolder);
        }

        HashMap<String,Object>friendInfo = newFriendList.get(position);
        if(friendInfo != null){
            final String newFriendUserName = (String)friendInfo.get("friendUserName");
            final String newFriendName = (String)friendInfo.get("friendNickName");
            final String newFriendId = (String)friendInfo.get("friendId");
            final String newFriendAvatarUri = (String)friendInfo.get("friendAvatarUri");
            viewHolder.newFriendText.setText(newFriendName);
            viewHolder.makeNewFriendImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Client_UserHandler.getConnection() != null) {
                        addFriend(newFriendUserName, newFriendId);
                        updateFriendStatusInSqlite(newFriendId);
                        deleteNewFriendList(newFriendUserName, newFriendId, newFriendName, newFriendAvatarUri);
                        new DownloadImage(newFriendAvatarUri).execute();
                        notifyDataSetChanged();
                        if(getCount() == 0){
                            noNewFriendText.setVisibility(View.GONE);
                        }
                    } else {
                        Toast.makeText(context, "無法送出邀請，請確認連線狀態", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            viewHolder.cancelNewFriendImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Client_UserHandler.getConnection()!=null){
                        rejectFriend(newFriendUserName, newFriendId);
                        deleteFriendInSqlite(newFriendId);
                        deleteNewFriendList(newFriendUserName, newFriendId, newFriendName, newFriendAvatarUri);
                        notifyDataSetChanged();
                        if(getCount() == 0){
                            noNewFriendText.setVisibility(View.GONE);
                        }
                    }
                    else{
                        Toast.makeText(context, "無法拒絕好友，請確認連線狀態", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        return convertView;
    }

    public void add(HashMap<String,Object>map){
        newFriendList.add(0,map);
    }

    public ArrayList<HashMap<String,Object>> getNewFriendList(){return newFriendList;}

    //新增好友
    public void addFriend(String newFriendUserName, String newFriendId ){

        IMConnection connection = Client_UserHandler.getConnection();
        Friend friend = new Friend();
        friend.setId(loginId);
        friend.setFriendId(newFriendId);

        //createSqliteTable(username);

        IMResponse resp = new IMResponse();
        Header header = new Header();
        header.setHandlerId(Handlers.USER);
        header.setCommandId(Commands.FRIEND_ADD_SUCCESS);
        resp.setHeader(header);
        resp.writeEntity(new FriendDTO(friend));
        connection.sendResponse(resp);
    }

    //拒絕好友
    public void rejectFriend(String newFriendUserName, String newFriendId){
        IMConnection connection = Client_UserHandler.getConnection();
        Friend friend = new Friend();
        friend.setId(loginId);
        friend.setUserName(username);
        friend.setFriendId(newFriendId);
        friend.setFriendUserName(newFriendUserName);
        friend.setFriendName("");

        IMResponse resp = new IMResponse();
        Header header = new Header();
        header.setHandlerId(Handlers.USER);
        header.setCommandId(Commands.FRIEND_REJECT_REQUEST);
        resp.setHeader(header);
        resp.writeEntity(new FriendDTO(friend));
        connection.sendResponse(resp);
    }

    //update在sqlite裡Friend列表的status
    private void updateFriendStatusInSqlite(String friendId){
        //創建MyDBHelper對象
        MyDBHelper dbHelper = new MyDBHelper(context , "Chat.db", null, 1);
        //得到一個可讀的SQLiteDatabase對象
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //生成ContentValues​​對象 //key:列名，value:想插入的值
        ContentValues cv = new ContentValues();
        cv.put("status", "1");
        db.update("Friend", cv, "id" + "=\"" + friendId + "\"", null);
        db.close();
    }

    //刪除在sqlite裡Friend列表的朋友
    private void deleteFriendInSqlite(String friendId){
        //創建MyDBHelper對象
        MyDBHelper dbHelper = new MyDBHelper(context , "Chat.db", null, 1);
        //得到一個可讀的SQLiteDatabase對象
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        db.delete("Friend","id=?",new String[]{friendId});
        db.close();
    }

    private void deleteNewFriendList(String friendusername, String friendId, String friendNickName, String friendAvatarUri){
        HashMap<String,Object> newFriend = new HashMap<String,Object>();
        newFriend.put("friendUserName", friendusername);
        newFriend.put("friendId", friendId);
        newFriend.put("friendNickName", friendNickName);
        newFriend.put("friendAvatarUri", friendAvatarUri);
        newFriendList.remove(newFriend);
    }


    private class DownloadImage extends AsyncTask<Void, Void, Bitmap> {
        String content;
        public DownloadImage(String content){
            this.content = content;
        }
        @Override
        protected Bitmap doInBackground(Void... params) {
            String url = Config.SERVER_PROFILE_ADDRESS + content + ".jpg";


            try{
                URLConnection connection = new URL(url).openConnection();
                connection.setConnectTimeout(1000 * 30);
                connection.setReadTimeout(1000*30);
                return BitmapFactory.decodeStream((InputStream) connection.getContent(), null, null);

            }catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null){
                File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), Config.IMAGE_DIRECTORY_NAME);
                // Create the storage directory if it does not exist
                if (!mediaStorageDir.exists()) {
                    if (!mediaStorageDir.mkdirs()) {
                        Log.d("sendContentFragment", "Oops! Failed create " + Config.IMAGE_DIRECTORY_NAME + " directory");
                    }
                }

                File file;
                file = new File(mediaStorageDir.getPath() + File.separator + content + ".jpg");

                try{
                    FileOutputStream fos = new FileOutputStream(file);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);   //Bitmap類別的compress方法產生檔案
                    bos.flush();
                    bos.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

}
