package com.example.user.netty_chatsystem;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.user.netty_chatsystem.Chat_Client.handler.Client_MessageHandler;
import com.example.user.netty_chatsystem.Chat_Client.handler.Client_UserHandler;
import com.example.user.netty_chatsystem.Chat_Listview_Message.ChatAdapter;
import com.example.user.netty_chatsystem.Chat_Listview_Message.ChatMessage;
import com.example.user.netty_chatsystem.Chat_Sqlite_ChatHistory.MyDBHelper;
import com.example.user.netty_chatsystem.Chat_biz.entity.Message_entity;
import com.example.user.netty_chatsystem.Chat_biz.entity.file.ServerFile;
import com.example.user.netty_chatsystem.Chat_core.connetion.IMConnection;
import com.example.user.netty_chatsystem.Chat_core.protocol.Commands;
import com.example.user.netty_chatsystem.Chat_core.protocol.Handlers;
import com.example.user.netty_chatsystem.Chat_core.transport.Header;
import com.example.user.netty_chatsystem.Chat_core.transport.IMResponse;
import com.example.user.netty_chatsystem.Chat_mongodb.ServerRequest;
import com.example.user.netty_chatsystem.Chat_server.dto.FileDTO;
import com.example.user.netty_chatsystem.Chat_server.dto.MessageDTO;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.InputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Chat_Activity extends AppCompatActivity  {
    EditText message_edit;
    ImageView sendmessage_imageview , back_imageview , bombSend_imageview;
    IMConnection connection;
    private ListView messagesContainer;
    private ChatAdapter adapter;

    String login_id;
    String friend_id;

    private ArrayList<ChatMessage> chatHistory;
    Client_MessageHandler client_messageHandler;

    List<NameValuePair> params;
    SharedPreferences pref;
    ServerRequest sr;

    //更改在設定中觀察者的眼睛圖示
    public int eyechange_count = 0;

    //更改最愛朋友的按鈕
    private int favorite_count = 0;

    // to take a picture
    private static final int CAMERA_PIC_REQUEST = 1111;
    private static final int GALLERY_PIC_REQUEST = 1112;

    //傳輸檔案的bytes陣列
    byte[] bytes;

    private int dataLength = 1024;
    private int sumCountpackage = 0;
    Uri selectedImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.resource_chat_actionbar);

        sr = new ServerRequest();
        pref = getSharedPreferences("AppPref",MODE_PRIVATE);


        client_messageHandler = new Client_MessageHandler();
        client_messageHandler.setListener(new Client_MessageHandler.Listener() {
            @Override
            public void onInterestingEvent(Message_entity message) {
                Show_GetMessage(message);
            }
        });
        initControls();
    }


    private void initControls() {
        messagesContainer = (ListView) findViewById(R.id.messagesContainer);
        message_edit = (EditText) findViewById(R.id.messageEdit);
        sendmessage_imageview = (ImageView) findViewById(R.id.messageSend_imageview);
        back_imageview = (ImageView)findViewById(R.id.back_imageview);
        bombSend_imageview = (ImageView)findViewById(R.id.bombSend_imageview);

        Bundle bundle = this.getIntent().getExtras();
        friend_id = bundle.getString("friend_id");
        login_id = Client_UserHandler.getLogin_id();

        loadSqliteHistory();
        loadOfflineMessage();

        sendmessage_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String messageText = message_edit.getText().toString();
                if (TextUtils.isEmpty(messageText)) {
                    return;
                }
                //saveSqliteHistory(messageText , 0);

                connection = Client_UserHandler.getConnection();
                Message_entity message = new Message_entity();
                message.setTo(friend_id);
                message.setFrom(login_id);
                message.setMessage(messageText);


                IMResponse resp = new IMResponse();
                Header header = new Header();
                header.setHandlerId(Handlers.MESSAGE);
                header.setCommandId(Commands.USER_MESSAGE_REQUEST);
                resp.setHeader(header);
                resp.writeEntity(new MessageDTO(message));
                connection.sendResponse(resp);

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setId(122);//dummy
                chatMessage.setMessage(messageText);
                chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                chatMessage.setMe(true);

                message_edit.setText("");

                displayMessage(chatMessage);
            }
        });

        bombSend_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);*/
                Intent it = new Intent();
                it.setClass(Chat_Activity.this, BombMessage_video_Activity.class);
                startActivity(it);
            }
        });


        //返回鍵按鈕監聽
        back_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void displayMessage(ChatMessage message) {
        adapter.add(message);
        adapter.notifyDataSetChanged();
        scroll();
    }

    private void scroll() {
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }

    private void saveSqliteHistory(String messageText , int me){
        //創建MyDBHelper對象
        MyDBHelper dbHelper = new MyDBHelper(Chat_Activity.this, "msg_to_123", null, 1);
        //得到一個可讀的SQLiteDatabase對象
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //生成ContentValues​​對象 //key:列名，value:想插入的值
        ContentValues cv = new ContentValues();

        //往ContentValues​​對象存放數據，鍵-值對模式
        if(me == 0) {
            cv.put("from_id", login_id);
            cv.put("to_id", friend_id);
        }else{
            cv.put("from_id", friend_id);
            cv.put("to_id" , login_id);
        }
        cv.put("content", messageText);
        //調用insert方法，將數據插入數據庫
        db.insert("stu_table", null, cv);
        //關閉數據庫
        db.close();
    }



    private void loadSqliteHistory(){

        params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("from",friend_id));
        params.add(new BasicNameValuePair("to",login_id));
        ServerRequest sr = new ServerRequest();
        //JSONObject json = sr.getJSON("http://192.168.43.157/offlinemessage_get",params);

        chatHistory = new ArrayList<ChatMessage>();
        ChatMessage msg = new ChatMessage();
        msg.setId(1);
        msg.setMe(false);
        msg.setMessage("1");
        chatHistory.add(msg);
        adapter = new ChatAdapter(Chat_Activity.this, new ArrayList<ChatMessage>());
        messagesContainer.setAdapter(adapter);
        for(int i=0; i<chatHistory.size(); i++) {
            ChatMessage message = chatHistory.get(i);
            displayMessage(message);
        }

        //以下為從sqlite載回聊天紀錄
        MyDBHelper dbHelper =  new  MyDBHelper(Chat_Activity.this , "msg_to_123" , null , 1 );
        //得到一個可寫的數據庫
        SQLiteDatabase db =dbHelper.getReadableDatabase();
        //參數1：表名
        //參數2：要想顯示的列
        //參數3：where子句
        //參數4：where子句對應的條件值
        //參數5：分組方式
        //參數6：having條件
        //參數7：排序方式
        Cursor cursor = db.query( "stu_table" ,  new  String[]{ "id" , "from_id" , "to_id" , "content" },  "id" ,  null ,  null ,  null ,  null );
        while (cursor.moveToNext()){
            String from_id = cursor.getString(cursor.getColumnIndex( "from_id" ));
            String content = cursor.getString(cursor.getColumnIndex( "content" ));
            ChatMessage sqliteMsg = new ChatMessage();
            sqliteMsg.setId(1);
            if(from_id.equals(login_id)) {
                sqliteMsg.setMe(true);
            }else{
                sqliteMsg.setMe(false);
            }
            sqliteMsg.setMessage(content);
            displayMessage(sqliteMsg);
        }

        //關閉數據庫
        db.close();

        /*if(json!=null){
            try{
                JSONArray jsonArray = json.getJSONArray("response");
                String []jsonstr = new String[jsonArray.length()];
                for(int i = 0 ; i < jsonArray.length() ; i++){
                    jsonstr[i] = jsonArray.getString(i);
                }
                chatHistory = new ArrayList<ChatMessage>();

                for(int i = 0 ; i < jsonstr.length ; i++){
                    ChatMessage msg = new ChatMessage();
                    msg.setId(1);
                    msg.setMe(false);
                    msg.setMessage(jsonstr[i]);
                    chatHistory.add(msg);
                }

                adapter = new ChatAdapter(Chat_Activity.this, new ArrayList<ChatMessage>());
                messagesContainer.setAdapter(adapter);
                for(int i=0; i<chatHistory.size(); i++) {
                    ChatMessage message = chatHistory.get(i);
                    displayMessage(message);
                }

            }catch(JSONException e){
                e.printStackTrace();
            }
        }*/
    }

    private void loadOfflineMessage(){
        connection = Client_UserHandler.getConnection();
        Message_entity message = new Message_entity();
        message.setTo(friend_id);
        message.setFrom(login_id);
        message.setMessage(" ");


        IMResponse resp = new IMResponse();
        Header header = new Header();
        header.setHandlerId(Handlers.MESSAGE);
        header.setCommandId(Commands.USER_MESSAGE_OFFLINE);
        resp.setHeader(header);
        resp.writeEntity(new MessageDTO(message));
        connection.sendResponse(resp);
    }

    public void Show_GetMessage(Message_entity message){
        if(friend_id.equals(message.getFrom())) {
            //saveSqliteHistory(message.getMessage() , 1);
            ChatMessage getmsg = new ChatMessage();
            getmsg.setId(2);
            getmsg.setMe(false);
            getmsg.setMessage(message.getMessage());
            getmsg.setDate(DateFormat.getDateTimeInstance().format(new Date()));
            Bundle bundle = new Bundle();
            bundle.putSerializable("GetMsg", getmsg);
            Message msg = new Message();
            msg.setData(bundle);
            handler.sendMessage(msg);
        }
    }

    //專門處理接收過來的訊息的handler , 並將它放在list view上
    protected Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            // Put code here...

            // Set a switch statement to toggle it on or off.
            ChatMessage getmsg = (ChatMessage)msg.getData().getSerializable("GetMsg");

            displayMessage(getmsg);


        }
    };

    //專門處理送出去與接受過來file的handler
    protected Handler fileHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch(msg.what){
                case 1 :{
                    try {
                        ContentResolver resolver = Chat_Activity.this.getContentResolver();
                        InputStream reader = resolver.openInputStream(selectedImage);
                        byte[] bytes = new byte[reader.available()];
                        System.out.println(reader.available());
                        reader.read(bytes);
                        reader.close();

                        if ((bytes.length % dataLength == 0))
                            sumCountpackage = bytes.length / dataLength;
                        else
                            sumCountpackage = (bytes.length / dataLength) + 1;

                        Log.i("TAG", "文件總長度:" + bytes.length);
                        final ServerFile serverFile = new ServerFile();
                        serverFile.setSumCountPackage(sumCountpackage);
                        serverFile.setCountPackage(1);
                        serverFile.setBytes(bytes);
                        serverFile.setSendId(login_id);
                        serverFile.setReceiveId(friend_id);
                        serverFile.setFileName(Build.MANUFACTURER + "-" + UUID.randomUUID() + ".jpg");
                        connection = Client_UserHandler.getConnection();
                        IMResponse resp = new IMResponse();
                        Header header = new Header();
                        header.setHandlerId(Handlers.MESSAGE);
                        header.setCommandId(Commands.USER_FILE_REQUEST);
                        resp.setHeader(header);
                        resp.writeEntity(new FileDTO(serverFile));
                        connection.sendResponse(resp);
                        System.out.println("文件已經讀取完畢");
                    }catch(Exception e){
                        e.printStackTrace();
                    }

                }
            }
        }
    };




    //處理照片
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // usedView is a bool that checks is a view was destroyed and this was reused.
        // if it wasn't reused, this means we create a new one.
        if (resultCode == RESULT_OK) {
                selectedImage = data.getData();
                fileHandler.obtainMessage(1).sendToTarget();
        }
    }
}
