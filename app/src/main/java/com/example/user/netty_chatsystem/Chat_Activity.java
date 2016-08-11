package com.example.user.netty_chatsystem;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.RandomAccessFile;
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

    // to take a picture
    private static final int CAMERA_PIC_REQUEST = 1111;
    private static final int GALLERY_PIC_REQUEST = 1112;

    //傳輸檔案的bytes陣列
    byte[] bytes;

    private int dataLength = 1024;
    private int sumCountpackage = 0;
    Uri selectedImage;

    public static boolean isRead ;


    @Override
    public void onBackPressed() {
        isRead = false;
        finish();
    }

    @Override
    protected  void onDestroy(){
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_);


        //進入頁面後就判定為已讀
        isRead = true;

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.resource_chat_actionbar);
        sr = new ServerRequest();
        pref = getSharedPreferences("AppPref", MODE_PRIVATE);

        client_messageHandler = new Client_MessageHandler();
        client_messageHandler.setListener(new Client_MessageHandler.Listener() {
            @Override
            public void onInterestingEvent(Message_entity message) {
                if(isRead) {
                    Show_GetMessage(message);
                }
            }
        });

        client_messageHandler.setAlreadyReadListener(new Client_MessageHandler.alreadyReadListener() {
            @Override
            public void onAlreadyReadEvent(Message_entity message) {
                showRead(message);
            }
        });

        client_messageHandler.setOfflineMessageListener(new Client_MessageHandler.offlineMessageListener() {
            @Override
            public void onOfflineInterestingEvent(String[] offlineMessageArray) {
                showOfflineMessage(offlineMessageArray);
            }
        });

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
                    cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),"TTImages_cache");
                else
                    cacheDir= getCacheDir();
                if(!cacheDir.exists())
                    cacheDir.mkdirs();

                //deleteDirectory(cacheDir);

                cacheDir = new File(cacheDir, fileName);


                try {
                    RandomAccessFile randomAccessFile = new RandomAccessFile(cacheDir, "rw");
                    randomAccessFile.seek(countPackage * 1024 - 1024);
                    randomAccessFile.write(bytes);
                }catch(Exception e){
                    e.printStackTrace();
                }

                saveSqliteHistory(cacheDir.getAbsolutePath(),1,"1");

                ChatMessage getmsg = new ChatMessage();
                getmsg.setId(2);
                getmsg.setMe(false);
                getmsg.setMessage("");
                getmsg.setIsEffect(true);
                getmsg.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                Bitmap icon = decodeFile(cacheDir);
                getmsg.setBitmap(icon);

                Bundle bundle = new Bundle();
                bundle.putSerializable("GetMsg", getmsg);
                Message msg = new Message();
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        });
        initControls();
    }

    //清掉資料夾
    public void deleteDirectory( File dir )
    {

        if ( dir.isDirectory() )
        {
            String [] children = dir.list();
            for ( int i = 0 ; i < children.length ; i ++ )
            {
                File child =    new File( dir , children[i] );
                if(child.isDirectory()){
                    deleteDirectory( child );
                    child.delete();
                }else{
                    child.delete();

                }
            }
            dir.delete();
        }
    }

    //decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f){
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            //Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE=70;
            int width_tmp=o.outWidth, height_tmp=o.outHeight;
            int scale=1;
            while(true){
                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                    break;
                width_tmp/=2;
                height_tmp/=2;
                scale*=2;
            }

            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
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

        //createTable();
        //載入sqlite裡的聊天紀錄
        loadSqliteHistory();
        //載入在遠端伺服器的離線紀錄
        loadOfflineMessage();

        //用來判斷是否為別人傳來的訊息，並且傳出已讀的訊息
        if(adapter.getCount() > 0) {
            if (!adapter.getItem(adapter.getCount() - 1).getIsme()) {
                Message_entity message = new Message_entity();
                message.setFrom(friend_id);
                message.setTo(login_id);
                message.setRead(1);

                connection = Client_UserHandler.getConnection();
                IMResponse resp = new IMResponse();
                Header header = new Header();
                header.setHandlerId(Handlers.MESSAGE);
                header.setCommandId(Commands.USER_MESSAGE_ALREADYREAD);
                resp.setHeader(header);
                resp.writeEntity(new MessageDTO(message));
                connection.sendResponse(resp);
            }
        }


        sendmessage_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adapter.getCount() > 0) {
                    adapter.getItem(adapter.getCount() - 1).setIsRead(0);
                }

                connection = Client_UserHandler.getConnection();
                String messageText = message_edit.getText().toString();
                if (TextUtils.isEmpty(messageText)) {
                    return;
                }
                saveSqliteHistory(messageText, 0 , "0");

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
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 2);


                /*ChatMessage getmsg = new ChatMessage();
                getmsg.setId(2);
                getmsg.setMe(true);
                getmsg.setMessage("");
                getmsg.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                Bitmap icon = BitmapFactory.decodeResource(getResources(),
                        R.drawable.blue_concave);
                getmsg.setBitmap(icon);

                Bundle bundle = new Bundle();
                bundle.putSerializable("GetMsg", getmsg);
                Message msg = new Message();
                msg.setData(bundle);
                handler.sendMessage(msg);*/
            }
        });


        //返回鍵按鈕監聽
        back_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRead = false;
                finish();
            }
        });

        messagesContainer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (adapter.getItem(position).getIsEffect()) {
                    Intent it = new Intent();
                    it.setClass(Chat_Activity.this, EffectShowActivity.class);
                    startActivity(it);
                } else {
                    Toast.makeText(Chat_Activity.this, "click list " + position, Toast.LENGTH_SHORT).show();
                }
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

    private void saveSqliteHistory(String messageText , int me , String type){
        //創建MyDBHelper對象
        MyDBHelper dbHelper = new MyDBHelper(Chat_Activity.this, "Chat.db", null, 1);
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
        cv.put("type" , type);

        //調用insert方法，將數據插入數據庫
        db.insert("Message", null, cv);
        //關閉數據庫
        db.close();

    }

    private void loadSqliteHistory(){
        chatHistory = new ArrayList<ChatMessage>();
        adapter = new ChatAdapter(Chat_Activity.this, new ArrayList<ChatMessage>());
        messagesContainer.setAdapter(adapter);

        //以下為從sqlite載回聊天紀錄
        MyDBHelper dbHelper =  new  MyDBHelper(Chat_Activity.this , "Chat.db" , null , 1 );
        //得到一個可寫的數據庫
        SQLiteDatabase db =dbHelper.getReadableDatabase();
        //參數1：表名
        //參數2：要想顯示的列
        //參數3：where子句
        //參數4：where子句對應的條件值
        //參數5：分組方式
        //參數6：having條件
        //參數7：排序方式
        Cursor cursor = db.query("Message" ,  new  String[]{ "id" , "from_id" , "to_id" , "content" , "type" },  "id" ,  null ,  null ,  null ,  null );
        while (cursor.moveToNext()){
            String from_id = cursor.getString(cursor.getColumnIndex( "from_id" ));
            String to_id = cursor.getString(cursor.getColumnIndex("to_id"));
            String content = cursor.getString(cursor.getColumnIndex( "content" ));
            String type = cursor.getString(cursor.getColumnIndex("type"));
            ChatMessage sqliteMsg = new ChatMessage();
            sqliteMsg.setId(2);
            if(type != null) {
                if (type.equals("0")) {
                    if (from_id.equals(login_id) && to_id.equals(friend_id)) {
                        sqliteMsg.setMe(true);
                        sqliteMsg.setMessage(content);
                        displayMessage(sqliteMsg);
                    } else if (from_id.equals(friend_id) && to_id.equals(login_id)) {
                        sqliteMsg.setMe(false);
                        sqliteMsg.setMessage(content);
                        displayMessage(sqliteMsg);
                    }
                }else if(type.equals("1")){
                    if(from_id.equals(login_id) && to_id.equals(friend_id)){
                        sqliteMsg.setMe(true);
                        sqliteMsg.setMessage("");
                        File pictureMessage = new File(content);
                        Bitmap icon = decodeFile(pictureMessage);
                        sqliteMsg.setBitmap(icon);
                        sqliteMsg.setIsEffect(true);
                        displayMessage(sqliteMsg);
                    }else if(from_id.equals(friend_id) && to_id.equals(login_id)){
                        sqliteMsg.setMe(false);
                        sqliteMsg.setMessage("");
                        File pictureMessage = new File(content);
                        Bitmap icon = decodeFile(pictureMessage);
                        sqliteMsg.setBitmap(icon);
                        displayMessage(sqliteMsg);
                    }
                }
            }
        }
        //db =dbHelper.getWritableDatabase();
        //db.execSQL("DROP TABLE IF EXISTS " + "stu_table");

        //關閉數據庫
        db.close();
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

    public void showOfflineMessage(String[] offlineMessageArray){
        for(int i = 0 ; i < offlineMessageArray.length ; i++){
            ChatMessage getmsg = new ChatMessage();
            getmsg.setId(2);
            getmsg.setMe(false);
            getmsg.setMessage(offlineMessageArray[i]);
            getmsg.setDate(DateFormat.getDateTimeInstance().format(new Date()));
            System.out.println("offline :" + offlineMessageArray[i]);
            saveSqliteHistory(offlineMessageArray[i] , 1 , "0");
            Bundle bundle = new Bundle();
            bundle.putSerializable("GetMsg", getmsg);
            Message msg = new Message();
            msg.setData(bundle);
            handler.sendMessage(msg);
        }
    }

    public void Show_GetMessage(Message_entity message){
        if(friend_id.equals(message.getFrom())) {
            //將前一個訊息的已讀符號拿到
            if(adapter.getItem(adapter.getCount()-1).getIsme()){
                adapter.getItem(adapter.getCount()-1).setIsRead(0);
            }
            saveSqliteHistory(message.getMessage(), 1 , "0");
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

            //判斷是否為已讀
            message.setRead(1);
            connection = Client_UserHandler.getConnection();
            IMResponse resp = new IMResponse();
            Header header = new Header();
            header.setHandlerId(Handlers.MESSAGE);
            header.setCommandId(Commands.USER_MESSAGE_ALREADYREAD);
            resp.setHeader(header);
            resp.writeEntity(new MessageDTO(message));
            connection.sendResponse(resp);
        }
    }

    //展現已讀
    public void showRead(Message_entity message){
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putInt("isRead" , message.getRead());
        msg.setData(bundle);
        readHandler.sendMessage(msg);
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

    protected Handler readHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            adapter.getItem(adapter.getCount()-2).setIsRead(0);
            adapter.getItem(adapter.getCount()-1).setIsRead(msg.getData().getInt("isRead"));
            adapter.notifyDataSetChanged();
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

                        //Find the dir to save cached images**************************************************************************
                        File cacheDir;
                        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
                            //Creates a new File instance from a parent abstract pathname and a child pathname string.
                            cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),"TTImages_cache");
                        else
                            cacheDir= getCacheDir();
                        if(!cacheDir.exists())
                            cacheDir.mkdirs();

                        cacheDir = new File(cacheDir , String.valueOf(selectedImage.toString().hashCode()));

                        saveSqliteHistory(cacheDir.getAbsolutePath(),0,"1");

                        try {
                            RandomAccessFile randomAccessFile = new RandomAccessFile(cacheDir, "rw");
                            randomAccessFile.write(bytes);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        //******************************************************************************************

                        ChatMessage getmsg = new ChatMessage();
                        getmsg.setId(2);
                        getmsg.setMe(true);
                        getmsg.setMessage("");
                        getmsg.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                                R.drawable.blue_concave);
                        getmsg.setBitmap(icon);

                        Bundle bundle = new Bundle();
                        bundle.putSerializable("GetMsg", getmsg);
                        Message msg1 = new Message();
                        msg1.setData(bundle);
                        handler.sendMessage(msg1);

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
        } /*if(requestCode == 2){
            Uri selectedImage = data.getData();
            getPath(selectedImage);
            try {
                InputStream is;
                is = this.getContentResolver().openInputStream(selectedImage);
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inJustDecodeBounds = true;
                //BitmapFactory.decodeStream(bis,null,opts);
                BitmapFactory.decodeStream(is, null, opts);

                //The new size we want to scale to
                final int REQUIRED_SIZE = 200;

                //Find the correct scale value. It should be the power of 2.
                int scale = 1;
                while (opts.outWidth / scale / 2 >= REQUIRED_SIZE || opts.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;

                opts.inSampleSize = scale;
                opts.inJustDecodeBounds = false;
                is = null;
                System.gc();
                InputStream is2 = this.getContentResolver().openInputStream(selectedImage);

                Bitmap returnedImage = BitmapFactory.decodeStream(is2, null, opts);
                ChatMessage getmsg = new ChatMessage();
                getmsg.setId(2);
                getmsg.setMe(true);
                getmsg.setMessage("");
                getmsg.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                getmsg.setBitmap(returnedImage);

                Bundle bundle = new Bundle();
                bundle.putSerializable("GetMsg", getmsg);
                Message msg = new Message();
                msg.setData(bundle);
                handler.sendMessage(msg);

            }catch(Exception e){
                e.printStackTrace();
            }
        }*/
    }

    //獲取圖片的路徑
    public String getPath(Uri uri){
        String[] filePathColumn={MediaStore.Images.Media.DATA};

        Cursor cursor=this.getContentResolver().query(uri, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex=cursor.getColumnIndex(filePathColumn[0]);
        return cursor.getString(columnIndex);
    }


}
