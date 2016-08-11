package com.example.user.netty_chatsystem.Chat_Service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.IBinder;

import com.example.user.netty_chatsystem.Chat_Client.ChatClient;
import com.example.user.netty_chatsystem.Chat_Client.handler.Client_MessageHandler;
import com.example.user.netty_chatsystem.Chat_Sqlite_ChatHistory.MyDBHelper;
import com.example.user.netty_chatsystem.Chat_biz.entity.Message_entity;

/**
 * Created by user on 2016/7/8.
 */
public class ChatService extends Service {
    ChatClient chatClient = new ChatClient();
    String username , password;
    ChatBinder chatBinder = new ChatBinder();

    //發送廣播事件用的Action
    public static final String BROADCAT_ACTION = "com.netty_chatsystem.ChatBroadcast";

    //建立接受廣播接收元件
    ChatBroadcastReceiver chatBroadcastReceiver = new ChatBroadcastReceiver();

    //宣告client_MessageHandler用來獲取message
    Client_MessageHandler client_messageHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("onCreate");

    }

    @Override
    public int onStartCommand(Intent intent , int flags , int startId){
        System.out.println("onStartCommand");
        username =intent.getStringExtra("username");
        password = intent.getStringExtra("password");

        client_messageHandler = new Client_MessageHandler();
        client_messageHandler.setListener(new Client_MessageHandler.Listener() {
            @Override
            public void onInterestingEvent(Message_entity message) {
                if (username.equals(message.getTo())) {
                    saveSqliteHistory(message.getMessage(),message.getFrom(),message.getTo());
                    registerReceiver(chatBroadcastReceiver, new IntentFilter(BROADCAT_ACTION));
                    Intent intent = new Intent(BROADCAT_ACTION);
                    intent.putExtra("from" , message.getFrom());
                    intent.putExtra("content" , message.getMessage());
                    sendBroadcast(intent);
                }
            }
        });

        try {
            chatClient.run(username, password);
        }catch(Exception e){
            e.printStackTrace();
        }

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy(){
        System.out.println("onDestroy");
        chatClient.close();
        super.onDestroy();

    }

    @Override
    public IBinder onBind(Intent intent){
        System.out.println("onBind");
        username =intent.getStringExtra("username");
        password = intent.getStringExtra("password");

        return chatBinder;
    }

    public class ChatBinder extends Binder {
        public void startConnection(){
        }
    }

    private void saveSqliteHistory(String messageText , String from , String to){
        //創建MyDBHelper對象
        MyDBHelper dbHelper = new MyDBHelper(this, "Chat.db", null, 1);
        //得到一個可讀的SQLiteDatabase對象
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //生成ContentValues​​對象 //key:列名，value:想插入的值
        ContentValues cv = new ContentValues();

        //往ContentValues​​對象存放數據，鍵-值對模式
        cv.put("from_id", from);
        cv.put("to_id" , to);

        cv.put("content", messageText);
        cv.put("type" , "0");


        //調用insert方法，將數據插入數據庫
        db.insert("Message" , null, cv);
        //關閉數據庫
        db.close();

    }
}
