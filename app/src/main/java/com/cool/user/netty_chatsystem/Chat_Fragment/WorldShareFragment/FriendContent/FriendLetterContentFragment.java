package com.cool.user.netty_chatsystem.Chat_Fragment.WorldShareFragment.FriendContent;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cool.user.netty_chatsystem.ChatListViewAdapter.RowItem;
import com.cool.user.netty_chatsystem.Chat_Client.handler.Client_MessageHandler;
import com.cool.user.netty_chatsystem.Chat_Client.handler.Client_UserHandler;
import com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.MyContetnFragment.MyContentPreviewFragment;
import com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.MyContetnFragment.MyContentRecycleViewAdapter;
import com.cool.user.netty_chatsystem.Chat_Fragment.WorldShareFragment.MyLetterContentFragment.LetterContentAdapter;
import com.cool.user.netty_chatsystem.Chat_Fragment.WorldShareFragment.MyLetterContentFragment.LetterDBConnector;
import com.cool.user.netty_chatsystem.Chat_Fragment.WorldShareFragment.ShareContentFragment;
import com.cool.user.netty_chatsystem.Chat_MySQL.Config;
import com.cool.user.netty_chatsystem.Chat_Sharepreference.SharePreferenceManager;
import com.cool.user.netty_chatsystem.Chat_Sqlite_ChatHistory.MyDBHelper;
import com.cool.user.netty_chatsystem.Chat_biz.entity.Friend;
import com.cool.user.netty_chatsystem.Chat_biz.entity.Message_entity;
import com.cool.user.netty_chatsystem.Chat_biz.entity.file.ServerFile;
import com.cool.user.netty_chatsystem.Chat_server.dto.FileDTO;
import com.cool.user.netty_chatsystem.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by user on 2016/12/20.
 */
public class FriendLetterContentFragment extends Fragment {
    ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();
    ArrayList<FriendContentData> friendLetterContentArrayList;
    LetterContentAdapter letterContentAdapter;
    ListView friendLetterContentListView;
    TextView randContentText;
    String loginId;
    //發送廣播事件用的Action
    public static final String BROADCAT_ACTION = "com.netty_chatsystem.ChatBroadcast";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater ,ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.friendlettercontent_fragment , container, false);
        randContentText = (TextView)rootView.findViewById(R.id.randContentText);
        friendLetterContentListView = (ListView)rootView.findViewById(R.id.friendLetterContentListView);
        TextView noFirstContentText = (TextView)rootView.findViewById(R.id.noFirstContentText);

        registerChatListener();//註冊監聽器

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());

        SharePreferenceManager sharePreferenceManager = new SharePreferenceManager(getActivity());
        loginId = sharePreferenceManager.getLoginId();

        if(Client_UserHandler.getConnection() !=null) {
            friendLetterContentArrayList = friendLetterContentFromMySQL();
            FriendContentRecycleViewAdapter friendContentRecycleViewAdapter = new FriendContentRecycleViewAdapter(getActivity(), friendLetterContentArrayList);
            RecyclerView friendContentRecycleView  = (RecyclerView)rootView.findViewById(R.id.friendContentRecycleView);
            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            friendContentRecycleView.setLayoutManager(llm);
            friendContentRecycleView.setAdapter(friendContentRecycleViewAdapter);
            friendContentRecycleViewAdapter.setOnItemClickListener(new FriendContentRecycleViewAdapter.ClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    if(Client_UserHandler.getConnection() != null){
                        Bundle bundle = new Bundle();
                        String[] friendContentArray = new String[friendLetterContentArrayList.size()];
                        for(int i = 0 ; i < friendLetterContentArrayList.size() ; i++){
                            friendContentArray[i] = friendLetterContentArrayList.get(i).getContent();
                        }
                        bundle.putInt("position", position);
                        bundle.putParcelableArrayList("friendContentArrayList", friendLetterContentArrayList);
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.addToBackStack(null);
                        FriendContentPreviewFragment friendContentPreviewFragment = new FriendContentPreviewFragment();
                        friendContentPreviewFragment.setArguments(bundle);
                        fragmentTransaction.replace(R.id.shareContentContainer, friendContentPreviewFragment);
                        fragmentTransaction.commit();
                    }else{
                        Toast.makeText(getActivity(), "無法進入驚喜時刻，請確認網路連線", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            if(friendLetterContentArrayList.size() == 0){
                noFirstContentText.setVisibility(View.VISIBLE);
            }
            /*for (int i = 0; i < friendLetterContentArrayList.size(); i++) {
                new DownloadImage(friendLetterContentArrayList.get(i).getContent(), friendLetterContentArrayList).execute();
            }*/
        }else{
            Toast.makeText(getActivity(), "無法獲得朋友驚喜，請確認連線狀態", Toast.LENGTH_SHORT).show();
        }

        randContentText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction  = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.shareContentContainer, new ShareContentFragment());
                fragmentTransaction.commit();
            }
        });

        friendLetterContentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(Client_UserHandler.getConnection() != null){
                    //ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    //bitmapArrayList.get(position).compress(Bitmap.CompressFormat.PNG, 100, stream);
                    //byte[] byteArray = stream.toByteArray();
                    Bundle bundle = new Bundle();
                    //bundle.putByteArray("byteArray", byteArray);
                    String[] friendContentArray = new String[friendLetterContentArrayList.size()];
                    for(int i = 0 ; i < friendLetterContentArrayList.size() ; i++){
                        friendContentArray[i] = friendLetterContentArrayList.get(i).getContent();
                    }
                    bundle.putInt("position", position);
                    bundle.putParcelableArrayList("friendContentArrayList", friendLetterContentArrayList);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.addToBackStack(null);
                    FriendContentPreviewFragment friendContentPreviewFragment = new FriendContentPreviewFragment();
                    friendContentPreviewFragment.setArguments(bundle);
                    fragmentTransaction.replace(R.id.shareContentContainer, friendContentPreviewFragment);
                    fragmentTransaction.commit();
                }else{
                    Toast.makeText(getActivity(), "無法進入驚喜時刻，請確認網路連線", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    private ArrayList<FriendContentData> friendLetterContentFromMySQL(){
        ArrayList<FriendContentData>friendLetterContentArrayList = new ArrayList<>();

        try {
            String result = LetterDBConnector.executeQuery(loginId);
            /*
                      SQL 結果有多筆資料時使用JSONArray
                      只有一筆資料時直接建立JSONObject物件
                      JSONObject jsonData = new JSONObject(result);
                      */
            if(!result.equals("\"\"\n")) {
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonData = jsonArray.getJSONObject(i);
                    FriendContentData friendContentData = new FriendContentData();
                    friendContentData.setOwnerId(jsonData.getString("ownerid"));
                    friendContentData.setOwnerNickName(jsonData.getString("nickname"));
                    friendContentData.setOwnerProfileName(jsonData.getString("profilename"));
                    friendContentData.setContent (jsonData.getString("content"));
                    friendContentData.setThink(jsonData.getString("think"));
                    friendContentData.setEffect(jsonData.getInt("effect"));
                    friendLetterContentArrayList.add(friendContentData);
                }


            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return friendLetterContentArrayList;
    }

    //註冊離開時的接受訊息和檔案的監聽器
    public void registerChatListener(){
        //接收訊息文字
        Client_MessageHandler client_messageHandler = new Client_MessageHandler();
        client_messageHandler.setListener(new Client_MessageHandler.Listener() {
            @Override
            public void onInterestingEvent(Message_entity message) {

                saveSqliteHistoryFromBrocastReceiver(message.getMessage(), message.getId(), message.getToId(), "0", 0, message.getCreateAt());

                //更新messagelist_fragment狀態------------------------------------------------------------------
                RowItem rowItem = new RowItem(message.getId(), message.getFrom());
                rowItem.setContent(message.getMessage());
                rowItem.setCreateTime(message.getCreateAt());
                updateMessageList(rowItem);
                //------------------------------------------------------------------------------------------------------------

                Intent intent = new Intent(BROADCAT_ACTION);
                intent.putExtra("fromId", message.getId());
                intent.putExtra("from", message.getFrom());
                intent.putExtra("fromNickName", message.getFromNickName());
                intent.putExtra("content", message.getMessage());
                intent.putExtra("createTime", message.getCreateAt());

                //type 0 : 訊息通知 1 : 朋友通知 2 :拒絕朋友
                intent.putExtra("type",0);
                getActivity().sendBroadcast(intent);
            }
        });

        //接受檔案訊息
        client_messageHandler.setReceiveFileListener(new Client_MessageHandler.receiveFileListener() {
            @Override
            public void onReceiveFileEvent(FileDTO fileDTO) {
                long createTime = fileDTO.getServerFile().getSendTime();
                ServerFile serverFile = fileDTO.getServerFile();
                int sumCountPackage = serverFile.getSumCountPackage();
                int countPackage = serverFile.getCountPackage();
                byte[] bytes = serverFile.getBytes();

                //將檔名變更為hashcode的形式
                String fileName = String.valueOf(serverFile.getFileName().hashCode());

                ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
                File directory = cw.getDir("chatDir", Context.MODE_PRIVATE);

                File cacheDir = new File(directory, fileName);



                try {
                    RandomAccessFile randomAccessFile = new RandomAccessFile(cacheDir, "rw");
                    randomAccessFile.seek(countPackage * 1024 - 1024);
                    randomAccessFile.write(bytes);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(serverFile.getEffectMessage() == -2){
                    saveSqliteFileFromBrocastReceiver(cacheDir.getAbsolutePath(), serverFile.getId(), serverFile.getToId(), "2", 0, fileDTO.getServerFile().getSendTime(), fileDTO.getServerFile().getThink());

                    //更新messagelist_fragment的狀態-------------------------------------------------------------------------------
                    RowItem rowItem = new RowItem(serverFile.getId(), serverFile.getSendId());
                    rowItem.setContent("貼圖來臨");
                    rowItem.setCreateTime(serverFile.getSendTime());
                    updateMessageList(rowItem);
                    //---------------------------------------------------------------------------------------------------------------------------

                    Intent intent = new Intent(BROADCAT_ACTION);
                    intent.putExtra("fromId", serverFile.getId());
                    intent.putExtra("from", serverFile.getSendId());
                    intent.putExtra("fromNickName", serverFile.getSendNickName());
                    intent.putExtra("content", "貼圖來臨");
                    intent.putExtra("createTime", createTime);

                    //type 0 : 訊息通知 1 : 朋友通知 2 :拒絕朋友
                    intent.putExtra("type",0);
                    getActivity().sendBroadcast(intent);
                }else{
                    saveSqliteFileFromBrocastReceiver(cacheDir.getAbsolutePath(), serverFile.getId(), serverFile.getToId(), "1", serverFile.getEffectMessage(), fileDTO.getServerFile().getSendTime(), fileDTO.getServerFile().getThink());

                    //更新messagelist_fragment的狀態-------------------------------------------------------------------------------
                    RowItem rowItem = new RowItem(serverFile.getId(), serverFile.getSendId());
                    rowItem.setContent("驚喜來臨");
                    rowItem.setCreateTime(serverFile.getSendTime());
                    updateMessageList(rowItem);
                    //---------------------------------------------------------------------------------------------------------------------------

                    Intent intent = new Intent(BROADCAT_ACTION);
                    intent.putExtra("fromId", serverFile.getId());
                    intent.putExtra("from", serverFile.getSendId());
                    intent.putExtra("fromNickName", serverFile.getSendNickName());
                    intent.putExtra("content", "驚喜來臨");
                    intent.putExtra("createTime", createTime);

                    //type 0 : 訊息通知 1 : 朋友通知 2 :拒絕朋友
                    intent.putExtra("type",0);
                    getActivity().sendBroadcast(intent);
                }

            }
        });

        //註冊已讀過來的監聽器
        client_messageHandler.setAlreadyReadListener(new Client_MessageHandler.alreadyReadListener() {
            @Override
            public void onAlreadyReadEvent(Message_entity message) {
                updateReadInSqlite();
            }
        });

        //接收好友要求
        Client_UserHandler client_userHandler = new Client_UserHandler();
        client_userHandler.setRequestFriendListListener( new Client_UserHandler.RequestFriendListListener() {
            @Override
            public void onRequestFriendListEvent(Friend friend) {
                saveInviteFriend(friend);
                Intent intent = new Intent(BROADCAT_ACTION);
                intent.putExtra("fromId", friend.getFriendId());
                intent.putExtra("from", friend.getFriendName());
                intent.putExtra("content", "新朋友請求 ");

                //type 0 : 訊息通知 1 : 朋友通知 2 :拒絕朋友
                intent.putExtra("type", 1);
                getActivity().sendBroadcast(intent);
            }
        });

    }


    //儲存來自brocastReceiver的聊天檔案
    private void saveSqliteFileFromBrocastReceiver(String messageText , String fromId , String toId , String type, int effectType, long createTime, String think){
        //創建MyDBHelper對象
        MyDBHelper dbHelper = new MyDBHelper(getActivity() , "Chat.db", null, 1);
        //得到一個可讀的SQLiteDatabase對象
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //生成ContentValues​​對象 //key:列名，value:想插入的值
        ContentValues cv = new ContentValues();

        //往ContentValues​​對象存放數據，鍵-值對模式
        cv.put("from_id", fromId);
        cv.put("to_id", toId);

        cv.put("content", messageText);
        cv.put("type", type);
        cv.put("effectType", effectType);
        cv.put("createtime", createTime);
        cv.put("think", think);


        //調用insert方法，將數據插入數據庫
        db.insert("Message", null, cv);
        //關閉數據庫
        db.close();
    }

    //儲存來自brocastReceiver的聊天訊息
    private void saveSqliteHistoryFromBrocastReceiver(String messageText , String fromId , String toId , String type, int effectType, long createTime){
        //創建MyDBHelper對象
        MyDBHelper dbHelper = new MyDBHelper(getActivity() , "Chat.db", null, 1);
        //得到一個可讀的SQLiteDatabase對象
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //生成ContentValues​​對象 //key:列名，value:想插入的值
        ContentValues cv = new ContentValues();

        //往ContentValues​​對象存放數據，鍵-值對模式
        cv.put("from_id", fromId);
        cv.put("to_id", toId);

        cv.put("content", messageText);
        cv.put("type", type);
        cv.put("effectType", effectType);
        cv.put("createtime", createTime);


        //調用insert方法，將數據插入數據庫
        db.insert("Message", null, cv);
        //關閉數據庫
        db.close();
    }

    //更新messagelist_fragment的狀態
    public void updateMessageList(RowItem rowItem){
        //以下為從sqlite載回聊天紀錄
        MyDBHelper dbHelper =  new  MyDBHelper(getActivity(), "Chat.db", null , 1 );
        //得到一個可寫的數據庫
        SQLiteDatabase db =dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from MessageOrder where friendid=?", new String[]{rowItem.getWhoId()});
        if(cursor.getCount() > 0){
            System.out.println("updateMessage");
            updateMessageListInSqlite(rowItem);
        }else{
            System.out.println("saveMessage");
            saveMessageListInSqlite(rowItem);
        }
        db.close();
        cursor.close();
    }

    //更新sqlite裡所有已讀狀態
    private void updateReadInSqlite(){
        //創建MyDBHelper對象
        MyDBHelper dbHelper = new MyDBHelper(getActivity() , "Chat.db", null, 1);
        //得到一個可讀的SQLiteDatabase對象
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //生成ContentValues​​對象 //key:列名，value:想插入的值
        ContentValues cv = new ContentValues();
        cv.put("read", "1");
        db.update("Message", cv, "read" + "= 0", null);
        db.close();
    }

    private void saveInviteFriend(Friend friend){
        //創建MyDBHelper對象
        MyDBHelper dbHelper = new MyDBHelper(getActivity(), "Chat.db", null, 1);
        //得到一個可讀的SQLiteDatabase對象
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        ContentValues cv = new ContentValues();

        //往ContentValues​​對象存放數據，鍵-值對模式
        cv.put("id", friend.getFriendId());
        cv.put("friendusername", friend.getFriendUserName());
        cv.put("friendname",friend.getFriendName());
        cv.put("friendAvatarUri", friend.getFriendAvatarUri());
        cv.put("status", 0);//status 0 : 代表認證中
        cv.put("viewer", 0);
        cv.put("favorite", 0);

        //調用insert方法，將數據插入數據庫
        db.insert("Friend", null, cv);

        db.close();
    }

    //更新sqlite裡的messagelist的資料
    private void updateMessageListInSqlite(RowItem rowItem){
        //創建MyDBHelper對象
        MyDBHelper dbHelper = new MyDBHelper(getActivity(), "Chat.db", null, 1);
        //得到一個可讀的SQLiteDatabase對象
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //生成ContentValues​​對象 //key:列名，value:想插入的值
        ContentValues cv = new ContentValues();
        cv.put("content", rowItem.getContent());
        cv.put("createtime", rowItem.getCreateTime());
        db.update("MessageOrder", cv, "friendid" + "=\"" + rowItem.getWhoId() + "\"", null);
        db.close();
    }

    //儲存MessageList進入sqlite
    private void saveMessageListInSqlite(RowItem rowItem){
        //創建MyDBHelper對象
        MyDBHelper dbHelper = new MyDBHelper(getActivity(), "Chat.db", null, 1);
        //得到一個可讀的SQLiteDatabase對象
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //生成ContentValues​​對象 //key:列名，value:想插入的值
        ContentValues cv = new ContentValues();

        //往ContentValues​​對象存放數據，鍵-值對模式
        cv.put("friendid",rowItem.getWhoId());
        cv.put("content" ,rowItem.getContent());
        cv.put("createTime", rowItem.getCreateTime());


        //調用insert方法，將數據插入數據庫
        db.insert("MessageOrder", null, cv);

        //關閉數據庫
        db.close();
    }

}
