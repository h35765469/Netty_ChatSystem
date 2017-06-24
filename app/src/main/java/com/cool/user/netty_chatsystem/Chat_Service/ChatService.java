package com.cool.user.netty_chatsystem.Chat_Service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;

import com.cool.user.netty_chatsystem.Chat_Client.ChatClient;
import com.cool.user.netty_chatsystem.Chat_Client.handler.Client_MessageHandler;
import com.cool.user.netty_chatsystem.Chat_Client.handler.Client_UserHandler;
import com.cool.user.netty_chatsystem.ChatListViewAdapter.RowItem;
import com.cool.user.netty_chatsystem.Chat_Fragment.SendContentFragment.AndroidMultiPartEntity;
import com.cool.user.netty_chatsystem.Chat_MySQL.Config;
import com.cool.user.netty_chatsystem.Chat_MySQL.DBConnector;
import com.cool.user.netty_chatsystem.Chat_Sharepreference.SharePreferenceManager;
import com.cool.user.netty_chatsystem.Chat_Sqlite_ChatHistory.MyDBHelper;
import com.cool.user.netty_chatsystem.Chat_biz.entity.Friend;
import com.cool.user.netty_chatsystem.Chat_biz.entity.Message_entity;
import com.cool.user.netty_chatsystem.Chat_biz.entity.file.ServerFile;
import com.cool.user.netty_chatsystem.Chat_core.connetion.IMConnection;
import com.cool.user.netty_chatsystem.Chat_core.protocol.Commands;
import com.cool.user.netty_chatsystem.Chat_core.protocol.Handlers;
import com.cool.user.netty_chatsystem.Chat_core.transport.Header;
import com.cool.user.netty_chatsystem.Chat_core.transport.IMResponse;
import com.cool.user.netty_chatsystem.Chat_server.dto.FileDTO;
import com.cool.user.netty_chatsystem.Chat_server.dto.FriendDTO;
import com.cool.user.netty_chatsystem.Chat_server.dto.MessageDTO;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by user on 2016/7/8.
 */
public class ChatService extends Service {
    ChatClient chatClient = new ChatClient();
    String username , password, nickName;
    ChatBinder chatBinder = new ChatBinder();


    //發送廣播事件用的Action
    public static final String BROADCAT_ACTION = "com.netty_chatsystem.ChatBroadcast";

    //建立接受廣播接收元件
    ChatBroadcastReceiver chatBroadcastReceiver = new ChatBroadcastReceiver();

    //宣告client_MessageHandler用來獲取message
    Client_MessageHandler client_messageHandler;

    public static RandomShareContentListener randomShareContentListener;
    public static ChatServiceKickUserListener chatServiceKickUserListener;
    public static ChatServiceLoginUserListener chatServiceLoginUserListener;

    public interface RandomShareContentListener{
        public void onRandomShareContentEvent();
    }
    public interface ChatServiceKickUserListener{
        public void onChatServiceKickUserEvent();
    }
    public interface ChatServiceLoginUserListener{
        public void onChatServiceLoginUserEvent();
    }

    public void setRandomShareContentListener(RandomShareContentListener randomShareContentListener1){
        randomShareContentListener = randomShareContentListener1;
    }
    public void setChatServiceKickUserListener(ChatServiceKickUserListener chatServiceKickUserListener1){
        chatServiceKickUserListener = chatServiceKickUserListener1;
    }
    public void setChatServiceLoginUserListener(ChatServiceLoginUserListener chatServiceLoginUserListener1){
        chatServiceLoginUserListener = chatServiceLoginUserListener1;
    }

    public void randomShareContentDoes(){
        randomShareContentListener.onRandomShareContentEvent();
    }
    public void chatServiceKickUserDoes(){
        chatServiceKickUserListener.onChatServiceKickUserEvent();
    }
    public void chatServiceLoginUserDoes(){
        chatServiceLoginUserListener.onChatServiceLoginUserEvent();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("ChatService onCreate");
        registerReceiver(chatBroadcastReceiver, new IntentFilter(BROADCAT_ACTION));
    }

    @Override
    public int onStartCommand(Intent intent , int flags , int startId){
        System.out.println("ChatService onStartCommand out");
        if(intent.getStringExtra("username") != null || intent.getStringExtra("password") != null) {
            System.out.println("ChatService onStartCommand in");
            username = intent.getStringExtra("username");
            password = intent.getStringExtra("password");
            nickName = intent.getStringExtra("nickName");
            try {
                chatClient.run(username, password, nickName);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //sharePreferenceManager.saveFirstLogin(1);

            //接收訊息文字
            client_messageHandler = new Client_MessageHandler();
            client_messageHandler.setListener(new Client_MessageHandler.Listener() {
                @Override
                public void onInterestingEvent(Message_entity message) {
                    saveSqliteHistory(message.getMessage(), message.getId(), message.getToId(), "0", 0, message.getCreateAt());

                    //更新messagelist_fragment狀態------------------------------------------------------------------
                    RowItem rowItem = new RowItem(message.getId(), message.getFrom());
                    rowItem.setContent(message.getMessage());
                    rowItem.setCreateTime(message.getCreateAt());
                    updateMessageList(rowItem);
                    //------------------------------------------------------------------------------------------------------------

                    Intent intent = new Intent(BROADCAT_ACTION);
                    intent.putExtra("fromId", message.getId());
                    //intent.putExtra("from", message.getFrom());
                    intent.putExtra("fromNickName", message.getFromNickName());
                    intent.putExtra("content", message.getMessage());
                    intent.putExtra("createTime", message.getCreateAt());

                    //type 0 : 訊息通知 1 : 朋友通知 2 :拒絕朋友
                    intent.putExtra("type", 0);
                    sendBroadcast(intent);
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
                    /*File cacheDir;
                    if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
                        //Creates a new File instance from a parent abstract pathname and a child pathname string.
                        cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), "TTImages_cache");
                    else
                        cacheDir = getCacheDir();
                    if (!cacheDir.exists())
                        cacheDir.mkdirs();

                    //deleteDirectory(cacheDir);

                    cacheDir = new File(cacheDir, fileName);*/

                    ContextWrapper cw = new ContextWrapper(getApplicationContext());
                    File directory = cw.getDir("chatDir", Context.MODE_PRIVATE);

                    File cacheDir = new File(directory, fileName);


                    try {
                        RandomAccessFile randomAccessFile = new RandomAccessFile(cacheDir, "rw");
                        randomAccessFile.seek(countPackage * 1024 - 1024);
                        randomAccessFile.write(bytes);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //判斷接收過來的檔案是貼圖還是驚喜特效
                    if (serverFile.getEffectMessage() == -2) {
                        saveSqliteFileHistory(cacheDir.getAbsolutePath(), serverFile.getId(), serverFile.getToId(), "2", 0, fileDTO.getServerFile().getSendTime(), fileDTO.getServerFile().getThink());

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
                        intent.putExtra("createTime", fileDTO.getServerFile().getSendTime());

                        //type 0 : 訊息通知 1 : 朋友通知 2 :拒絕朋友
                        intent.putExtra("type", 0);
                        sendBroadcast(intent);
                    } else {
                        saveSqliteFileHistory(cacheDir.getAbsolutePath(), serverFile.getId(), serverFile.getToId(), "1", serverFile.getEffectMessage(), fileDTO.getServerFile().getSendTime(), fileDTO.getServerFile().getThink());

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
                        intent.putExtra("createTime", fileDTO.getServerFile().getSendTime());

                        //type 0 : 訊息通知 1 : 朋友通知 2 :拒絕朋友
                        intent.putExtra("type", 0);
                        sendBroadcast(intent);
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
            client_userHandler.setRequestFriendListListener(new Client_UserHandler.RequestFriendListListener() {
                @Override
                public void onRequestFriendListEvent(Friend friend) {
                    saveNewFriend(friend);
                    Intent intent = new Intent(BROADCAT_ACTION);
                    intent.putExtra("fromId", friend.getFriendId());
                    intent.putExtra("from", friend.getFriendName());
                    intent.putExtra("content", "新朋友請求 ");

                    //type 0 : 訊息通知 1 : 朋友通知 2 :拒絕朋友
                    intent.putExtra("type", 1);
                    sendBroadcast(intent);
                }
            });

            //當有人從別處登入強行登出
            client_userHandler.setKickUserListener(new Client_UserHandler.KickUserListener() {
                @Override
                public void onKickUserEvent() {
                    clearAllSqlite();//清除所有本地資料庫
                    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), Config.IMAGE_DIRECTORY_NAME);
                    deleteDir(mediaStorageDir);//刪除所有檔案資料夾
                    ContextWrapper cw = new ContextWrapper(getApplicationContext());
                    File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                    deleteDir(directory);
                    directory = cw.getDir("chatDir", Context.MODE_PRIVATE);
                    deleteDir(directory);

                    // Session Manager Class
                    SharePreferenceManager sharePreferenceManager = new SharePreferenceManager(ChatService.this);
                    //sharePreferenceManager.saveRestart(false);

                    //close the service
                    stopSelf();

                    IMConnection connection = Client_UserHandler.getConnection();
                    if(connection != null) {
                        IMResponse resp = new IMResponse();
                        Header header = new Header();
                        header.setHandlerId(Handlers.MESSAGE);
                        header.setCommandId(Commands.USER_LOGOUT_REQUEST);
                        resp.setHeader(header);
                        connection.sendResponse(resp);
                    }

                    // Clear the session data
                    // This will clear all session data and
                    // redirect user to LoginActivity
                    sharePreferenceManager.kickOutUser();
                    chatServiceKickUserDoes();

                    /*Intent i = new Intent();
                    i.setAction(Intent.ACTION_MAIN);
                    i.addCategory(Intent.CATEGORY_LAUNCHER);
                    //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.setComponent(new ComponentName(getApplicationContext().getPackageName(), MainActivity.class.getName()));
                    startActivity(i);*/

                }
            });

            //登入成功後獲得loginId並且獲得user資料
            client_userHandler.setLoginSuccessListener(new Client_UserHandler.LoginSuccessListener() {
                @Override
                public void onLoginSuccessEvent(String loginId, String loginNickName) {
                    System.out.println("LoginSuccessFuck");
                    SharePreferenceManager sharePreferenceManager = new SharePreferenceManager(getApplicationContext());
                    sharePreferenceManager.saveLoginId(loginId);
                    sharePreferenceManager.saveNickName(loginNickName);
                    sharePreferenceManager.setIsLogin();
                    if (sharePreferenceManager.getFirstLogin() == 0) {//初次登入時
                        createTable();//創立資料表
                        new registerUserDataInPhpServer(loginId, username, nickName, "").execute();//儲存會員資料進入php server
                        loadAllFriend(loginId);//全部朋友
                        loadFriendInvite(loginId);//載入朋友邀請
                        loadAllCollect(loginId);//全部收藏
                        //loadAllSticker(loginId);//全部貼圖
                        loadProfile(loginId);//大頭貼
                        loadOfflineMessage(loginId);//載入所有離線訊息
                        sharePreferenceManager.saveRestart(true);//讓service永久不會開閉
                        alarmManagerToDeleteMessage();//設置固定00:00刪除所有聊天紀錄
                        alarmManagerToKeepServiceAlive();//固定時間來激活chatService
                        sharePreferenceManager.saveFirstLogin(1);//設置為1代表不會再是第一次登入
                        chatServiceLoginUserDoes();//代表登入成功登入
                    } else {
                        loadAllFriend(loginId);//全部朋友
                        loadFriendInvite(loginId);//載入朋友邀請
                        loadOfflineMessage(loginId);//載入所有離線訊息
                        alarmManagerToDeleteMessage();//設置固定00:00刪除所有聊天紀錄
                        alarmManagerToKeepServiceAlive();//固定時間來激活chatService
                    }
                }
            });

            //接受拒絕好友的通知
            client_userHandler.setRejectFriendListener(new Client_UserHandler.RejectFriendListener() {
                @Override
                public void onRejectFriendEvent(Friend friend) {
                    deleteFriendInSqlite(friend.getId());//刪除在sqlite裡的朋友資料
                    Intent intent = new Intent(BROADCAT_ACTION);
                    intent.putExtra("fromId", friend.getId());
                    intent.putExtra("from", friend.getFriendName());
                    intent.putExtra("content", "拒絕成為你朋友");

                    //type 0 : 訊息通知 1 : 朋友邀請通知 2 :拒絕朋友 3 :成為朋友 4:收藏通知
                    intent.putExtra("type", 2);
                    sendBroadcast(intent);
                }
            });

            //接收成為好友的通知
            client_userHandler.setAddFriendListener(new Client_UserHandler.AddFriendListener() {
                @Override
                public void onAddFriendEvent(Friend friend) {
                    updateFriendStatusInSqlite(friend.getFriendId(), friend.getFriendAvatarUri());//更改朋友狀態
                    new DownloadImage(friend.getFriendAvatarUri(), 2).execute();//下載朋友大頭貼
                    Intent intent = new Intent(BROADCAT_ACTION);
                    intent.putExtra("fromId", friend.getFriendId());
                    intent.putExtra("from", friend.getFriendName());
                    intent.putExtra("content", "開心成為你朋友");

                    intent.putExtra("type", 3);
                    sendBroadcast(intent);
                }
            });

            //接收有人收藏你內容的資訊
            client_messageHandler.setReceiveCollectNotificationListener(new Client_MessageHandler.receiveCollectNotificationListener() {
                @Override
                public void onReceiveCollectNotificationEvent(Message_entity message) {
                    Intent intent = new Intent(BROADCAT_ACTION);
                    intent.putExtra("fromId", message.getToId());
                    intent.putExtra("from", "收藏度上升");
                    intent.putExtra("content", "收藏度上升");

                    //type 0 : 訊息通知 1 : 朋友邀請通知 2 :拒絕朋友 3 :成為朋友 4:收藏通知
                    intent.putExtra("type", 4);
                    sendBroadcast(intent);
                }
            });

            //獲取所有好友資料
            Client_UserHandler clientUserHandler = new Client_UserHandler();
            clientUserHandler.setFriendListListener(new Client_UserHandler.FriendListListener() {
                @Override
                public void onFriendListEvent(Friend friend) {
                    clearFriendInSqlite();//清空朋友列表
                    deleteAllFriendAvatar(friend);//刪除朋友大頭貼列表
                    saveAllFriendInSqlite(friend);//儲存所有朋友資料
                    for(int i = 0 ; i < friend.getFriendAvatarUriArray().length ; i++){
                        System.out.println("friendAvatarUri " + friend.getFriendAvatarUriArray()[i]);
                        if(!friend.getFriendAvatarUriArray()[i].isEmpty()){
                            new DownloadImage(friend.getFriendAvatarUriArray()[i], 2).execute();//從遠端下載朋友大頭貼
                        }
                    }
                }
            });

            //獲取所有邀請我為好友的資料
            clientUserHandler.setCheckNewFriendListListener(new Client_UserHandler.CheckNewFriendListListener() {
                @Override
                public void onCheckNewFriendListEvent(Friend friend) {
                    saveAllFriendInvite(friend);//獲取所有朋友邀請
                }
            });

        }
         return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy(){
        chatClient.close();
        unregisterReceiver(chatBroadcastReceiver);
        SharePreferenceManager sharePreferenceManager = new SharePreferenceManager(this);
        System.out.println("destroy " + sharePreferenceManager.getRestart());
        if(sharePreferenceManager.getRestart()) {
            Intent broadcastIntent = new Intent("com.netty_chatsystem.ActivityRecognition.RestartSensor");
            sendBroadcast(broadcastIntent);
            System.out.println("restart");
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent){
        return chatBinder;
    }

    public class ChatBinder extends Binder {
        public void startConnection(){
        }
    }

    //可建立sqlite的資料表
    private void createTable(){
        MyDBHelper dbHelper = new MyDBHelper(this, "Chat.db", null, 1);
        SQLiteDatabase db =dbHelper.getWritableDatabase();
        //db.execSQL("DROP TABLE IF EXISTS " + "MessageOrder");
        //db.execSQL("DROP TABLE IF EXISTS " + "User");
        //db.execSQL("DROP TABLE IF EXISTS " + "Sticker");
        //db.execSQL("DROP TABLE IF EXISTS " + "Message");
        //db.execSQL("DROP TABLE IF EXISTS " + "Collect");
        //db.execSQL("DROP TABLE IF EXISTS " + "Friend");
        db.execSQL("create table IF NOT EXISTS Message(id INTEGER PRIMARY KEY AUTOINCREMENT , from_id varchar(50) , to_id varchar(50) , content varchar(500) , type varchar(10), effecttype varchar(10) default 0, " +
                "createtime INTEGER, think varchar(255), read varchar(10) DEFAULT 0 )");
        //db.execSQL("create table IF NOT EXISTS User(id INTEGER , username varchar(50), name varchar(100) , profile varchar(500))");
        db.execSQL("create table IF NOT EXISTS Friend(id varchar(100)  , " + "friendusername varchar(50) , friendname varchar(50),  friendAvatarUri varchar(50),  status varchar(10) , viewer varchar(10) , favorite varchar(10))");
        db.execSQL("create table IF NOT EXISTS MessageOrder(id INTEGER PRIMARY KEY AUTOINCREMENT ," + "friendid varchar(50) , content varchar(100), createtime varchar(50))");
        db.execSQL("create table IF NOT EXISTS Sticker(id INTEGER PRIMARY KEY AUTOINCREMENT, " + " content varchar(100), isdelete INTEGER DEFAULT 0)");
        db.execSQL("create table IF NOT EXISTS Collect(id varchar(255), " + "content varchar(100), isdelete INTEGER DEFAULT 0)");

        db.close();
    }

    //清除所有sqlite
    public void clearAllSqlite(){
        MyDBHelper dbHelper = new MyDBHelper(this, "Chat.db", null, 1);
        SQLiteDatabase db =dbHelper.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + "MessageOrder");
        db.execSQL("DROP TABLE IF EXISTS " + "User");
        db.execSQL("DROP TABLE IF EXISTS " + "Sticker");
        db.execSQL("DROP TABLE IF EXISTS " + "Message");
        db.execSQL("DROP TABLE IF EXISTS " + "Collect");
        db.execSQL("DROP TABLE IF EXISTS " + "Friend");
        db.close();
    }

    //清除資料夾
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }


    private void saveSqliteHistory(String messageText , String fromId , String toId , String type, int effectType, long createTime){
        //創建MyDBHelper對象
        MyDBHelper dbHelper = new MyDBHelper(this, "Chat.db", null, 1);
        //得到一個可讀的SQLiteDatabase對象
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //生成ContentValues​​對象 //key:列名，value:想插入的值
        ContentValues cv = new ContentValues();

        //往ContentValues​​對象存放數據，鍵-值對模式
        cv.put("from_id", fromId);
        cv.put("to_id", toId);

        cv.put("content", messageText);
        cv.put("type", type);
        cv.put("effecttype",effectType);
        cv.put("createtime", createTime);


        //調用insert方法，將數據插入數據庫
        db.insert("Message", null, cv);
        //關閉數據庫
        db.close();
    }

    private void saveSqliteFileHistory(String messageText , String fromId , String toId , String type, int effectType, long createTime, String think){
        //創建MyDBHelper對象
        MyDBHelper dbHelper = new MyDBHelper(this, "Chat.db", null, 1);
        //得到一個可讀的SQLiteDatabase對象
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //生成ContentValues​​對象 //key:列名，value:想插入的值
        ContentValues cv = new ContentValues();

        //往ContentValues​​對象存放數據，鍵-值對模式
        cv.put("from_id", fromId);
        cv.put("to_id", toId);

        cv.put("content", messageText);
        cv.put("type", type);
        cv.put("effecttype",effectType);
        cv.put("createtime", createTime);
        cv.put("think", think);


        //調用insert方法，將數據插入數據庫
        db.insert("Message", null, cv);
        //關閉數據庫
        db.close();

    }

    private void saveNewFriend(Friend friend){
        //創建MyDBHelper對象
        MyDBHelper dbHelper = new MyDBHelper(this, "Chat.db", null, 1);
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

    private void updateFriendStatusInSqlite(String friendId, String friendAvatarUri){
        //創建MyDBHelper對象
        MyDBHelper dbHelper = new MyDBHelper(this, "Chat.db", null, 1);
        //得到一個可讀的SQLiteDatabase對象
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //生成ContentValues​​對象 //key:列名，value:想插入的值
        ContentValues cv = new ContentValues();
        cv.put("status", 1);
        cv.put("friendAvatarUri", friendAvatarUri);
        db.update("Friend", cv, "id" + "=\"" + friendId + "\"", null);
        db.close();
    }

    private void deleteFriendInSqlite(String friendId){
        //創建MyDBHelper對象
        MyDBHelper dbHelper = new MyDBHelper(this, "Chat.db", null, 1);
        //得到一個可讀的SQLiteDatabase對象
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        db.delete("Friend", "id" + "=\"" + friendId + "\"", null);

        db.close();
    }

    //刪除所有朋友大頭貼
    private void deleteAllFriendAvatar(Friend friend){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        for (int i=0; i < friend.getFriendAvatarUriArray().length; i++) {
            if(friend.getFriendAvatarUriArray()[i].length() > 0) {
                File file = new File(directory.getAbsolutePath(), friend.getFriendAvatarUriArray()[i]);
                Boolean isDelete = file.delete();
            }
        }
    }


    //更新messagelist_fragment的狀態
    public void updateMessageList(RowItem rowItem){
        //以下為從sqlite載回聊天紀錄
        MyDBHelper dbHelper =  new  MyDBHelper(this, "Chat.db", null , 1 );
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

        cursor.close();
        db.close();
    }

    //儲存MessageList進入sqlite
    private void saveMessageListInSqlite(RowItem rowItem){
        //創建MyDBHelper對象
        MyDBHelper dbHelper = new MyDBHelper(this, "Chat.db", null, 1);
        //得到一個可讀的SQLiteDatabase對象
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //生成ContentValues​​對象 //key:列名，value:想插入的值
        ContentValues cv = new ContentValues();

        //往ContentValues​​對象存放數據，鍵-值對模式
        cv.put("friendid", rowItem.getWhoId());
        cv.put("content", rowItem.getContent());
        cv.put("createTime", rowItem.getCreateTime());


        //調用insert方法，將數據插入數據庫
        db.insert("MessageOrder", null, cv);

        //關閉數據庫
        db.close();
    }

    //更新sqlite裡的messagelist的資料
    private void updateMessageListInSqlite(RowItem rowItem){
        //創建MyDBHelper對象
        MyDBHelper dbHelper = new MyDBHelper(this, "Chat.db", null, 1);
        //得到一個可讀的SQLiteDatabase對象
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //生成ContentValues​​對象 //key:列名，value:想插入的值
        ContentValues cv = new ContentValues();
        cv.put("content", rowItem.getContent());
        cv.put("createtime", rowItem.getCreateTime());
        db.update("MessageOrder", cv, "friendid" + "=\"" + rowItem.getWhoId() + "\"", null);
        db.close();
    }

    //初次登入時獲取離線訊息
    private void loadOfflineMessage(String loginId){
        IMConnection connection = Client_UserHandler.getConnection();
        Message_entity message = new Message_entity();
        message.setId(loginId);
        IMResponse resp = new IMResponse();
        Header header = new Header();
        header.setHandlerId(Handlers.MESSAGE);
        header.setCommandId(Commands.USER_MESSAGE_OFFLINE);
        resp.setHeader(header);
        resp.writeEntity(new MessageDTO(message));
        connection.sendResponse(resp);
    }

    //初次登入時載入所有朋友邀請
    private void loadFriendInvite(String loginId){
        IMConnection connection = Client_UserHandler.getConnection();
        Friend friend = new Friend();
        friend.setId(loginId);
        IMResponse resp = new IMResponse();
        Header header = new Header();
        header.setHandlerId(Handlers.USER);
        header.setCommandId(Commands.FRIEND_NEWFRIENDCHECK_REQUEST);
        resp.setHeader(header);
        resp.writeEntity(new FriendDTO(friend));
        connection.sendResponse(resp);
    }

    //初次登入時載入所有的朋友資料
    private void loadAllFriend(String loginId){
        IMConnection connection = Client_UserHandler.getConnection();
        Friend friend = new Friend();
        friend.setId(loginId);
        IMResponse resp = new IMResponse();
        Header header = new Header();
        header.setHandlerId(Handlers.USER);
        header.setCommandId(Commands.FRIEND_REQUEST);
        resp.setHeader(header);
        resp.writeEntity(new FriendDTO(friend));
        connection.sendResponse(resp);
    }

    //初次登入時載入所有收集資料
    private void loadAllCollect(String loginId){
        try {
            ArrayList<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("collectid",loginId));
            String result = DBConnector.executeQuery("", Config.COLLECT_LOAD_URL, params);
            System.out.println("loadCollect" + result + " " + result.length());
            /*
                      SQL 結果有多筆資料時使用JSONArray
                      只有一筆資料時直接建立JSONObject物件
                      JSONObject jsonData = new JSONObject(result);
                      */
            if(!result.equals("\"\"\n")) {
                JSONArray jsonArray = new JSONArray(result);
                saveAllCollectInSqlite(jsonArray);//存入收集資料進sqlite
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //初次登入時載入所有貼圖
    private void loadAllSticker(String loginId){
        try {
            ArrayList<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("ownerid",loginId));
            String result = DBConnector.executeQuery("", Config.STICKER_LOAD_URL, params);
            /*
                      SQL 結果有多筆資料時使用JSONArray
                      只有一筆資料時直接建立JSONObject物件
                      JSONObject jsonData = new JSONObject(result);
                      */
            if(result !=null) {
                JSONArray jsonArray = new JSONArray(result);
                saveAllStickerInSqlite(jsonArray);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //初次登入時載入大頭貼
    private void loadProfile(String loginId){
        try {
            ArrayList<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("ownerid", loginId));
            String result = DBConnector.executeQuery("", Config.PROFILE_LOAD_URL, params);
            /*
                      SQL 結果有多筆資料時使用JSONArray
                      只有一筆資料時直接建立JSONObject物件
                      JSONObject jsonData = new JSONObject(result);
                      */
            if(result !=null) {
                JSONArray jsonArray = new JSONArray(result);
                for(int i = 0 ; i < jsonArray.length() ; i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    SharePreferenceManager sharePreferenceManager = new SharePreferenceManager(getApplicationContext());
                    sharePreferenceManager.saveProfileName("profile");//將大頭貼名字存到sharepreference中

                    new DownloadImage(jsonObject.getString("profilename"), 1).execute();
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //清除Friend表的所有資料
    private void clearFriendInSqlite(){
        MyDBHelper dbHelper = new MyDBHelper(this, "Chat.db", null, 1);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        db.delete("Friend", null, null);
        db.close();
    }

    //清除Friend表

    //存入朋友進sqlite裡
    public void saveAllFriendInSqlite(Friend friend){
        //創建MyDBHelper對象
        MyDBHelper dbHelper = new MyDBHelper(this, "Chat.db", null, 1);
        //得到一個可讀的SQLiteDatabase對象
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //生成ContentValues​​對象 //key:列名，value:想插入的值
        ContentValues cv = new ContentValues();

        //往ContentValues​​對象存放數據，鍵-值對模式
        for(int i = 0; i < friend.getFriendIdArray().length; i++) {
            cv.put("id", friend.getFriendIdArray()[i]);
            cv.put("friendusername", friend.getFriendArray()[i]);
            cv.put("friendname", friend.getFriendNameArray()[i]);
            cv.put("friendAvatarUri", friend.getFriendAvatarUriArray()[i]);

            //0代表顯示可讀 ，1代表不顯示
            cv.put("viewer", String.valueOf(friend.getViewerArray()[i]));

            cv.put("status", String.valueOf(friend.getStatusArray()[i]));

            //當status:0代表有人邀請我為好友，發出通知
            /*if(friend.getStatusArray()[i] == 0){
                Intent intent = new Intent(BROADCAT_ACTION);
                intent.putExtra("fromId", friend.getFriendIdArray()[i]);
                intent.putExtra("from", friend.getFriendNameArray()[i]);
                intent.putExtra("content", "邀請你成為好友");

                //type 0 : 訊息通知 1 : 朋友邀請通知 2 :拒絕朋友 3 :成為朋友 4:收藏通知
                intent.putExtra("type", 1);
                sendBroadcast(intent);
            }*/

            //0代表普通朋友，1代表最愛
            cv.put("favorite", String.valueOf(friend.getFavoriteArray()[i]));

            //調用insert方法，將數據插入數據庫
            db.insert("Friend", null, cv);
        }
        //關閉數據庫
        db.close();

    }

    private void saveAllFriendInvite(Friend friend){
        //創建MyDBHelper對象
        MyDBHelper dbHelper = new MyDBHelper(this, "Chat.db", null, 1);
        //得到一個可讀的SQLiteDatabase對象
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //生成ContentValues​​對象 //key:列名，value:想插入的值
        ContentValues cv = new ContentValues();

        //往ContentValues​​對象存放數據，鍵-值對模式
        for(int i = 0; i < friend.getFriendIdArray().length; i++) {
            cv.put("id", friend.getFriendIdArray()[i]);
            cv.put("friendusername", friend.getFriendArray()[i]);
            cv.put("friendname", friend.getFriendNameArray()[i]);
            cv.put("friendAvatarUri", friend.getFriendAvatarUriArray()[i]);

            //0代表顯示可讀 ，1代表不顯示
            cv.put("viewer", "0");

            cv.put("status", "0");

            //當status:0代表有人邀請我為好友，發出通知
            if(friend.getStatusArray()[i] == 4) {
                Intent intent = new Intent(BROADCAT_ACTION);
                intent.putExtra("fromId", friend.getFriendIdArray()[i]);
                intent.putExtra("from", friend.getFriendNameArray()[i]);
                intent.putExtra("content", "邀請你成為好友");

                //type 0 : 訊息通知 1 : 朋友邀請通知 2 :拒絕朋友 3 :成為朋友 4:收藏通知
                intent.putExtra("type", 1);
                sendBroadcast(intent);
            }

            //0代表普通朋友，1代表最愛
            cv.put("favorite", "0");

            //調用insert方法，將數據插入數據庫
            db.insert("Friend", null, cv);
        }

        db.close();
    }


    //存入全部收集資料
    public void saveAllCollectInSqlite(JSONArray jsonArray){
        //創建MyDBHelper對象
        MyDBHelper dbHelper = new MyDBHelper(this, "Chat.db", null, 1);
        //得到一個可讀的SQLiteDatabase對象
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //生成ContentValues​​對象 //key:列名，value:想插入的值
        ContentValues cv = new ContentValues();

        try{
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                cv.put("id", jsonData.getString("ownerid"));
                cv.put("content", jsonData.getString("content"));

                //調用insert方法，將數據插入數據庫
                db.insert("Collect", null, cv);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        //關閉數據庫
        db.close();
    }

    //存入全部貼圖進sqlite中
    private void saveAllStickerInSqlite(JSONArray jsonArray){
        //創建MyDBHelper對象
        MyDBHelper dbHelper = new MyDBHelper(this, "Chat.db", null, 1);
        //得到一個可讀的SQLiteDatabase對象
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //生成ContentValues​​對象 //key:列名，value:想插入的值
        ContentValues cv = new ContentValues();

        try{
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                cv.put("name", jsonData.getString("name"));
                cv.put("content", jsonData.getString("content"));
                new DownloadImage(jsonData.getString("content"), 0).execute();//從遠端將所有貼圖載下

                //調用insert方法，將數據插入數據庫
                db.insert("Sticker", null, cv);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        //關閉數據庫
        db.close();
    }

    private void alarmManagerToDeleteMessage(){
        Intent intent = new Intent(this, ChatAutoDeleteMessageCastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);//取消之前的pendingIntent
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 59);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("日期 " + sdf.format(calendar.getTimeInMillis()));
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public void alarmManagerToKeepServiceAlive(){
        Intent intent = new Intent(this, ChatRestartServiceBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 6 * AlarmManager.INTERVAL_HOUR, pendingIntent);
        System.out.println("ChatService alarmServiceAlive");
    }

    /*初次登入時，從遠端下載圖片下來
            type 0 : 貼圖 1 : 大頭貼  2:朋友大頭貼
    */
    private class DownloadImage extends AsyncTask<Void, Void, InputStream> {
        String content;
        int type;
        public DownloadImage(String content, int type){
            this.content = content;
            this.type = type;
        }
        @Override
        protected InputStream doInBackground(Void... params) {
            String url;
            if(content.length() > 0) {
                if (type == 0) {
                    url = Config.SERVER_STICKER_ADDRESS + content + ".jpg";
                } else {
                    url = Config.SERVER_PROFILE_ADDRESS + content + ".jpg";
                }

                try {
                    URLConnection connection = new URL(url).openConnection();
                    connection.setConnectTimeout(1000 * 30);
                    connection.setReadTimeout(1000 * 30);
                    if (type == 1) {
                        saveToInternalStorage((InputStream) connection.getContent());
                    } else if (type == 2) {
                        saveFriendProfileToInternalStorage((InputStream) connection.getContent(), content);
                        System.out.println("saveFriendProfile " + content);
                    }

                    System.out.println("content " + content);
                    return (InputStream) connection.getContent();

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }else{
                return  null;
            }
        }
        @Override
        protected void onPostExecute(InputStream inputStream) {
            super.onPostExecute(inputStream);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    private void saveToInternalStorage(InputStream inputStream){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        File file = new File(directory, "profile");

        OutputStream out = null;

        try {
            out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=inputStream.read(buf))>0){
                out.write(buf,0,len);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            // Ensure that the InputStreams are closed even if there's an exception.
            try {
                if ( out != null ) {
                    out.close();
                }

                // If you want to close the "in" InputStream yourself then remove this
                // from here but ensure that you close it yourself eventually.
                inputStream.close();
            }
            catch ( IOException e ) {
                e.printStackTrace();
            }
        }
    }

    public void saveFriendProfileToInternalStorage(InputStream inputStream , String content){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        File file = new File(directory, content);

        OutputStream out = null;

        try {
            out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=inputStream.read(buf))>0){
                out.write(buf,0,len);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            // Ensure that the InputStreams are closed even if there's an exception.
            try {
                if ( out != null ) {
                    out.close();
                }

                // If you want to close the "in" InputStream yourself then remove this
                // from here but ensure that you close it yourself eventually.
                inputStream.close();
            }
            catch ( IOException e ) {
                e.printStackTrace();
            }
        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    //更新sqlite裡所有已讀狀態
    private void updateReadInSqlite(){
        //創建MyDBHelper對象
        MyDBHelper dbHelper = new MyDBHelper(this, "Chat.db", null, 1);
        //得到一個可讀的SQLiteDatabase對象
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //生成ContentValues​​對象 //key:列名，value:想插入的值
        ContentValues cv = new ContentValues();
        cv.put("read", "1");
        db.update("Message", cv, "read" + "= 0", null);
        db.close();
    }

    //註冊會員資料進去php server
    private  class registerUserDataInPhpServer extends AsyncTask<Void, Integer, String>{
        String loginId;
        String username;
        String nickName;
        String profileName;

        public registerUserDataInPhpServer(String loginId, String username, String nickName, String profileName){
            this.loginId = loginId;
            this.username = username;
            this.nickName = nickName;
            this.profileName = profileName;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected void onProgressUpdate(Integer... progress) {
        }
        @Override
        protected String doInBackground(Void... params) {
            String responseString = null;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Config.USERDATA_SAVE_URL);
            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(new AndroidMultiPartEntity.ProgressListener() {
                    @Override
                    public void transferred(long num) {
                    }
                });
                // Extra parameters if you want to pass to server
                entity.addPart("id", new StringBody(loginId));
                entity.addPart("username", new StringBody(username));
                entity.addPart("nickName", new StringBody(nickName,  Charset.forName("UTF-8")));
                entity.addPart("profileName", new StringBody(""));

                httppost.setEntity(entity);
                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: " + statusCode;
                }
            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            //Log.e(TAG, "Response from server: " + result);
            //Toast.makeText(getActivity() , result , Toast.LENGTH_SHORT).show();
            // showing the server response in an alert dialog
            //showAlert(result);
            super.onPostExecute(result);
        }
    }

    //自動從新連回service
    @Override
    public void onTaskRemoved(Intent rootIntent){
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);
        System.out.println("onTaskRemove");

        /*chatClient.close();
        unregisterReceiver(chatBroadcastReceiver);
        SharePreferenceManager sharePreferenceManager = new SharePreferenceManager(this);
        System.out.println("destroy " + sharePreferenceManager.getRestart());
        if(sharePreferenceManager.getRestart()) {
            Intent broadcastIntent = new Intent("com.netty_chatsystem.ActivityRecognition.RestartSensor");
            sendBroadcast(broadcastIntent);
            System.out.println("restart");
        }*/

        /*Intent broadcastIntent = new Intent("com.netty_chatsystem.ActivityRecognition.RestartService");
        sendBroadcast(broadcastIntent);
        System.out.println("restart");*/

        super.onTaskRemoved(rootIntent);
    }


}
