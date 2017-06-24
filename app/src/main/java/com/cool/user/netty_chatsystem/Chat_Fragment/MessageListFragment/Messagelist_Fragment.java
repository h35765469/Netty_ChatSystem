package com.cool.user.netty_chatsystem.Chat_Fragment.MessageListFragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.cool.user.netty_chatsystem.ChatListViewAdapter.FriendRowItem;
import com.cool.user.netty_chatsystem.ChatListViewAdapter.MessageListAdapter;
import com.cool.user.netty_chatsystem.ChatListViewAdapter.RowItem;
import com.cool.user.netty_chatsystem.Chat_Client.handler.Client_MessageHandler;
import com.cool.user.netty_chatsystem.Chat_Client.handler.Client_UserHandler;
import com.cool.user.netty_chatsystem.Chat_Fragment.BaseFragment;
import com.cool.user.netty_chatsystem.Chat_Fragment.CameraFragment.OpenCameraFragment;
import com.cool.user.netty_chatsystem.Chat_Fragment.ChatFragment.ChatFragment;
import com.cool.user.netty_chatsystem.Chat_Fragment.SearchFragment;
import com.cool.user.netty_chatsystem.Chat_Service.ChatBroadcastReceiver;
import com.cool.user.netty_chatsystem.Chat_Sharepreference.SharePreferenceManager;
import com.cool.user.netty_chatsystem.Chat_Sqlite_ChatHistory.MyDBHelper;
import com.cool.user.netty_chatsystem.Chat_biz.entity.Friend;
import com.cool.user.netty_chatsystem.Chat_biz.entity.Message_entity;
import com.cool.user.netty_chatsystem.Chat_biz.entity.file.ServerFile;
import com.cool.user.netty_chatsystem.Chat_server.dto.FileDTO;
import com.cool.user.netty_chatsystem.R;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by user on 2016/3/11.
 */
public class Messagelist_Fragment extends BaseFragment {

    ChatBroadcastReceiver chatBroadcastReceiver = new ChatBroadcastReceiver();

    MessageListAdapter messageAdapter;

    ListView Messagelist_listview;

    //使用者名字
    private String username;

    //發送廣播事件用的Action
    public static final String BROADCAT_ACTION = "com.netty_chatsystem.ChatBroadcast";



    Handler messageHandler = new Handler(){
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            FriendRowItem friendRowItem = (FriendRowItem)msg.getData().getSerializable("updateMessage");
            updateMessage(friendRowItem);
        }
    };

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public View initView(LayoutInflater inflater){
        View view = inflater.inflate(R.layout.activity_messagelist_,null);
        TextView noMessageText = (TextView)view.findViewById(R.id.noMessageText);
        //設置上方搜尋按鈕和照片按鈕------------------------------------------------------------------------------------------------------------------
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(),"fonts/fontawesome-webfont.ttf");
        TextView searchbarTxt = (TextView) view.findViewById(R.id.searchbarTxt);
        TextView newpostTxt = (TextView) view.findViewById(R.id.newpostTxt);
        TextView messageNumberTxt = (TextView)view.findViewById(R.id.messageNumberTxt);
        searchbarTxt.setTypeface(font);
        searchbarTxt.setText("\uf002");
        newpostTxt.setTypeface(font);
        newpostTxt.setText("\uf030");
        //--------------------------------------------------------------------------------------------------------------------------------------------------------------

        registerChatListener();//註冊訊息傳送過來後的各種監聽器
        setHasOptionsMenu(true);

        // Session class instance
        // Session Manager Class
        SharePreferenceManager sharePreferenceManager = new SharePreferenceManager(getActivity().getApplicationContext());

        /**
         * Call this function whenever you want to check user login
         * This will redirect user to LoginActivity is he is not
         * logged in
         * */
        // get user data from session
        HashMap<String, String> user = sharePreferenceManager.getUserDetails();

        // name
        username = user.get(SharePreferenceManager.KEY_NAME);

        chatBroadcastReceiver.setChangeMessageListListener(new ChatBroadcastReceiver.ChangeMessageListListener() {
            @Override
            public void onChangeMessageListEvent(String fromId, String from, String fromNickName,  String content, long createTime) {
                int avatarDrawable;
                if(content.equals("貼圖來臨")){
                    avatarDrawable = R.drawable.smile;
                }else if(content.equals("驚喜來臨")){
                    avatarDrawable = R.drawable.gift;
                }else{
                    avatarDrawable = R.drawable.word_blue;
                }
                FriendRowItem friendRowItem = new FriendRowItem(fromId, from, fromNickName, BitmapFactory.decodeResource(getResources(), avatarDrawable));//少了friendid
                friendRowItem.setContent(content);
                Bundle bundle = new Bundle();
                bundle.putSerializable("updateMessage", friendRowItem);
                bundle.putLong("createTime", createTime);
                Message msg = new Message();
                msg.setData(bundle);
                messageHandler.sendMessage(msg);
            }
        });
        Messagelist_listview = (ListView)view.findViewById(R.id.messagelist_listview);


        final ArrayList<FriendRowItem>friendRowItemArrayList = loadMessageListInSqlite();


        messageAdapter = new MessageListAdapter(getActivity(), friendRowItemArrayList);

        messageNumberTxt.setText("(" + String.valueOf(messageAdapter.getCount()) + ")");//所有訊息的數量

        if(friendRowItemArrayList.size() == 0){
            noMessageText.setVisibility(View.VISIBLE);
        }






        //設置處理message的訊息列條監聽
        Messagelist_listview.setAdapter(messageAdapter);
        Messagelist_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FriendRowItem rowItem = (FriendRowItem) Messagelist_listview.getItemAtPosition(position);
                Bundle bundle = new Bundle();
                bundle.putString("friendId", rowItem.getFriendId());
                bundle.putInt("whichFragment", R.id.messageListContainer);
                ChatFragment chatFragment = new ChatFragment();
                chatFragment.setArguments(bundle);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.messageListContainer, chatFragment);
                fragmentTransaction.commit();
            }
        });

        Bundle bundle = getArguments();//從RootMessageListFragment來的為了點擊上方提醒欄後可以直接開啟聊天室
        if(bundle != null){
            if(bundle.getInt("finishOpen") != 1) {
                bundle.putInt("whichFragment", 1);
                bundle.putInt("finishOpen", 1);
                ChatFragment chatFragment = new ChatFragment();
                chatFragment.setArguments(bundle);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.messageListContainer, chatFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        }

        searchbarTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Bundle searchBarBundle = new Bundle();
                searchBarBundle.putInt("whichFragment", R.id.messageListContainer);
                SearchFragment searchFragment = new SearchFragment();
                searchFragment.setArguments(searchBarBundle);
                fragmentTransaction.replace(R.id.messageListContainer, searchFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        newpostTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Bundle newPostBundle = new Bundle();
                newPostBundle.putInt("whichFragment", R.id.messageListContainer);
                OpenCameraFragment openCameraFragment = new OpenCameraFragment();
                openCameraFragment.setArguments(newPostBundle);
                fragmentTransaction.replace(R.id.messageListContainer, openCameraFragment);
                fragmentTransaction.addToBackStack("backFragment");
                fragmentTransaction.commit();
            }
        });

        return view;
    }

    @Override
    public void initData(Bundle savedInstanceState){

    }



   @Override
    public void onPrepareOptionsMenu(Menu menu){
   }

    //註冊接受訊息和檔案的監聽器
    public void registerChatListener(){
        //接收訊息文字
        Client_MessageHandler client_messageHandler = new Client_MessageHandler();
        client_messageHandler.setListener(new Client_MessageHandler.Listener() {
            @Override
            public void onInterestingEvent(Message_entity message) {
                saveSqliteHistoryFromBrocastReceiver(message.getMessage(), message.getId(), message.getToId(), "0",0, message.getCreateAt());

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

                //更新listView的狀態----------------------------------------------------------------------------------------------------------------------------------------------------------
                int avatarDrawable = R.drawable.word_blue;
                FriendRowItem friendRowItem = new FriendRowItem(message.getId(), message.getFrom(), message.getFromNickName(), BitmapFactory.decodeResource(getResources(), avatarDrawable));
                friendRowItem.setContent(message.getMessage());
                Bundle bundle = new Bundle();
                bundle.putSerializable("updateMessage", friendRowItem);
                bundle.putLong("createTime", message.getCreateAt());
                Message msg = new Message();
                msg.setData(bundle);
                messageHandler.sendMessage(msg);
                //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

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

                if (serverFile.getEffectMessage() == -2) {
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
                    intent.putExtra("createTime", serverFile.getSendTime());

                    //type 0 : 訊息通知 1 : 朋友通知 2 :拒絕朋友
                    intent.putExtra("type",0);
                    getActivity().sendBroadcast(intent);

                    //更新listView的狀態----------------------------------------------------------------------------------------------------------------------------------------------------------
                    int avatarDrawable = R.drawable.c10;
                    FriendRowItem friendRowItem = new FriendRowItem(serverFile.getId(), serverFile.getSendId(),  serverFile.getSendNickName(), BitmapFactory.decodeResource(getResources(), avatarDrawable));
                    friendRowItem.setContent("貼圖來臨");
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("updateMessage", friendRowItem);
                    bundle.putLong("createTime", serverFile.getSendTime());
                    Message msg = new Message();
                    msg.setData(bundle);
                    messageHandler.sendMessage(msg);
                    //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

                } else {
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
                    intent.putExtra("createTime", serverFile.getSendTime());

                    //type 0 : 訊息通知 1 : 朋友通知 2 :拒絕朋友
                    intent.putExtra("type",0);
                    getActivity().sendBroadcast(intent);

                    //更新listView的狀態----------------------------------------------------------------------------------------------------------------------------------------------------------
                    int avatarDrawable = R.drawable.gift;
                    FriendRowItem friendRowItem = new FriendRowItem(serverFile.getId(), serverFile.getSendId(),  serverFile.getSendNickName(), BitmapFactory.decodeResource(getResources(), avatarDrawable));
                    friendRowItem.setContent("驚喜來臨");
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("updateMessage", friendRowItem);
                    bundle.putLong("createTime", serverFile.getSendTime());
                    Message msg = new Message();
                    msg.setData(bundle);
                    messageHandler.sendMessage(msg);
                    //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

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
                saveNewFriend(friend);
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

    private void saveNewFriend(Friend friend){
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
        cv.put("to_id" , toId);

        cv.put("content", messageText);
        cv.put("type", type);
        cv.put("effectType", effectType);
        cv.put("createtime", createTime);


        //調用insert方法，將數據插入數據庫
        db.insert("Message", null, cv);
        //關閉數據庫
        db.close();
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

   public void updateMessage(FriendRowItem friendRowItem){
       if(!messageAdapter.inMessage(friendRowItem)) {
           messageAdapter.addMessage(friendRowItem);
       }else{
           messageAdapter.removeMessage(friendRowItem);
       }
       messageAdapter.notifyDataSetChanged();
       scroll();
   }

    private void scroll() {
        Messagelist_listview.setSelection(Messagelist_listview.getCount() - 1);
    }



    private ArrayList<FriendRowItem> loadMessageListInSqlite(){
        ArrayList<FriendRowItem>friendRowItemList = new ArrayList<FriendRowItem>();
        Bitmap avatar;

        //以下為從sqlite載回聊天紀錄
        MyDBHelper dbHelper =  new  MyDBHelper(getActivity() , "Chat.db" , null , 1 );
        //得到一個可寫的數據庫
        SQLiteDatabase db =dbHelper.getReadableDatabase();
        //參數1：表名
        //參數2：要想顯示的列
        //參數3：where子句
        //參數4：where子句對應的條件值
        //參數5：分組方式
        //參數6：having條件
        //參數7：排序方式
        //Cursor cursor = db.query("MessageOrder" ,  new  String[]{"friendusername", "content", "orders"},  "username=?" , new String[]{username}, null, null, null );
        Cursor cursor = db.rawQuery("SELECT Friend.id, Friend.friendusername, Friend.friendname,  MessageOrder.content, MessageOrder.createtime " +
                "FROM MessageOrder  INNER JOIN Friend ON MessageOrder.friendid = Friend.id ORDER BY MessageOrder.createtime",null);

        while (cursor.moveToNext()){
            String id = cursor.getString(cursor.getColumnIndex("id"));
            String friendusername = cursor.getString(cursor.getColumnIndex( "friendusername" ));
            String friendname = cursor.getString(cursor.getColumnIndex("friendname"));
            Long createtime = cursor.getLong(cursor.getColumnIndex("createtime"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            if(content.equals("貼圖來臨") || content.equals("你傳出貼圖")) {
                avatar = BitmapFactory.decodeResource(getResources(),R.drawable.smile);
            }else if(content.equals("驚喜來臨") || content.equals("你傳出驚喜")){
                avatar = BitmapFactory.decodeResource(getResources(), R.drawable.gift);
            }
            else{
                avatar = BitmapFactory.decodeResource(getResources(),R.drawable.word_blue);
            }

            FriendRowItem friendRowItem = new FriendRowItem(id, friendusername, friendname, avatar);
            friendRowItem.setContent(content);
            friendRowItem.setCreateTime(createtime);
            friendRowItemList.add(0, friendRowItem);
        }

        cursor.close();

        //關閉數據庫
        db.close();

        return friendRowItemList;
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

}
