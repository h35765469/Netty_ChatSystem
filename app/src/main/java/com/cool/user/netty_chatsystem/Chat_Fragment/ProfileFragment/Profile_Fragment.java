package com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.cool.user.netty_chatsystem.ChatListViewAdapter.RowItem;
import com.cool.user.netty_chatsystem.Chat_Client.handler.Client_MessageHandler;
import com.cool.user.netty_chatsystem.Chat_Client.handler.Client_UserHandler;
import com.cool.user.netty_chatsystem.Chat_Fragment.BaseFragment;
import com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.AddFriendFragment.AddBySearchFragment;
import com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.CollectFragment.AndroidVersion;
import com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.CollectFragment.CollectFragment;
import com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.MyContetnFragment.MyContentData;
import com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.MyContetnFragment.MyContentFragment;
import com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.NewFriendFragment.NewFriendFragment;
import com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.PersonalSettingFragment.PersonalSettingFragment;
import com.cool.user.netty_chatsystem.Chat_Fragment.SearchFragment;
import com.cool.user.netty_chatsystem.Chat_Fragment.SendContentFragment.AndroidMultiPartEntity;
import com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.CollectFragment.CollectData;
import com.cool.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.Chat_AnimationElement.BangEffect.SmallBang;
import com.cool.user.netty_chatsystem.Chat_MySQL.Config;
import com.cool.user.netty_chatsystem.Chat_MySQL.DBConnector;
import com.cool.user.netty_chatsystem.Chat_Sharepreference.SharePreferenceManager;
import com.cool.user.netty_chatsystem.Chat_Sqlite_ChatHistory.MyDBHelper;
import com.cool.user.netty_chatsystem.Chat_biz.entity.Friend;
import com.cool.user.netty_chatsystem.Chat_biz.entity.Message_entity;
import com.cool.user.netty_chatsystem.Chat_biz.entity.User;
import com.cool.user.netty_chatsystem.Chat_biz.entity.file.ServerFile;
import com.cool.user.netty_chatsystem.Chat_core.connetion.IMConnection;
import com.cool.user.netty_chatsystem.Chat_core.protocol.Commands;
import com.cool.user.netty_chatsystem.Chat_core.protocol.Handlers;
import com.cool.user.netty_chatsystem.Chat_core.transport.Header;
import com.cool.user.netty_chatsystem.Chat_core.transport.IMResponse;
import com.cool.user.netty_chatsystem.Chat_server.dto.FileDTO;
import com.cool.user.netty_chatsystem.Chat_server.dto.UserDTO;
import com.cool.user.netty_chatsystem.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

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
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user on 2016/8/9.
 */
public class Profile_Fragment extends BaseFragment {

    de.hdodenhof.circleimageview.CircleImageView effectProfile_imageview;
    SmallBang smallBang;

    // sharePreferenceManager
    SharePreferenceManager sharePreferenceManager;

    String username;
    String loginId;
    String profileName;

    //發送廣播事件用的Action
    public static final String BROADCAT_ACTION = "com.netty_chatsystem.ChatBroadcast";

    ArrayList<MyContentData> myContentDataArrayList;
    ArrayList<CollectData> collectDataArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,Bundle savedInstanceState) {
        View view = initView(inflater);
        return view;
    }

    @Override
    public  void initData(Bundle savedInstanceState){

    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onPause(){
        super.onPause();
    }



    @Override
    public View initView(LayoutInflater inflater){
        View view = inflater.inflate(R.layout.profile_fragment, null);

        effectProfile_imageview = (de.hdodenhof.circleimageview.CircleImageView)view.findViewById(R.id.effectprofile_imageview);
        final TextView myNameText = (TextView)view.findViewById(R.id.myNameText);
        registerChatListener();


        smallBang = SmallBang.attach2Window(getActivity());

        sharePreferenceManager = new SharePreferenceManager(getActivity().getApplicationContext());
        // get user data from sharePreference
        final HashMap<String, String> user = sharePreferenceManager.getUserDetails();

        // name
        username = user.get(SharePreferenceManager.KEY_NAME);
        //userId
        loginId = sharePreferenceManager.getLoginId();
        //獲取大頭貼名稱
        profileName = sharePreferenceManager.getProfileName();


        final DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.logo)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.logo_red)
                //.cacheInMemory(false)
                //.cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();


        //從網路下載facebook大頭貼
        if(!profileName.isEmpty()) {
            //new DownloadImageTask(effectProfile_imageview).execute(profile);
            ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            SharePreferenceManager sharePreferenceManager = new SharePreferenceManager(getActivity());
            String profileName = sharePreferenceManager.getProfileName();
            File file = new File(directory.getAbsolutePath(), profileName);
            if(file.exists()) {
                com.cool.user.netty_chatsystem.Chat_Fragment.CameraFragment.internal.utils.ImageLoader.Builder builder = new com.cool.user.netty_chatsystem.Chat_Fragment.CameraFragment.internal.utils.ImageLoader.Builder(getActivity());
                builder.load(file.getAbsolutePath()).build().into(effectProfile_imageview);
            }else{
                effectProfile_imageview.setImageResource(R.drawable.logo_red);
            }
        }

        if(!sharePreferenceManager.getNickName().isEmpty()){
            myNameText.setText(sharePreferenceManager.getNickName());
        }else{
            myNameText.setText(loginId);
        }

        myNameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog editname_dialog = new Dialog(getActivity(), R.style.selectorDialog);
                editname_dialog.setContentView(R.layout.resource_profile_editname_dialog);
                editname_dialog.show();
                final BootstrapEditText myNameEdit = (BootstrapEditText)editname_dialog.findViewById(R.id.myNameEdit);
                ImageView editNameYesImg = (ImageView)editname_dialog.findViewById(R.id.editNameYesImg);
                ImageView editNameNoImg = (ImageView)editname_dialog.findViewById(R.id.editNameNoImg);
                editNameYesImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(Client_UserHandler.getConnection() !=null) {
                            if (myNameEdit.getText().length() != 0) {
                                if(myNameEdit.getText().length() < 20) {
                                    editMyNameInNettyServer(myNameEdit.getText().toString());//更改在netty server 的我的名字
                                    new editMyNameInPhpServer(myNameEdit.getText().toString()).execute();//更改在php server的我的名字
                                    sharePreferenceManager.saveNickName(myNameEdit.getText().toString());//儲存
                                    myNameText.setText(myNameEdit.getText().toString());
                                    editname_dialog.dismiss();
                                }else{
                                    Toast.makeText(getActivity(), "名字限制在20字以下", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(getActivity(), "無名之人，無人知曉", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(getActivity(), "你的名字是?，請確認離線狀態", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                editNameNoImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editname_dialog.dismiss();
                    }
                });
            }
        });

        //以下為跳到新的fragment的變數
        final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Bundle bundle = getArguments();////從rootProfileFragment來的為了點擊上方提醒欄後可以直接進入新朋友頁面
        if(bundle !=null){
            if(bundle.getInt("finishOpen") != 1) {
                bundle.putInt("finishOpen", 1);
                NewFriendFragment newFriendFragment = new NewFriendFragment();
                newFriendFragment.setArguments(bundle);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.profileContainer, newFriendFragment);
                fragmentTransaction.commit();
            }
        }


        effectProfile_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentTransaction.addToBackStack("backFragment");
                fragmentTransaction.replace(R.id.profileContainer, new TakeProfileFragment());
                fragmentTransaction.commit();
                /*Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bubble);
                String file = saveToInternalStorage(bitmap);
                Toast.makeText(getActivity(), file, Toast.LENGTH_SHORT).show();*/
            }
        });



        RecyclerView rv = (RecyclerView)view.findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        ProfileRecycleViewAdapter profileRecycleViewAdapter = new ProfileRecycleViewAdapter();
        profileRecycleViewAdapter.setOnItemClickListener(new ProfileRecycleViewAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                switch(position){
                    case 0:{
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.replace(R.id.profileContainer, new MyContentFragment());
                        fragmentTransaction.commit();
                        break;
                    }
                    case 1:{
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.replace(R.id.profileContainer, new CollectFragment());
                        fragmentTransaction.commit();
                        break;
                    }
                    case 2: {
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.replace(R.id.profileContainer, new NewFriendFragment());
                        fragmentTransaction.commit();
                        break;
                    }
                    case 3:{
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.replace(R.id.profileContainer, new AddBySearchFragment());
                        fragmentTransaction.commit();
                        break;
                    }
                    case 4:{
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.replace(R.id.profileContainer, new PersonalSettingFragment());
                        fragmentTransaction.commit();
                        break;
                    }
                }
            }
        });
        rv.setAdapter(profileRecycleViewAdapter);



        return view;
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


    //儲存檔案進去internal storage
    private String saveToInternalStorage(Bitmap bitmap){
        ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        File mypath = new File(directory, "profile.jpg");

        FileOutputStream fos = null;
        try{
            fos = new FileOutputStream(mypath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            try {
                fos.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        return directory.getAbsolutePath();
    }

    //編輯我的名字(在NettyServer)
    private void editMyNameInNettyServer(String myName){
        IMConnection connection = Client_UserHandler.getConnection();
        User user = new User();
        user.setId(loginId);
        user.setNickName(myName);

        IMResponse resp = new IMResponse();
        Header header = new Header();
        header.setHandlerId(Handlers.USER);
        header.setCommandId(Commands.USER_NAME_REQUEST);
        resp.setHeader(header);
        resp.writeEntity(new UserDTO(user));
        connection.sendResponse(resp);
    }

    //編輯我的名字(在phpServer)
    private  class editMyNameInPhpServer extends AsyncTask<Void, Integer, String>{
        String nickName;

        public editMyNameInPhpServer(String nickName){
            this.nickName = nickName;
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
            HttpPost httppost = new HttpPost(Config.NAME_EDIT_URL);
            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(new AndroidMultiPartEntity.ProgressListener() {
                    @Override
                    public void transferred(long num) {
                    }
                });
                // Extra parameters if you want to pass to server
                entity.addPart("id", new StringBody(loginId));
                entity.addPart("nickName", new StringBody(nickName,  Charset.forName("UTF-8")));

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

    //計算要壓縮圖片的size
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight){
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if(height > reqHeight || width > reqWidth){

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;


            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth){
                inSampleSize *= 2;
            }

        }
        return  inSampleSize;
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


    //載入所有收集資料
    private ArrayList<MyContentData> loadAllMyContent(String loginId){
        ArrayList<MyContentData>myContentDataArrayList = new ArrayList<>();
        if(Client_UserHandler.getConnection() != null) {
            try {
                ArrayList<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("ownerid", loginId));
                String result = DBConnector.executeQuery("", Config.MYCONTENT_LOAD_URL, params);
            /*
                      SQL 結果有多筆資料時使用JSONArray
                      只有一筆資料時直接建立JSONObject物件
                      JSONObject jsonData = new JSONObject(result);
                      */

                if (!result.equals("\"\"\n")) {
                    JSONArray jsonArray = new JSONArray(result);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        MyContentData myContentData = new MyContentData();
                        myContentData.setContent(jsonObject.getString("content"));
                        myContentData.setThink(jsonObject.getString("think"));
                        myContentData.setEffect(jsonObject.getInt("effect"));
                        myContentData.setCollectCount(jsonObject.getInt("collect_count"));
                        myContentDataArrayList.add(0, myContentData);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        return  myContentDataArrayList;
    }

    //載入所有未讀者的資料
    private ArrayList<MyContentData>loadAllUnreadData(String loginId){
        ArrayList<MyContentData> unreadDataArrayList = new ArrayList<>();
        if(Client_UserHandler.getConnection() != null) {
            try {
                ArrayList<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("ownerid", loginId));
                String result = DBConnector.executeQuery("", Config.MYCONTENTUNREADCOUNT_LOAD_URL, params);
            /*
                      SQL 結果有多筆資料時使用JSONArray
                      只有一筆資料時直接建立JSONObject物件
                      JSONObject jsonData = new JSONObject(result);
                      */
                if (!result.equals("\"\"\n")) {
                    JSONArray jsonArray = new JSONArray(result);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        MyContentData myContentData = new MyContentData();
                        myContentData.setContent(jsonObject.getString("content"));
                        myContentData.setUnReadCount(jsonObject.getInt("unreadcount"));
                        unreadDataArrayList.add(0, myContentData);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return  unreadDataArrayList;

    }

    //載入所有收集資料
    private ArrayList<CollectData> loadAllCollect(String loginId){
        ArrayList<CollectData> collectDataArrayList = new ArrayList<>();
        try {
            ArrayList<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("collectid",loginId));
            String result = DBConnector.executeQuery("", Config.COLLECT_LOAD_URL, params);

            /*
                      SQL 結果有多筆資料時使用JSONArray
                      只有一筆資料時直接建立JSONObject物件
                      JSONObject jsonData = new JSONObject(result);
                      */
            if(!result.equals("\"\"\n")) {
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    CollectData collectData = new CollectData();
                    collectData.setCollectId(jsonObject.getString("ownerid"));
                    collectData.setCollectUserName(jsonObject.getString("username"));
                    collectData.setCollectNickName(jsonObject.getString("nickname"));
                    collectData.setCollectProfile(jsonObject.getString("profilename"));
                    collectData.setCollectContent(jsonObject.getString("content"));
                    collectData.setCollectThink(jsonObject.getString("think"));
                    collectData.setCollectEffect(jsonObject.getInt("effect"));
                    collectDataArrayList.add(0, collectData);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return collectDataArrayList;
    }

    //初始化androidversion的arraylist
    private ArrayList<AndroidVersion> getAndroidVersionMyContentDataArrayList(ArrayList<MyContentData> myContentDataArrayList){
        ArrayList<AndroidVersion> androidVersionArrayList = new ArrayList<>();
        for(int i = 0 ; i < myContentDataArrayList.size() ; i++){
            AndroidVersion androidVersion = new AndroidVersion();
            androidVersion.setAndroid_image_url(Config.SERVER_ADDRESS + myContentDataArrayList.get(i).getContent() + ".jpg");
            androidVersion.setCollectCount(myContentDataArrayList.get(i).getCollectCount());
            androidVersionArrayList.add(androidVersion);
        }

        return  androidVersionArrayList;
    }


    //初始化androidversion的arraylist
    private ArrayList<AndroidVersion> getAndroidVersionCollectDataArrayList(ArrayList<CollectData> collectDataArrayList){
        ArrayList<AndroidVersion> androidVersionArrayList = new ArrayList<>();
        for(int i = 0 ; i < collectDataArrayList.size() ; i++){
            AndroidVersion androidVersion = new AndroidVersion();
            androidVersion.setAndroid_version_name(collectDataArrayList.get(i).getCollectNickName());
            androidVersion.setAndroid_image_url(Config.SERVER_ADDRESS + collectDataArrayList.get(i).getCollectContent() + ".jpg");
            androidVersionArrayList.add(androidVersion);
        }

        return  androidVersionArrayList;
    }

}
