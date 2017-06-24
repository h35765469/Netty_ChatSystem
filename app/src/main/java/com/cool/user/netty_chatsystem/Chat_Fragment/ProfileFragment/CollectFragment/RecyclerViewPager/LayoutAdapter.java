/*
 * Copyright (C) 2014 Lucas Rocha
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.CollectFragment.RecyclerViewPager;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cool.user.netty_chatsystem.Chat_Client.handler.Client_UserHandler;
import com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.CollectFragment.CollectData;
import com.cool.user.netty_chatsystem.Chat_Sqlite_ChatHistory.MyDBHelper;
import com.cool.user.netty_chatsystem.Chat_biz.entity.Friend;
import com.cool.user.netty_chatsystem.Chat_core.connetion.IMConnection;
import com.cool.user.netty_chatsystem.Chat_core.protocol.Commands;
import com.cool.user.netty_chatsystem.Chat_core.protocol.Handlers;
import com.cool.user.netty_chatsystem.Chat_core.transport.Header;
import com.cool.user.netty_chatsystem.Chat_core.transport.IMResponse;
import com.cool.user.netty_chatsystem.Chat_server.dto.FriendDTO;
import com.cool.user.netty_chatsystem.R;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class LayoutAdapter extends RecyclerView.Adapter<LayoutAdapter.SimpleViewHolder> {
    private static final String SERVER_ADDRESS = "http://192.168.43.157/AndroidFileUpload/uploads/";
    private static final String SERVER_PROFILE_ADDRESS = "http://192.168.43.157/AndroidFileUpload/profile/";
    private final Context mContext;
    private ArrayList<CollectData> collectDataArrayList;
    private ArrayList<String>friendArrayList;
    private String loginId;
    private ImageView collectContentImg, collectProfileImg, addCollectFriendImg, cancelCollectImg;

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        public SimpleViewHolder(View view) {
            super(view);
        }
    }


    public LayoutAdapter(Context context, ArrayList<CollectData> collectDataArrayList, ArrayList<String>friendArrayList,  String loginId) {
        mContext = context;
        this.collectDataArrayList = collectDataArrayList;
        this.friendArrayList = friendArrayList;
        this.loginId = loginId;
    }

    public void addItem(int position) {

    }

    public void removeItem(int position) {
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.resource_collect_item_recycleview, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, final int position) {

        final View itemView = holder.itemView;
        collectContentImg = (ImageView)itemView.findViewById(R.id.myContentImg);
        collectProfileImg = (ImageView)itemView.findViewById(R.id.collectProfileImg);
        addCollectFriendImg = (ImageView)itemView.findViewById(R.id.addCollectFriendImg);
        cancelCollectImg = (ImageView)itemView.findViewById(R.id.cancelCollectImg);
        TextView collectNameText = (TextView)itemView.findViewById(R.id.randomNameText);

        new DownloadImage(collectDataArrayList.get(position).getCollectContent() + ".jpg",0).execute();//collectcontent
        new DownloadImage(collectDataArrayList.get(position).getCollectProfile() + ".jpg",1).execute();//collectprofile

        differentiateFriend();//判斷被收集的人是否為好友

        addCollectFriendImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestFriend(collectDataArrayList.get(position).getCollectId(), collectDataArrayList.get(position).getCollectUserName());
            }
        });

        cancelCollectImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "你以刪除收藏", Toast.LENGTH_SHORT).show();
                deleteCollectContentInSqlite(position);
            }
        });

        collectNameText.setText(collectDataArrayList.get(position).getCollectUserName());

    }

    @Override
    public int getItemCount() {
        return collectDataArrayList.size();
    }

    private class DownloadImage extends AsyncTask<Void, Void, Bitmap> {
        String name;
        int type ;
        public DownloadImage(String name, int type){
            this.name = name;
            this.type = type;
        }
        @Override
        protected Bitmap doInBackground(Void... params) {
            String url;
            if(type == 0) {
                url = SERVER_ADDRESS + name;
            }else{
                url = SERVER_PROFILE_ADDRESS + name;
            }
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
                if(type == 0) {
                    collectContentImg.setImageBitmap(bitmap);
                }else{
                    collectProfileImg.setImageBitmap(bitmap);
                }
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }



    // 請求朋友邀請
    public void requestFriend(String collectId, String collectUserName){
        IMConnection connection = Client_UserHandler.getConnection();
        Friend friend = new Friend();
        friend.setId(loginId);
        friend.setFriendId(collectId);
        friend.setFriendUserName(collectUserName);

        IMResponse resp = new IMResponse();
        Header header = new Header();
        header.setHandlerId(Handlers.USER);
        header.setCommandId(Commands.FRIEND_ADD_REQUEST);
        resp.setHeader(header);
        resp.writeEntity(new FriendDTO(friend));
        connection.sendResponse(resp);
    }

    //刪除收集的內容
    private void deleteCollectContentInSqlite(int position){
        //創建MyDBHelper對象
        MyDBHelper dbHelper = new MyDBHelper(mContext , "Chat.db", null, 1);
        //得到一個可讀的SQLiteDatabase對象
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //生成ContentValues​​對象 //key:列名，value:想插入的值
        ContentValues cv = new ContentValues();
        cv.put("isdelete", 1);//isdelete 1 : 代表刪除
        db.update("Collect", cv, "id=" + "=\"" + collectDataArrayList.get(position).getCollectId() + "\"", null);
        //關閉數據庫
        db.close();

        collectDataArrayList.remove(position);

        notifyDataSetChanged();
    }

    //判別收集的人是否為好友
    private void differentiateFriend(){
        for(int i = 0 ; i < collectDataArrayList.size() ; i++){
            if(friendArrayList.contains(collectDataArrayList.get(i).getCollectId())){
                addCollectFriendImg.setImageResource(R.drawable.bubble);
            }
        }
    }

}
