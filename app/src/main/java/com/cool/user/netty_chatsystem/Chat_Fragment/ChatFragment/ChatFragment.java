package com.cool.user.netty_chatsystem.Chat_Fragment.ChatFragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.os.OperationCanceledException;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cool.user.netty_chatsystem.Chat_Client.handler.Client_MessageHandler;
import com.cool.user.netty_chatsystem.Chat_Client.handler.Client_UserHandler;
import com.cool.user.netty_chatsystem.Chat_Fragment.CameraFragment.OpenCameraFragment;
import com.cool.user.netty_chatsystem.Chat_Fragment.EffectShowFragment.EffectShowFragment;
import com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.StickerFragment.StickersGridAdapter;
import com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.StickerFragment.StickersPagerAdapter;
import com.cool.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.Utils.BitmapUtils;
import com.cool.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.fragment.WhiteBoardFragment;
import com.cool.user.netty_chatsystem.ChatListViewAdapter.RowItem;
import com.cool.user.netty_chatsystem.Chat_Listview_Message.ChatAdapter;
import com.cool.user.netty_chatsystem.Chat_Listview_Message.ChatMessage;
import com.cool.user.netty_chatsystem.Chat_MySQL.Config;
import com.cool.user.netty_chatsystem.Chat_Sharepreference.SharePreferenceManager;
import com.cool.user.netty_chatsystem.Chat_Sqlite_ChatHistory.MyDBHelper;
import com.cool.user.netty_chatsystem.Chat_biz.entity.Message_entity;
import com.cool.user.netty_chatsystem.Chat_biz.entity.file.ServerFile;
import com.cool.user.netty_chatsystem.Chat_core.connetion.IMConnection;
import com.cool.user.netty_chatsystem.Chat_core.protocol.Commands;
import com.cool.user.netty_chatsystem.Chat_core.protocol.Handlers;
import com.cool.user.netty_chatsystem.Chat_core.transport.Header;
import com.cool.user.netty_chatsystem.Chat_core.transport.IMResponse;
import com.cool.user.netty_chatsystem.Chat_mongodb.ServerRequest;
import com.cool.user.netty_chatsystem.Chat_server.dto.FileDTO;
import com.cool.user.netty_chatsystem.Chat_server.dto.MessageDTO;
import com.cool.user.netty_chatsystem.R;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by user on 2016/11/6.
 */
public class ChatFragment extends Fragment implements StickersGridAdapter.KeyClickListener {
    private EditText message_edit;
    private ImageView sendmessage_imageview, bombSend_imageview , stickerListImg;
    private RelativeLayout parentLayout;
    IMConnection connection;
    private ListView messagesContainer;
    private ChatAdapter adapter;


    private String loginId;
    private String username;
    private String nickName;
    private String friendUserName;
    private String friendName;
    private String friendId;
    private int viewer;


    Client_MessageHandler client_messageHandler;

    SharedPreferences pref;
    ServerRequest sr;

    // to take a picture
    private static final int CAMERA_PIC_REQUEST = 1111;
    private static final int GALLERY_PIC_REQUEST = 1112;
    //發送廣播事件用的Action
    public static final String BROADCAT_ACTION = "com.netty_chatsystem.ChatBroadcast";


    public boolean isRead ;
    private int whichFragment;


    //以下為貼圖使用
    private ArrayList<Bitmap>stickers = new ArrayList<>();
    private ArrayList<String>stickerString = new ArrayList<>();
    private View popUpView;
    private PopupWindow popupWindow;
    private LinearLayout stickersCover;
    private boolean isKeyBoardVisible;
    private int keyboardHeight;


    @Override
    public void onStop(){
        System.out.println("onStop");
        super.onStop();
    }

    @Override
    public void onResume(){
        System.out.println("onResume");
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater ,ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.activity_chat_ , container, false);

        SharePreferenceManager sharePreferenceManager = new SharePreferenceManager(getActivity().getApplicationContext());

        HashMap<String, String> user = sharePreferenceManager.getUserDetails();

        username = user.get(SharePreferenceManager.KEY_NAME);
        loginId = sharePreferenceManager.getLoginId();
        nickName = sharePreferenceManager.getNickName();

        Bundle bundle = getArguments();
        friendId = bundle.getString("friendId");
        whichFragment = bundle.getInt("whichFragment");
        loadFriendDataInSqlite();

        TextView friendNameText = (TextView)rootView.findViewById(R.id.friendNameTxt);
        TextView chatBackTxt = (TextView)rootView.findViewById(R.id.chatBackTxt);
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(),"fonts/fontawesome-webfont.ttf");//設定back的按紐
        chatBackTxt.setTypeface(font);
        chatBackTxt.setText("\uf060");
        //返回鍵按鈕監聽
        chatBackTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRead = false;
                BackFragment();
            }
        });
        friendNameText.setText(friendName);



        //當觀察者為0時顯示已讀 1時不顯示已讀
        if(viewer == 0) {
            isRead = true;
        }else{
            isRead = false;
        }


        sr = new ServerRequest();
        pref = getActivity().getSharedPreferences("AppPref", getActivity().MODE_PRIVATE);

        client_messageHandler = new Client_MessageHandler();
        client_messageHandler.setListener(new Client_MessageHandler.Listener() {
            @Override
            public void onInterestingEvent(Message_entity message) {
                Show_GetMessage(message);
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
            public void onOfflineInterestingEvent(String[] offlineMessageArray, long[] createTimeArray) {
                showOfflineMessage(offlineMessageArray, createTimeArray);
            }
        });

        client_messageHandler.setReceiveFileListener(new Client_MessageHandler.receiveFileListener() {
            @Override
            public void onReceiveFileEvent(FileDTO fileDTO) {

                //將前一個訊息的已讀符號拿到
                if(adapter.getCount() > 0) {
                    if (adapter.getItem(adapter.getCount() - 1).getIsme()) {
                        adapter.getItem(adapter.getCount() - 1).setIsRead(0);
                    }
                }

                ServerFile serverFile = fileDTO.getServerFile();
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
                    cacheDir = getActivity().getCacheDir();
                if (!cacheDir.exists())
                    cacheDir.mkdirs();

                //deleteDirectory(cacheDir);

                cacheDir = new File(cacheDir, fileName);*/

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

                ChatMessage getmsg = new ChatMessage();
                getmsg.setId(2);
                getmsg.setMe(false);
                getmsg.setMessage("");
                if(serverFile.getEffectMessage() == -2){
                    getmsg.setIsEffect(false);
                    saveSqliteFile(cacheDir.getAbsolutePath(), 1, "2",0, fileDTO.getServerFile().getSendTime(), fileDTO.getServerFile().getThink());
                    File sticker = new File(cacheDir.getAbsolutePath());
                    Bitmap stickPicture = decodeFile(sticker);
                    getmsg.setBitmap(stickPicture);
                    getmsg.setFilePath(cacheDir.getAbsolutePath());
                    getmsg.setEffectPicture(1);
                }else {
                    getmsg.setIsEffect(true);
                    saveSqliteFile(cacheDir.getAbsolutePath(), 1, "1", serverFile.getEffectMessage(), fileDTO.getServerFile().getSendTime(), fileDTO.getServerFile().getThink());
                    Bitmap icon = BitmapFactory.decodeResource(getResources(),
                            R.drawable.gift);
                    getmsg.setBitmap(icon);
                    getmsg.setFilePath(cacheDir.getAbsolutePath());
                    getmsg.setEffectMessage(serverFile.getEffectMessage());
                }

                getmsg.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                getmsg.setThink(serverFile.getThink());//儲存驚喜想法
                Bundle bundle = new Bundle();
                bundle.putSerializable("GetMsg", getmsg);
                Message msg = new Message();
                msg.setData(bundle);
                handler.sendMessage(msg);

                //下面為要存入messagelist的sqlite的資料
                RowItem rowItem = new RowItem(friendId,friendUserName);
                if (serverFile.getEffectMessage() == -2){
                    rowItem.setContent("貼圖來臨");
                }else {
                    rowItem.setContent("驚喜來臨");
                }
                rowItem.setCreateTime(serverFile.getSendTime());
                updateMessageList(rowItem);
            }
        });

        initControls(rootView);

        return rootView;
    }

    //載入朋友的data從sqlite裡
    public void loadFriendDataInSqlite(){
        //以下為從sqlite載回聊天紀錄
        MyDBHelper dbHelper =  new  MyDBHelper(getActivity() , "Chat.db" , null , 1 );
        //得到一個可寫的數據庫
        SQLiteDatabase db =dbHelper.getReadableDatabase();
        Cursor cursor = db.query("Friend" ,  new  String[]{ "friendusername", "friendname ", "viewer"},  "id=?" , new String[]{friendId} ,  null ,  null ,  null );
        while (cursor.moveToNext()){
            friendUserName = cursor.getString(cursor.getColumnIndex("friendusername"));
            friendName = cursor.getString(cursor.getColumnIndex("friendname"));
            viewer = Integer.parseInt(cursor.getString(cursor.getColumnIndex("viewer")));
        }

        cursor.close();
        db.close();
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



    private void initControls(View rootView) {
        messagesContainer = (ListView) rootView.findViewById(R.id.messagesContainer);
        message_edit = (EditText) rootView.findViewById(R.id.messageEdit);
        sendmessage_imageview = (ImageView) rootView.findViewById(R.id.messageSend_imageview);
        bombSend_imageview = (ImageView)rootView.findViewById(R.id.bombSend_imageview);
        stickerListImg = (ImageView)rootView.findViewById(R.id.stickerListImg);
        parentLayout = (RelativeLayout)rootView.findViewById(R.id.parentLayout);
        stickersCover = (LinearLayout)rootView.findViewById(R.id.footer_for_stickers);//此為收納貼圖的選單
        popUpView = getLayoutInflater(new Bundle()).inflate(R.layout.resource_stickers_popup, null);//此為貼圖選單長甚麼樣

        // Defining default height of keyboard which is equal to 230 dip--
        final float popUpheight = getResources().getDimension(
                R.dimen.keyboard_height);
        changeKeyboardHeight((int) popUpheight);
        //--------------------------------------------------------------------

        //open the stickers-----------------------------------------
        /*readStickers();
        enablePopUpView();
        checkKeyboardHeight(parentLayout);*/
        //----------------------------------------------------------



        //載入sqlite裡的聊天紀錄
        loadSqliteHistory();

        //用來判斷是否為別人傳來的訊息，並且傳出已讀的訊息
        if(isRead) {
            if (adapter.getCount() > 0) {
                if (!adapter.getItem(adapter.getCount() - 1).getIsme()) {
                    Message_entity message = new Message_entity();
                    message.setFrom(friendUserName);
                    message.setTo(username);
                    message.setRead(1);

                    connection = Client_UserHandler.getConnection();
                    if(connection != null) {
                        IMResponse resp = new IMResponse();
                        Header header = new Header();
                        header.setHandlerId(Handlers.MESSAGE);
                        header.setCommandId(Commands.USER_MESSAGE_ALREADYREAD);
                        resp.setHeader(header);
                        resp.writeEntity(new MessageDTO(message));
                        connection.sendResponse(resp);
                    }
                }
            }
        }


        sendmessage_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Client_UserHandler.getConnection() != null) {
                    if (adapter.getCount() > 0) {
                        adapter.getItem(adapter.getCount() - 1).setIsRead(0);
                    }
                    long createTime = System.currentTimeMillis();
                    connection = Client_UserHandler.getConnection();
                    String messageText = message_edit.getText().toString();
                    if (TextUtils.isEmpty(messageText)) {
                        return;
                    }
                    saveSqliteHistory(messageText, 0, "0", 0, createTime);

                    Message_entity message = new Message_entity();
                    message.setToId(friendId);
                    message.setTo(friendUserName);
                    message.setId(loginId);
                    message.setFrom(username);
                    message.setToNickName(friendName);
                    message.setFromNickName(nickName);
                    message.setMessage(messageText);
                    message.setCreateAt(createTime);


                    if (connection != null) {
                        IMResponse resp = new IMResponse();
                        Header header = new Header();
                        header.setHandlerId(Handlers.MESSAGE);
                        header.setCommandId(Commands.USER_MESSAGE_REQUEST);
                        resp.setHeader(header);
                        resp.writeEntity(new MessageDTO(message));
                        connection.sendResponse(resp);
                    }

                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setId(122);//dummy
                    chatMessage.setMessage(messageText);
                    chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                    chatMessage.setMe(true);
                    chatMessage.setBitmap(null);

                    message_edit.setText("");
                    displayMessage(chatMessage);

                    //下面為要存入messagelist的sqlite的資料
                    RowItem rowItem = new RowItem(friendId, friendUserName);
                    rowItem.setContent(messageText);
                    rowItem.setCreateTime(createTime);
                    updateMessageList(rowItem);
                }else{
                    Toast.makeText(getActivity(), "無法送出訊息，請確認連狀態", Toast.LENGTH_SHORT).show();
                }

            }
        });

        bombSend_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adapter.getCount() > 0) {
                    adapter.getItem(adapter.getCount() - 1).setIsRead(0);
                }
                Bundle bundle = new Bundle();
                bundle.putInt("whichFragment", whichFragment);
                /*bundle.putString("login_id", username);
                bundle.putString("friend_id", friendUserName);
                bundle.putInt("whichFragment",whichFragment);
                WhiteBoardFragment whiteBoardFragment = new WhiteBoardFragment();
                whiteBoardFragment.setArguments(bundle);*/
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.addToBackStack("backFragment");
                OpenCameraFragment openCameraFragment = new OpenCameraFragment();
                openCameraFragment.setArguments(bundle);
                fragmentTransaction.replace(whichFragment, openCameraFragment);
                fragmentTransaction.commit();
            }
        });

        stickerListImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!popupWindow.isShowing()){
                    popupWindow.setHeight((int)keyboardHeight);

                    if(isKeyBoardVisible){
                        stickersCover.setVisibility(LinearLayout.GONE);
                    }else{
                        stickersCover.setVisibility(LinearLayout.VISIBLE);
                    }
                    popupWindow.showAtLocation(parentLayout, Gravity.BOTTOM, 0, 0);
                }else{
                    popupWindow.dismiss();
                }
            }
        });

        messagesContainer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (adapter.getItem(position).getIsEffect()) {
                    /*Intent it = new Intent();
                    it.putExtra("filePath",adapter.getItem(position).getFilePath());
                    it.putExtra("effectMessage", adapter.getItem(position).getEffectMessage());
                    it.putExtra("think", adapter.getItem(position).getThink());
                    it.setClass(getActivity(), EffectShowActivity.class);
                    startActivity(it);*/

                    EffectShowFragment effectShowFragment = new EffectShowFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("filePath", adapter.getItem(position).getFilePath());
                    bundle.putInt("effectMessage", adapter.getItem(position).getEffectMessage());
                    bundle.putString("think", adapter.getItem(position).getThink());
                    effectShowFragment.setArguments(bundle);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.allContainer, effectShowFragment);
                    fragmentTransaction.commit();

                }
            }
        });

        messagesContainer.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(!adapter.getItem(position).getIsEffect()){
                    TextView txtMessage = (TextView)view.findViewById(R.id.txtMessage);
                    ClipboardManager clipboardManager = (ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("text", txtMessage.getText().toString());
                    clipboardManager.setPrimaryClip(clipData);
                }
                return false;
            }
        });
    }



    /**
     * change height of emoticons keyboard according to height of actual
     * keyboard
     *
     * @param height
     *            minimum height by which we can make sure actual keyboard is
     *            open or not
     */
    private void changeKeyboardHeight(int height) {

        if (height > 100) {
            keyboardHeight = height;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, keyboardHeight);
            stickersCover.setLayoutParams(params);
        }

    }

    //Reading all stickers in local cache
    private void readStickers(){
        //以下為從sqlite載回聊天紀錄
        MyDBHelper dbHelper =  new  MyDBHelper(getActivity() , "Chat.db" , null , 1 );
        //得到一個可寫的數據庫
        SQLiteDatabase db =dbHelper.getReadableDatabase();
        Cursor cursor = db.query("Sticker" ,  new  String[]{ "id", "content ", "isdelete"}, "isdelete=?", new String[]{"0"} ,  null ,  null ,  null );
        while (cursor.moveToNext()){
            String content = cursor.getString(cursor.getColumnIndex("content"));
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), Config.IMAGE_DIRECTORY_NAME);
            Bitmap bitmap = getSDCardPhoto(mediaStorageDir.getPath() + File.separator + cursor.getString(cursor.getColumnIndex("content")) + ".jpg");
            stickers.add(bitmap);
            stickerString.add(mediaStorageDir.getPath() + File.separator + cursor.getString(cursor.getColumnIndex("content")) + ".jpg");
        }

        cursor.close();
        db.close();
    }


    //Define all component of stickers keyboard
    private void enablePopUpView(){
        ViewPager pager = (ViewPager)popUpView.findViewById(R.id.stickers_pager);
        pager.setOffscreenPageLimit(3);
        StickersPagerAdapter stickersPagerAdapter = new StickersPagerAdapter(getActivity(), stickers,this, stickerString);
        pager.setAdapter(stickersPagerAdapter);

        //Create a pop window for stickers keyboard
        popupWindow = new PopupWindow(popUpView, ViewGroup.LayoutParams.MATCH_PARENT,(int)keyboardHeight, false);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                stickersCover.setVisibility(LinearLayout.GONE);
            }
        });

    }

    /**
     * Checking keyboard height and keyboard visibility
     */
    int previousHeightDiffrence = 0;
    private void checkKeyboardHeight(final View parentLayout) {

        parentLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {

                        Rect r = new Rect();
                        parentLayout.getWindowVisibleDisplayFrame(r);

                        int screenHeight = parentLayout.getRootView()
                                .getHeight();
                        int heightDifference = screenHeight - (r.bottom);

                        if (previousHeightDiffrence - heightDifference > 50) {
                            popupWindow.dismiss();
                        }

                        previousHeightDiffrence = heightDifference;
                        if (heightDifference > 100) {

                            isKeyBoardVisible = true;
                            changeKeyboardHeight(heightDifference);

                        } else {

                            isKeyBoardVisible = false;

                        }

                    }
                });

    }

    //implements 來自在StickersGridAdapter裡KeyClickListener所需要的方法
    //為處理發送自製貼圖使用
    @Override
    public void keyClickedIndex(final Bitmap index){
        if(Client_UserHandler.getConnection()!=null) {
            if (adapter.getCount() > 0) {
                adapter.getItem(adapter.getCount() - 1).setIsRead(0);
            }
            long createTime = System.currentTimeMillis();
            int sumCountpackage;
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            index.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bytes = stream.toByteArray();

            if ((bytes.length % 1024 == 0))
                sumCountpackage = bytes.length / 1024;
            else
                sumCountpackage = (bytes.length / 1024) + 1;

            Log.i("TAG", "文件總長度:" + bytes.length);
            final ServerFile serverFile = new ServerFile();
            serverFile.setSumCountPackage(sumCountpackage);
            serverFile.setCountPackage(1);
            serverFile.setBytes(bytes);
            serverFile.setId(loginId);
            serverFile.setSendId(username);
            serverFile.setToId(friendId);
            serverFile.setReceiveId(friendUserName);
            serverFile.setSendNickName(nickName);
            serverFile.setReceiveNickName(friendName);
            serverFile.setFileName(Build.MANUFACTURER + "-" + UUID.randomUUID() + ".jpg");
            serverFile.setEffectMessage(-2);
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
            File cacheDir;
            if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
                //Creates a new File instance from a parent abstract pathname and a child pathname string.
                cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), "TTImages_cache");
            else
                cacheDir = getActivity().getCacheDir();
            if (!cacheDir.exists())
                cacheDir.mkdirs();

            cacheDir = new File(cacheDir, String.valueOf(index.toString().hashCode()));

            saveSqliteHistory(cacheDir.getAbsolutePath(), 0, "2", 0, createTime);

            try {
                RandomAccessFile randomAccessFile = new RandomAccessFile(cacheDir, "rw");
                randomAccessFile.write(bytes);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ChatMessage sqliteMsg = new ChatMessage();
            sqliteMsg.setMe(true);
            sqliteMsg.setMessage("");
            sqliteMsg.setBitmap(index);
            sqliteMsg.setFilePath(cacheDir.getAbsolutePath());
            displayMessage(sqliteMsg);
            //******************************************************************************************

            //下面為要存入messagelist的sqlite的資料(更改messagelist的順序)
            RowItem rowItem = new RowItem(friendId, friendUserName);
            rowItem.setContent("你傳出貼圖");
            rowItem.setCreateTime(System.currentTimeMillis());
            updateMessageList(rowItem) ;
        }else{
            Toast.makeText(getActivity(), "無法送出自製貼圖，請確認連線狀態", Toast.LENGTH_SHORT).show();
        }
    }



    private void BackFragment(){
        if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0){
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    private void myimageviewsize(ImageView imgid, int evenWidth, int evenHight) {
        // TODO 自動產生的方法 Stub
        ViewGroup.LayoutParams params = imgid.getLayoutParams();  //需import android.view.ViewGroup.LayoutParams;
        params.width = evenWidth;
        params.height = evenHight;
        imgid.setLayoutParams(params);
    }

    //處理動畫的監聽器
    private class effectListener implements Animation.AnimationListener {

        @Override
        public void onAnimationEnd(Animation arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onAnimationRepeat(Animation arg0) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onAnimationStart(Animation arg0) {
            // TODO Auto-generated method stub

        }

    }

    public void displayMessage(ChatMessage message) {
        adapter.add(message);
        adapter.notifyDataSetChanged();
        scroll();
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

    private void scroll() {
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }

    //type 0 : 普通message  1: 驚喜message  2:自製貼圖
    private void saveSqliteHistory(String messageText , int me , String type, int effecttype, long createTime){
        //創建MyDBHelper對象
        MyDBHelper dbHelper = new MyDBHelper(getActivity() , "Chat.db", null, 1);
        //得到一個可讀的SQLiteDatabase對象
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //生成ContentValues​​對象 //key:列名，value:想插入的值
        ContentValues cv = new ContentValues();

        //往ContentValues​​對象存放數據，鍵-值對模式
        if(me == 0) {
            cv.put("from_id", loginId);
            cv.put("to_id", friendId);
        }else{
            cv.put("from_id", friendId);
            cv.put("to_id" , loginId);
        }
        cv.put("content", messageText);
        cv.put("type" , type);

        cv.put("effecttype", effecttype);
        cv.put("createtime", createTime);

        //調用insert方法，將數據插入數據庫
        db.insert("Message", null, cv);
        //關閉數據庫
        db.close();

    }

    // 儲存驚喜message
    private void saveSqliteFile(String messageText , int me , String type, int effecttype, long createTime, String think){
        //創建MyDBHelper對象
        MyDBHelper dbHelper = new MyDBHelper(getActivity() , "Chat.db", null, 1);
        //得到一個可讀的SQLiteDatabase對象
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //生成ContentValues​​對象 //key:列名，value:想插入的值
        ContentValues cv = new ContentValues();

        //往ContentValues​​對象存放數據，鍵-值對模式
        if(me == 0) {
            cv.put("from_id", loginId);
            cv.put("to_id", friendId);
        }else{
            cv.put("from_id", friendId);
            cv.put("to_id" , loginId);
        }
        cv.put("content", messageText);
        cv.put("type" , type);

        cv.put("effecttype", effecttype);
        cv.put("createtime", createTime);

        cv.put("think", think);

        //調用insert方法，將數據插入數據庫
        db.insert("Message", null, cv);
        //關閉數據庫
        db.close();

    }


    private void loadSqliteHistory(){
        adapter = new ChatAdapter(getActivity(), new ArrayList<ChatMessage>());
        messagesContainer.setAdapter(adapter);

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
        Cursor cursor = db.query("Message" ,  new  String[]{ "id" , "from_id" , "to_id" , "content" , "type", "effecttype", "think", "read" }, "(from_id=? or to_id=?) and (from_id=? or to_id=?)", new String[]{loginId, loginId, friendId, friendId}  ,  null ,  null ,  null );
        while (cursor.moveToNext()){
            String from_id = cursor.getString(cursor.getColumnIndex( "from_id" ));
            String to_id = cursor.getString(cursor.getColumnIndex("to_id"));
            String content = cursor.getString(cursor.getColumnIndex( "content" ));
            String type = cursor.getString(cursor.getColumnIndex("type"));
            String effectType = cursor.getString(cursor.getColumnIndex("effecttype"));
            String think = cursor.getString(cursor.getColumnIndex("think"));
            String read = cursor.getString(cursor.getColumnIndex("read"));
            ChatMessage sqliteMsg = new ChatMessage();
            sqliteMsg.setId(2);
            sqliteMsg.setIsRead(Integer.parseInt(read));
            //type 0 : 普通訊息 1:驚喜 2:自製貼圖
            if(type != null) {
                if (type.equals("0")) {
                    if (from_id.equals(loginId) && to_id.equals(friendId)) {
                        sqliteMsg.setMe(true);
                        sqliteMsg.setMessage(content);
                        displayMessage(sqliteMsg);
                    } else if (from_id.equals(friendId) && to_id.equals(loginId)) {
                        sqliteMsg.setMe(false);
                        sqliteMsg.setMessage(content);
                        displayMessage(sqliteMsg);
                    }
                }else if(type.equals("1")){
                    if(from_id.equals(loginId) && to_id.equals(friendId)){
                        sqliteMsg.setMe(true);
                        sqliteMsg.setMessage("");
                        sqliteMsg.setFilePath(content);
                        sqliteMsg.setIsEffect(true);
                        sqliteMsg.setEffectMessage(Integer.parseInt(effectType));
                        sqliteMsg.setThink(think);
                        displayMessage(sqliteMsg);
                    }else if(from_id.equals(friendId) && to_id.equals(loginId)){
                        sqliteMsg.setMe(false);
                        sqliteMsg.setMessage("");
                        sqliteMsg.setFilePath(content);
                        sqliteMsg.setIsEffect(true);
                        sqliteMsg.setEffectMessage(Integer.parseInt(effectType));
                        sqliteMsg.setThink(think);
                        displayMessage(sqliteMsg);
                    }
                }else if(type.equals("2")){
                    if(from_id.equals(loginId) && to_id.equals(friendId)){
                        sqliteMsg.setMe(true);
                        sqliteMsg.setMessage("");
                        File sticker = new File(content);
                        Bitmap stickPicture = decodeFile(sticker);
                        sqliteMsg.setBitmap(stickPicture);
                        sqliteMsg.setFilePath(content);
                        displayMessage(sqliteMsg);
                    }else if(from_id.equals(friendId) && to_id.equals(loginId)){
                        sqliteMsg.setMe(false);
                        sqliteMsg.setMessage("");
                        File sticker = new File(content);
                        Bitmap stickPicture = decodeFile(sticker);
                        sqliteMsg.setBitmap(stickPicture);
                        sqliteMsg.setFilePath(content);
                        displayMessage(sqliteMsg);
                    }
                }
            }
        }

        cursor.close();
        //關閉數據庫
        db.close();
    }

    //從sqlite獲取聊天的messageList
    private ArrayList<RowItem> loadMessageListInSqlite(){
        ArrayList<RowItem>messageList = new ArrayList<RowItem>();

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
        Cursor cursor = db.rawQuery("SELECT Friend.friendusername,  MessageOrder.friendid, MessageOrder.content, MessageOrder.createtime" +
                "FROM MessageOrder INNER JOIN FRIEND ON MessageOrder.friendid = Friend.id", null);
        while (cursor.moveToNext()){
            String friendId = cursor.getString(cursor.getColumnIndex("friendid"));
            String friendusername = cursor.getString(cursor.getColumnIndex("friendusername"));
            String createtime = cursor.getString(cursor.getColumnIndex("createtime"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            RowItem rowItem = new RowItem(friendId, friendusername);
            rowItem.setContent(content);
            messageList.add(rowItem);
        }

        cursor.close();

        //關閉數據庫
        db.close();

        return messageList;
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

    public void showOfflineMessage(String[] offlineMessageArray, long[] createTimeArray){
        for(int i = 0 ; i < offlineMessageArray.length ; i++){
            ChatMessage getmsg = new ChatMessage();
            getmsg.setId(2);
            getmsg.setMe(false);
            getmsg.setMessage(offlineMessageArray[i]);
            getmsg.setDate(DateFormat.getDateTimeInstance().format(new Date()));
            System.out.println("offline :" + offlineMessageArray[i]);
            saveSqliteHistory(offlineMessageArray[i] , 1 , "0",0, createTimeArray[i]);
            Bundle bundle = new Bundle();
            bundle.putSerializable("GetMsg", getmsg);
            Message msg = new Message();
            msg.setData(bundle);
            handler.sendMessage(msg);
        }
    }

    public void Show_GetMessage(Message_entity message){
        if(friendUserName.equals(message.getFrom())) {
            //將前一個訊息的已讀符號拿到
            if(adapter.getCount() >0) {
                if (adapter.getItem(adapter.getCount() - 1).getIsme()) {
                    adapter.getItem(adapter.getCount() - 1).setIsRead(0);
                }
            }
            saveSqliteHistory(message.getMessage(), 1, "0",0, message.getCreateAt());
            ChatMessage getmsg = new ChatMessage();
            getmsg.setId(2);
            getmsg.setMe(false);
            getmsg.setMessage(message.getMessage());
            getmsg.setDate(DateFormat.getDateTimeInstance().format(new Date()));
            getmsg.setBitmap(null);
            Bundle bundle = new Bundle();
            bundle.putSerializable("GetMsg", getmsg);
            Message msg = new Message();
            msg.setData(bundle);
            handler.sendMessage(msg);

            //判斷是否為已讀
            if(isRead) {
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

            //下面為要存入messagelist的sqlite的資料
            RowItem rowItem = new RowItem(friendId, friendUserName);
            rowItem.setContent(message.getMessage());
            rowItem.setCreateTime(message.getCreateAt());
            updateMessageList(rowItem);
        }
    }

    //展現已讀
    private void showRead(Message_entity message){
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putInt("isRead" , message.getRead());
        msg.setData(bundle);
        readHandler.sendMessage(msg);
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

    //從sd卡裡獲取圖片
    public Bitmap getSDCardPhoto(String path) {
        File file = new File(path);
        if (file.exists()) {
            return BitmapUtils.decodeSampleBitMapFromFile(getActivity(), path, 0.5f);
        } else {
            return null;
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

    protected Handler readHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            if(adapter.getCount()>0) {
                if (adapter.getCount() > 1) {
                    adapter.getItem(adapter.getCount() - 2).setIsRead(0);
                }
                adapter.getItem(adapter.getCount() - 1).setIsRead(msg.getData().getInt("isRead"));
                adapter.notifyDataSetChanged();
                updateReadInSqlite();
            }

        }
    };


    //處理照片
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // usedView is a bool that checks is a view was destroyed and this was reused.
        // if it wasn't reused, this means we create a new one.
        if (resultCode == getActivity().RESULT_OK) {
            //selectedImage = data.getData();
            //fileHandler.obtainMessage(1).sendToTarget();
            ChatMessage getmsg = new ChatMessage();
            getmsg.setId(2);
            getmsg.setMe(true);
            getmsg.setMessage("");
            getmsg.setDate(DateFormat.getDateTimeInstance().format(new Date()));
            Bitmap icon = BitmapFactory.decodeResource(getResources(),
                    R.drawable.gift);
            getmsg.setBitmap(icon);

            Bundle bundle = new Bundle();
            bundle.putSerializable("GetMsg", getmsg);
            Message msg1 = new Message();
            msg1.setData(bundle);
            handler.sendMessage(msg1);

        }
         /*if(requestCode == 2){
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
}
