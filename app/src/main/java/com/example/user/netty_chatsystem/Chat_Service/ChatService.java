package com.example.user.netty_chatsystem.Chat_Service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;

import com.example.user.netty_chatsystem.Chat_Client.ChatClient;
import com.example.user.netty_chatsystem.Chat_Client.handler.Client_MessageHandler;
import com.example.user.netty_chatsystem.Chat_Sqlite_ChatHistory.MyDBHelper;
import com.example.user.netty_chatsystem.Chat_biz.entity.Message_entity;
import com.example.user.netty_chatsystem.Chat_biz.entity.file.ServerFile;
import com.example.user.netty_chatsystem.Chat_server.dto.FileDTO;

import java.io.File;
import java.io.RandomAccessFile;

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
        if(intent.getStringExtra("username") != null || intent.getStringExtra("password") != null) {
            username = intent.getStringExtra("username");
            password = intent.getStringExtra("password");
            try {
                chatClient.run(username, password);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //接收訊息文字
            client_messageHandler = new Client_MessageHandler();
            client_messageHandler.setListener(new Client_MessageHandler.Listener() {
                @Override
                public void onInterestingEvent(Message_entity message) {
                    if (username.equals(message.getTo())) {
                        saveSqliteHistory(message.getMessage(), message.getFrom(), message.getTo(), "0");
                        registerReceiver(chatBroadcastReceiver, new IntentFilter(BROADCAT_ACTION));
                        Intent intent = new Intent(BROADCAT_ACTION);
                        intent.putExtra("from", message.getFrom());
                        intent.putExtra("content", message.getMessage());
                        sendBroadcast(intent);
                    }
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
                    registerReceiver(chatBroadcastReceiver, new IntentFilter(BROADCAT_ACTION));
                    Intent intent = new Intent(BROADCAT_ACTION);
                    intent.putExtra("from", serverFile.getSendId());
                    intent.putExtra("content", "你收到一則驚喜");
                    sendBroadcast(intent);

                }
            });
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

    private void saveSqliteHistory(String messageText , String from , String to , String type){
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
        cv.put("type" , type);


        //調用insert方法，將數據插入數據庫
        db.insert("Message" , null, cv);
        //關閉數據庫
        db.close();

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

        super.onTaskRemoved(rootIntent);
    }
}
