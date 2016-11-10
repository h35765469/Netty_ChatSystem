package com.example.user.netty_chatsystem;


import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.user.netty_chatsystem.Chat_Client.handler.Client_MessageHandler;
import com.example.user.netty_chatsystem.Chat_Fragment.FriendListFragment.RootFriendListFragment;
import com.example.user.netty_chatsystem.Chat_Fragment.LockableViewPager;
import com.example.user.netty_chatsystem.Chat_Fragment.MessageListFragment.RootMessageListFragment;
import com.example.user.netty_chatsystem.Chat_Fragment.ProfileFragment.RootProfileFragment;
import com.example.user.netty_chatsystem.Chat_Fragment.ViewPagerAdapter;
import com.example.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.fragment.RootWhiteBoardFragment;
import com.example.user.netty_chatsystem.Chat_Fragment.WorldShareFragment.RootWorldShareFragment;
import com.example.user.netty_chatsystem.Chat_Sqlite_ChatHistory.MyDBHelper;
import com.example.user.netty_chatsystem.Chat_biz.entity.Message_entity;
import com.example.user.netty_chatsystem.Chat_biz.entity.file.ServerFile;
import com.example.user.netty_chatsystem.Chat_server.dto.FileDTO;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class  Character_Activity extends FragmentActivity {
    public static LockableViewPager mViewPager;
    private List<Fragment>mFragmentList = new ArrayList<Fragment>();
    private ViewPagerAdapter mViewPagerAdapter;


    //**以下相關函式皆為通知MessageListFragment更改介面元件使用
    public static ChangeMessageListListener mChangeMessageListListener;

    public interface ChangeMessageListListener{
        public void onChangeMessageListEvent(String from , String content);
    }

    public void setChangeMessageListListener(ChangeMessageListListener mChangeMessageListListener){
        this.mChangeMessageListListener = mChangeMessageListListener;
    }

    public void ChatBroadcastReceiverDoes(String from , String content){
        mChangeMessageListListener.onChangeMessageListEvent(from,content);
    }
    //****************************************************************


    @Override
    public  boolean onCreateOptionsMenu(Menu menu) {
        return  super .onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch(id){
            case android.R.id.home:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_template);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle != null) {
            //使用json獲取fb好友
            String jsondata = bundle.getString("fbFriend");
            JSONArray jsonFriendList;
            ArrayList<String> friendList = new ArrayList<>();
            if(jsondata != null) {
                try {
                    jsonFriendList = new JSONArray(jsondata);
                    String gg = "";
                    for (int i = 0; i < jsonFriendList.length(); i++) {
                        friendList.add(jsonFriendList.getJSONObject(i).getString("name"));
                        gg += friendList.get(i);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        mViewPager = (LockableViewPager)findViewById(R.id.pager);
        registerMessageReceiver();
        init(savedInstanceState , bundle);
    }

    private void init(Bundle savedInstanceState, Bundle bundle ){
        mFragmentList.add(new RootProfileFragment());
        mFragmentList.add(new RootWorldShareFragment());
        mFragmentList.add(new RootWhiteBoardFragment());
        mFragmentList.add(new RootFriendListFragment());
        mFragmentList.add(new RootMessageListFragment());

        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),mFragmentList);
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setCurrentItem(2);


    }

    public void registerMessageReceiver(){
        //接收訊息文字
        Client_MessageHandler client_messageHandler = new Client_MessageHandler();
        client_messageHandler.setListener(new Client_MessageHandler.Listener() {
            @Override
            public void onInterestingEvent(Message_entity message) {
                saveSqliteHistory(message.getMessage(), message.getFrom(), message.getTo(), "0");
                ChatBroadcastReceiverDoes(message.getFrom() , message.getMessage());
            }
        });

        //接受檔案訊息
        client_messageHandler.setReceiveFileListener(new Client_MessageHandler.receiveFileListener() {
            @Override
            public void onReceiveFileEvent(FileDTO fileDTO) {
                ServerFile serverFile = fileDTO.getServerFile();
                int sumCountPackage = serverFile.getSumCountPackage();
                int countPackage = serverFile.getCountPackage();
                byte[] bytes = serverFile.getBytes();

                //將檔名變更為hashcode的形式
                String fileName = String.valueOf(serverFile.getFileName().hashCode());

                //Find the dir to save cached images
                File cacheDir;
                if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
                    //Creates a new File instance from a parent abstract pathname and a child pathname string.
                    cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), "TTImages_cache");
                else
                    cacheDir = getCacheDir();
                if (!cacheDir.exists())
                    cacheDir.mkdirs();

                //deleteDirectory(cacheDir);

                cacheDir = new File(cacheDir, fileName);


                try {
                    RandomAccessFile randomAccessFile = new RandomAccessFile(cacheDir, "rw");
                    randomAccessFile.seek(countPackage * 1024 - 1024);
                    randomAccessFile.write(bytes);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                saveSqliteHistory(cacheDir.getAbsolutePath(), serverFile.getSendId(), serverFile.getReceiveId(), "1");
                ChatBroadcastReceiverDoes(serverFile.getSendId(),"驚喜來臨");
            }
        });

    }


    //儲存資料進sqlite裡
    private void saveSqliteHistory(String messageText , String from , String to , String type){
        //創建MyDBHelper對象
        MyDBHelper dbHelper = new MyDBHelper(Character_Activity.this , "Chat.db", null, 1);
        //得到一個可讀的SQLiteDatabase對象
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //生成ContentValues​​對象 //key:列名，value:想插入的值
        ContentValues cv = new ContentValues();

        //往ContentValues​​對象存放數據，鍵-值對模式
        cv.put("from_id", from);
        cv.put("to_id" , to);

        cv.put("content", messageText);
        cv.put("type", type);


        //調用insert方法，將數據插入數據庫
        db.insert("Message", null, cv);
        //關閉數據庫
        db.close();

    }
}
