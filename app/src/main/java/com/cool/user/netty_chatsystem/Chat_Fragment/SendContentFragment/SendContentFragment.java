package com.cool.user.netty_chatsystem.Chat_Fragment.SendContentFragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cool.user.netty_chatsystem.Chat_Client.handler.Client_UserHandler;
import com.cool.user.netty_chatsystem.ChatListViewAdapter.RowItem;
import com.cool.user.netty_chatsystem.Chat_MySQL.Config;
import com.cool.user.netty_chatsystem.Chat_Sharepreference.SharePreferenceManager;
import com.cool.user.netty_chatsystem.Chat_Sqlite_ChatHistory.MyDBHelper;
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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by user on 2016/10/30.
 */
public class SendContentFragment extends Fragment {
    ListView sendFriendListView;
    ImageView sendFriendImg;
    TextView sendText;
    LinearLayout sendLayout;

    private ProgressBar progressBar;
    private TextView txtPercentage;
    private long totalSize = 0;
    private String mediaPath;
    private String mediaName;
    private String username;
    private String loginId;
    private String nickName;

    Bitmap rotateBitmap;


    int count = 0;

    private ArrayList<String>friendIdArray = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater ,ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.sendcontent_fragment, container, false);
        sendFriendListView = (ListView)rootView.findViewById(R.id.sendFriendListView);
        sendFriendImg = (ImageView)rootView.findViewById(R.id.sendFriendImg);
        progressBar = (ProgressBar)rootView.findViewById(R.id.progressBar);
        txtPercentage = (TextView)rootView.findViewById(R.id.txtPercentage);
        sendText = (TextView)rootView.findViewById(R.id.sendText);
        sendLayout = (LinearLayout)rootView.findViewById(R.id.sendLayout);
        ImageView goBackWhiteBoardImg = (ImageView)rootView.findViewById(R.id.goBackWhiteBoardImg);


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


        username = user.get(SharePreferenceManager.KEY_NAME);
        loginId = sharePreferenceManager.getLoginId();
        nickName = sharePreferenceManager.getNickName();


        //final List<HashMap<String,Object>>sendFriendList =loadFriendInSqlite();
        byte[] imageBytes = getArguments().getByteArray("bitmapBytes");
        final int effectMessage = getArguments().getInt("effectMessage");
        final String thinking = getArguments().getString("thinking");
        final String previewFilePath = getArguments().getString("previewFilePath");


        final Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        rotateBitmap = rotateBitmap(bitmap, getExifOrientation(previewFilePath));

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), Config.IMAGE_DIRECTORY_NAME);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("sendContentFragment", "Oops! Failed create " + Config.IMAGE_DIRECTORY_NAME + " directory");
            }
        }

        File file;
        file = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + (int)Math.random() * 1000000000 + ".jpg");
        mediaPath = file.getPath();
        mediaName =  "IMG_" + timeStamp + (int)Math.random() * 1000000000;

        try{
            FileOutputStream fos = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            rotateBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);   //Bitmap類別的compress方法產生檔案
            bos.flush();
            bos.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        //SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity(),sendFriendList,R.layout.resource_sendcontent_listview , new String[] {"friendUserName"} , new int [] {R.id.friendNameText});
        final SendCustomAdapter sendCustomAdapter = new SendCustomAdapter(getActivity());
        loadFriendInSqlite(sendCustomAdapter);
        final ArrayList<SendData> friendUserNameArray = new ArrayList<>();

        sendFriendListView.setAdapter(sendCustomAdapter);

        sendFriendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    if (position != 3) {
                        if (friendUserNameArray.contains(sendCustomAdapter.getItem(position))) {
                            ImageView buttonImg = (ImageView) view.findViewById(R.id.buttonImg);
                            if (position == 1) {
                                buttonImg.setImageResource(R.drawable.questionmark_gray);
                            } else if (position == 2) {
                               buttonImg.setImageResource(R.drawable.logo);
                            }/*else if(position == 3){
                                buttonImg.setImageResource(R.drawable.smile_gray);
                            }*/
                            else {
                                buttonImg.setImageResource(R.drawable.circle_gray);
                            }
                            friendUserNameArray.remove(sendCustomAdapter.getItem(position));
                            if(friendUserNameArray.isEmpty()){
                               sendLayout.setVisibility(View.GONE);
                            }
                            count--;
                            sendText.setText("傳送(" + count + ")");
                        } else {
                            ImageView buttonImg = (ImageView) view.findViewById(R.id.buttonImg);
                            if (position == 1) {
                                buttonImg.setImageResource(R.drawable.questionmark_red);
                            } else if (position == 2) {
                                buttonImg.setImageResource(R.drawable.logo_red);
                            }/*else if(position == 3){
                                buttonImg.setImageResource(R.drawable.smile);
                            }*/
                            else {
                                buttonImg.setImageResource(R.drawable.circle_blue);
                            }
                            if(friendUserNameArray.isEmpty()){
                                sendLayout.setVisibility(View.VISIBLE);
                            }
                            friendUserNameArray.add(sendCustomAdapter.getItem(position));
                            count++;
                            sendText.setText("傳送(" + count + ")");
                        }
                    }
                }
            }
        });

        //傳送按鈕(用TextView 來表示)
        sendText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Client_UserHandler.getConnection() != null) {
                    if (!friendUserNameArray.isEmpty()) {
                        sendText.setText("傳送中");
                        sendText.setBackgroundColor(Color.RED);
                        sendText.setEnabled(false);
                        CountDownTimer countDownTimer = new CountDownTimer(1,1) {
                            @Override
                            public void onTick(long millisUntilFinished) {

                            }

                            @Override
                            public void onFinish() {
                                for (int i = 0; i < friendUserNameArray.size(); i++) {
                                    if (friendUserNameArray.get(i).getName().equals("驚喜")) {
                                        new UploadSurpriseToServer(effectMessage, thinking).execute();
                                        System.out.println("驚喜");
                                    }else if(friendUserNameArray.get(i).getName().equals("頭貼")){
                                        //loadProfileAndDeleteFile();
                                        new uploadProfileToServer().execute();//更改mysql的userdata
                                        updateProfileInSharePreference();//更改在sharePreference裡的資料
                                        updateUserAvatarDataOnServer();//更改netty伺服器的useravatardata
                                        saveToInternalStorage(rotateBitmap);
                                        System.out.println("頭貼");
                                    }
                            /*else if (friendUserNameArray.get(i).getName().equals("貼圖本(stickers)")) {
                                new UploadStickerToServer().execute();//上傳貼圖到遠端伺服器
                                 saveStickerInSqlite();//儲存貼圖在本地端
                            }*/                    else {
                                        System.out.println("傳送圖片");
                                        sendPicture(friendUserNameArray.get(i).getId(), friendUserNameArray.get(i).getUsername(), friendUserNameArray.get(i).getName(), rotateBitmap, effectMessage, thinking);
                                    }
                                }

                                getActivity().getSupportFragmentManager().popBackStack("backFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            }
                        };
                        countDownTimer.start();
                    } else {
                        Toast.makeText(getActivity(), "你沒有選擇與誰分享你的經典之作", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getActivity(), "無法分享你的經典之作，請確認連線狀態", Toast.LENGTH_SHORT).show();
                }
            }
        });

        goBackWhiteBoardImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0){
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });

        return rootView;
    }

    //儲存大頭貼進去internal storage
    private String saveToInternalStorage(Bitmap bitmap){
        ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        final File mypath = new File(directory, "profile");


        FileOutputStream fos = null;
        try{
            fos = new FileOutputStream(mypath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            try {
                System.out.println("fos close");
                fos.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        return directory.getAbsolutePath();
    }



    //載入在sqlite裡朋友名單
    private void loadFriendInSqlite(SendCustomAdapter sendCustomAdapter){
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
        Cursor cursor = db.query("Friend", new String[]{"id", "friendusername", "friendname"}, "status=?", new String[]{"1"}, null, null, null);
        sendCustomAdapter.addHeaderItem(new SendData("個人"));
        sendCustomAdapter.addItem(new SendData("驚喜"));
        sendCustomAdapter.addItem(new SendData("頭貼"));
        //sendCustomAdapter.addItem(new SendData("貼圖本(stickers)"));
        sendCustomAdapter.addHeaderItem(new SendData("朋友"));


        while (cursor.moveToNext()){
            HashMap<String, Object> map = new HashMap<String, Object>();
            String sqliteFriendId = cursor.getString(cursor.getColumnIndex("id"));
            String sqliteFriendUsername = cursor.getString(cursor.getColumnIndex("friendusername"));
            String sqliteFriendName = cursor.getString(cursor.getColumnIndex("friendname"));
            SendData sendData = new SendData();
            sendData.setId(sqliteFriendId);
            sendData.setUsername(sqliteFriendUsername);
            sendData.setName(sqliteFriendName);
            sendCustomAdapter.addItem(sendData);
            friendIdArray.add(sqliteFriendId);
        }

        sendCustomAdapter.setSelectArrayLength(sendCustomAdapter.getCount());

        cursor.close();
        //關閉數據庫
        db.close();
    }

    /*傳送圖片給好友
           effectMessage  0:炸彈訊息   1:愛心特效  2:泡泡特效
    */
    private void sendPicture(String friendId, String friendUserName , String name, Bitmap bitmap, int effectMesssage, String think){
        long createTime = System.currentTimeMillis();
        int sumCountpackage;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bytes = stream.toByteArray();

        if ((bytes.length % 1024 == 0))
            sumCountpackage = bytes.length / 1024;
        else
            sumCountpackage = (bytes.length / 1024) + 1;

        Log.i("TAG", "文件總長度:" + bytes.length);
        final ServerFile serverFile = new ServerFile();
        //serverFile.setSumCountPackage(sumCountpackage);
        serverFile.setCountPackage(1);
        serverFile.setBytes(bytes);
        serverFile.setId(loginId);
        serverFile.setSendId(username);
        serverFile.setSendNickName(nickName);
        serverFile.setToId(friendId);
        serverFile.setReceiveId(friendUserName);
        serverFile.setReceiveNickName(name);
        serverFile.setFileName(Build.MANUFACTURER + "-" + UUID.randomUUID() + ".jpg");
        serverFile.setEffectMessage(effectMesssage);
        serverFile.setThink(think);
        serverFile.setSendTime(createTime);
        IMConnection connection = Client_UserHandler.getConnection();
        IMResponse resp = new IMResponse();
        Header header = new Header();
        header.setHandlerId(Handlers.MESSAGE);
        header.setCommandId(Commands.USER_FILE_REQUEST);
        resp.setHeader(header);
        resp.writeEntity(new FileDTO(serverFile));
        connection.sendResponse(resp);
        System.out.println("文件已經讀取完畢");

        //Find the dir to save cached images**************************************************************************
        /*File cacheDir;
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            //Creates a new File instance from a parent abstract pathname and a child pathname string.
            cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),"TTImages_cache");
        else
            cacheDir= getActivity().getCacheDir();
        if(!cacheDir.exists())
            cacheDir.mkdirs();

        cacheDir = new File(cacheDir , String.valueOf(bitmap.toString().hashCode()));*/

        ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
        File directory = cw.getDir("chatDir", Context.MODE_PRIVATE);

        File cacheDir = new File(directory, String.valueOf(bitmap.toString().hashCode()));

        saveSqliteHistory(cacheDir.getAbsolutePath(), "1", friendId, think, effectMesssage, createTime);

        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(cacheDir, "rw");
            randomAccessFile.write(bytes);
        }catch(Exception e){
            e.printStackTrace();
        }
        //******************************************************************************************

        //下面為要存入messagelist的sqlite的資料(更改messagelist的順序)
        RowItem rowItem = new RowItem(friendId, friendUserName);
        rowItem.setContent("你傳出驚喜");
        rowItem.setCreateTime(System.currentTimeMillis());
        updateMessageList(rowItem) ;


    }

    //將圖片路徑存進sqlite裡
    private void saveSqliteHistory(String messageText, String type , String friendId, String think, int effectType, long createTime){
        //創建MyDBHelper對象
        MyDBHelper dbHelper = new MyDBHelper(getActivity(), "Chat.db", null, 1);
        //得到一個可讀的SQLiteDatabase對象
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //生成ContentValues​​對象 //key:列名，value:想插入的值
        ContentValues cv = new ContentValues();

        //往ContentValues​​對象存放數據，鍵-值對模式
        cv.put("from_id", loginId);
        cv.put("to_id", friendId);
        cv.put("content", messageText);
        cv.put("type", type);
        cv.put("think", think);
        cv.put("effecttype",effectType);
        cv.put("createtime", createTime);

        //調用insert方法，將數據插入數據庫
        db.insert("Message", null, cv);
        //關閉數據庫
        db.close();
    }

    public void updateMessageList(RowItem rowItem){
        //以下為從sqlite載回聊天紀錄
        MyDBHelper dbHelper =  new  MyDBHelper(getActivity() , "Chat.db" , null , 1 );
        //得到一個可寫的數據庫
        SQLiteDatabase db =dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from MessageOrder where friendid=?", new String[]{rowItem.getWhoId()});
        if(cursor.getCount() > 0){
            updateMessageListInSqlite(rowItem);
        }else{
            saveMessageListInSqlite(rowItem);
        }
        db.close();
        cursor.close();
    }

    //更新sqlite裡的messagelist的資料
    private void updateMessageListInSqlite(RowItem rowItem){
        //創建MyDBHelper對象
        MyDBHelper dbHelper = new MyDBHelper(getActivity() , "Chat.db", null, 1);
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


    //儲存自製貼圖路徑進去sqlite
    private void saveStickerInSqlite(){
        //創建MyDBHelper對象
        MyDBHelper dbHelper = new MyDBHelper(getActivity(), "Chat.db", null, 1);
        //得到一個可讀的SQLiteDatabase對象
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //生成ContentValues​​對象 //key:列名，value:想插入的值
        ContentValues cv = new ContentValues();

        cv.put("content", mediaName);
        cv.put("isdelete",0);

        //調用insert方法，將數據插入數據庫
        db.insert("Sticker", null, cv);
        //關閉數據庫
        db.close();
    }


    private void loadProfileAndDeleteFile(){
        ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        SharePreferenceManager sharePreferenceManager = new SharePreferenceManager(getActivity());
        String profileName = sharePreferenceManager.getProfileName();
        final File file = new File(directory.getAbsolutePath(), profileName);
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                String filePath = file.getAbsolutePath();
                file.delete();
                MediaScannerConnection.scanFile(getActivity(),
                        new String[]{filePath}, null, null);
                return null;
            }
        }.execute();

    }

    //更改在sharePreference裡的profile資料
    private void updateProfileInSharePreference(){
        SharePreferenceManager sharePreferenceManager = new SharePreferenceManager(getActivity());
        sharePreferenceManager.saveProfileName("profile");
    }


    //uploading the profile to server
    private  class uploadProfileToServer extends AsyncTask<Void, Integer, String>{
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            progressBar.setProgress(0);
            super.onPreExecute();
        }
        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            progressBar.setVisibility(View.VISIBLE);
            // updating progress bar value
            progressBar.setProgress(progress[0]);
            // updating percentage value
            txtPercentage.setText(String.valueOf(progress[0]) + "%");
        }
        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }
        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Config.PROFILE_UPLOAD_URL);
            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(new AndroidMultiPartEntity.ProgressListener() {
                    @Override
                    public void transferred(long num) {
                        publishProgress((int) ((num / (float) totalSize) * 100));
                    }
                });

                File sourceFile = new File(mediaPath);
                // Adding file data to http body
                entity.addPart("image", new FileBody(sourceFile));
                // Extra parameters if you want to pass to server
                entity.addPart("id", new StringBody(loginId));
                entity.addPart("username", new StringBody(username));
                entity.addPart("nickName", new StringBody(nickName, Charset.forName("UTF-8")));
                entity.addPart("profileName", new StringBody(mediaName));

                totalSize = entity.getContentLength();
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

    private void updateUserAvatarDataOnServer(){
        User user = new User();
        user.setId(loginId);
        user.setAvatarUrl(mediaName);

        IMResponse resp = new IMResponse();
        Header header = new Header();
        header.setHandlerId(Handlers.USER);
        header.setCommandId(Commands.USER_AVATAR_REQUEST);
        resp.setHeader(header);
        resp.writeEntity(new UserDTO(user));
        Client_UserHandler.getConnection().sendResponse(resp);
    }

    //旋轉bitmap圖像
    private Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }

        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        } catch (OutOfMemoryError ignore) {
            return null;
        }
    }

    private int getExifOrientation(String url) {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(url);
        } catch (IOException ignore) {
        }
        return exif == null ? ExifInterface.ORIENTATION_UNDEFINED :
                exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
    }



    /**
     * Uploading the file to server
     * type  0 : 到世界去 ， 1 : 到sticker去
     * */
    private class UploadSurpriseToServer extends AsyncTask<Void, Integer, String> {
        int effectMessage;
        String thinking;

        public UploadSurpriseToServer(int effectMessage, String thinking){
            this.effectMessage = effectMessage;
            this.thinking = thinking;
        }
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            progressBar.setProgress(0);
            super.onPreExecute();
        }
        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            progressBar.setVisibility(View.VISIBLE);
            // updating progress bar value
            progressBar.setProgress(progress[0]);
            // updating percentage value
            txtPercentage.setText(String.valueOf(progress[0]) + "%");
        }
        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }
        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost;
            httpPost = new HttpPost(Config.FILE_UPLOAD_URL);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(new AndroidMultiPartEntity.ProgressListener() {
                    @Override
                    public void transferred(long num) {
                        publishProgress((int) ((num / (float) totalSize) * 100));
                    }
                });

                File sourceFile = new File(mediaPath);
                // Adding file data to http body
                entity.addPart("image", new FileBody(sourceFile));
                // Extra parameters if you want to pass to server
                entity.addPart("ownerid", new StringBody(loginId));
                entity.addPart("content", new StringBody(mediaName));
                entity.addPart("effect", new StringBody(String.valueOf(effectMessage)));
                entity.addPart("think", new StringBody(thinking, Charset.forName("UTF-8")));

                for(String friendId : friendIdArray){
                    entity.addPart("friendIdArray[]", new StringBody(friendId));
                }


                totalSize = entity.getContentLength();
                httpPost.setEntity(entity);
                // Making server call
                HttpResponse response = httpclient.execute(httpPost);
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
//            Toast.makeText(getActivity() , result , Toast.LENGTH_SHORT).show();
            // showing the server response in an alert dialog
            //showAlert(result);
            super.onPostExecute(result);
        }
    }

    private class UploadStickerToServer extends AsyncTask<Void, Integer, String> {

        public UploadStickerToServer(){
        }
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            progressBar.setProgress(0);
            super.onPreExecute();
        }
        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            progressBar.setVisibility(View.VISIBLE);
            // updating progress bar value
            progressBar.setProgress(progress[0]);
            // updating percentage value
            txtPercentage.setText(String.valueOf(progress[0]) + "%");
        }
        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }
        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost;
            httpPost = new HttpPost(Config.STICKER_SAVE_URL);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(new AndroidMultiPartEntity.ProgressListener() {
                    @Override
                    public void transferred(long num) {
                        publishProgress((int) ((num / (float) totalSize) * 100));
                    }
                });

                File sourceFile = new File(mediaPath);
                // Adding file data to http body
                entity.addPart("image", new FileBody(sourceFile));
                // Extra parameters if you want to pass to server
                entity.addPart("ownerid", new StringBody(loginId));
                entity.addPart("content", new StringBody(mediaName));
                totalSize = entity.getContentLength();
                httpPost.setEntity(entity);
                // Making server call
                HttpResponse response = httpclient.execute(httpPost);
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
//            Toast.makeText(getActivity() , result , Toast.LENGTH_SHORT).show();
            // showing the server response in an alert dialog
            //showAlert(result);
            super.onPostExecute(result);
        }
    }

}
