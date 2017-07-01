package com.cool.user.netty_chatsystem.Chat_Fragment.WorldShareFragment;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cool.user.netty_chatsystem.ChatListViewAdapter.RowItem;
import com.cool.user.netty_chatsystem.Chat_Client.handler.Client_MessageHandler;
import com.cool.user.netty_chatsystem.Chat_Client.handler.Client_UserHandler;
import com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.CollectFragment.CollectData;
import com.cool.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.Chat_AnimationElement.BubbleEffect.BubbleView;
import com.cool.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.Chat_AnimationElement.HeartEffect.BezierEvaluator;
import com.cool.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.Utils.BitmapUtils;
import com.cool.user.netty_chatsystem.Chat_Fragment.WorldShareFragment.FriendContent.FriendLetterContentFragment;
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
import com.cool.user.netty_chatsystem.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.apache.http.NameValuePair;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by user on 2016/11/1.
 */
public class ShareContentFragment extends Fragment {
    private ImageView downLoadedImg, addRandomFriendImg, collectRandomContentImg, randomProfileImg, randomEffectImg, launchEffectImg, thinkImg;
    private TextView randomNameTxt, firstContentText;
    private RelativeLayout shareContentRelativeLayout;
    private LinearLayout iconBar;
    private boolean uiShow = true;
    String loginId, username;
    Boolean isFriendClick = true, isCollectClick = true;
    ArrayList<String> friendArrayList;
    ArrayList<CollectData> collectDataArrayList;
    RandomData randomData;
    DisplayImageOptions options;
    int ScreenHeight;
    int ScreenWidth;
    //發送廣播事件用的Action
    public static final String BROADCAT_ACTION = "com.netty_chatsystem.ChatBroadcast";


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater ,ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.sharecontent_fragment, container, false);
        shareContentRelativeLayout = (RelativeLayout)rootView.findViewById(R.id.shareContentRelativeLayout);
        iconBar = (LinearLayout)rootView.findViewById(R.id.iconbar);
        addRandomFriendImg = (ImageView)rootView.findViewById(R.id.addRandomFriendImg);
        collectRandomContentImg = (ImageView)rootView.findViewById(R.id.collectRandomContentImg);
        randomProfileImg = (ImageView)rootView.findViewById(R.id.randomProfileImg);
        downLoadedImg = (ImageView)rootView.findViewById(R.id.downLoadedImg);
        randomNameTxt = (TextView)rootView.findViewById(R.id.friendNameTxt);
        randomEffectImg = (ImageView)rootView.findViewById(R.id.randEffectImg);
        firstContentText = (TextView)rootView.findViewById(R.id.firstContentText);
        launchEffectImg = (ImageView)rootView.findViewById(R.id.launchEffectImg);
        thinkImg = (ImageView)rootView.findViewById(R.id.thinkImg);

        //螢幕的高
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        ScreenHeight = dm.heightPixels;
        ScreenWidth = dm.widthPixels;

        registerChatListener();//註冊監聽訊息的listener

        HttpParams params = new BasicHttpParams();
        // Turn off stale checking. Our connections break all the time anyway,
        // and it's not worth it to pay the penalty of checking every time.
        HttpConnectionParams.setStaleCheckingEnabled(params, false);
        // Default connection and socket timeout of 10 seconds. Tweak to taste.
        HttpConnectionParams.setConnectionTimeout(params, 10 * 1000);
        HttpConnectionParams.setSoTimeout(params, 10 * 1000);
        HttpConnectionParams.setSocketBufferSize(params, 8192);

        // Don't handle redirects -- return them to the caller. Our code
        // often wants to re-POST after a redirect, which we must do ourselves.
        HttpClientParams.setRedirecting(params, false);
        // Set the specified user agent and register standard protocols.
        HttpProtocolParams.setUserAgent(params, "some_randome_user_agent");
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

        ClientConnectionManager manager = new ThreadSafeClientConnManager(params, schemeRegistry);


        //ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getActivity()));


        // Session class instance
        SharePreferenceManager sharePreferenceManager = new SharePreferenceManager(getActivity());
        loginId = sharePreferenceManager.getLoginId();
        HashMap<String,String> userDetails = sharePreferenceManager.getUserDetails();
        username = userDetails.get(SharePreferenceManager.KEY_NAME);
        friendArrayList = loadFriendInSqlite();//載入朋友列
        collectDataArrayList = loadCollectInSqlite();//載入收集資料


        if(Client_UserHandler.getConnection() != null) {
            randomData = randomDataFromMySQL();//獲取從遠端獲得的隨機資料

            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.logo)
                    .showImageForEmptyUri(R.drawable.ic_empty)
                    .showImageOnFail(R.drawable.logo_red)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();
            

            /*FasterAnimationContainer mFasterAnimationsContainer;
            mFasterAnimationsContainer = FasterAnimationContainer
                    .getInstance(randomEffectImg);
            mFasterAnimationsContainer.addAllFrames(bomb,
                    ANIMATION_INTERVAL);
            mFasterAnimationsContainer.start();*/



            if(randomData.getRandomId() != null) {
                differentiateFriendCondition(loginId, friendArrayList, collectDataArrayList, randomData);//判別是否為朋友或以收集過的東西

                //download the content----------
                ImageLoader.getInstance().displayImage(Config.SERVER_ADDRESS +  randomData.getRandomContent() + ".jpg", downLoadedImg, options, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    }
                }, new ImageLoadingProgressListener() {
                    @Override
                    public void onProgressUpdate(String imageUri, View view, int current, int total) {

                    }
                });
                //-----------------------------------------------------------------------------------------------------------------------------------------
                if(randomData.getRandomProfile() != null && randomData.getRandomProfile().length() != 0 ) {
                    //download the profile----------------------------------
                    ImageLoader.getInstance().displayImage(Config.SERVER_PROFILE_ADDRESS + randomData.getRandomProfile() + ".jpg", randomProfileImg, options, new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        }
                    }, new ImageLoadingProgressListener() {
                        @Override
                        public void onProgressUpdate(String imageUri, View view, int current, int total) {
                        }
                    });
                }else{
                    randomProfileImg.setImageResource(R.drawable.logo_red);
                }

                randomNameTxt.setText(randomData.getRandomNickName());
            }
            //--------------------------------------
        }else{
            Toast.makeText(getActivity(), "無法獲取新驚喜，請確認連線狀態", Toast.LENGTH_SHORT).show();
        }

        downLoadedImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Client_UserHandler.getConnection() != null) {
                    collectRandomContentImg.setImageResource(R.drawable.collectbook_gray);
                    addRandomFriendImg.setImageResource(R.drawable.adduser);
                    isCollectClick = true;
                    isFriendClick = true;
                    randomData = randomDataFromMySQL();//獲取從遠端獲得的隨機資料
                    friendArrayList = loadFriendInSqlite();//載入朋友列
                    collectDataArrayList = loadCollectInSqlite();//載入收集資料
                    if(randomData.getRandomId() != null) {
                        differentiateFriendCondition(loginId, friendArrayList, collectDataArrayList, randomData);
                        //new DownloadImage(randomData.getRandomContent() + ".jpg", 0).execute();//down the content
                        //new DownloadImage(randomData.getRandomProfile() + ".jpg", 1).execute();//down the profile
                        //Picasso.with(getActivity()).load(SERVER_ADDRESS + randomData.getRandomContent() + ".jpg").into(downLoadedImg);//download the content
                        //Picasso.with(getActivity()).load(SERVER_PROFILE_ADDRESS + randomData.getRandomProfile() + ".jpg").into(randomProfileImg);//download the profile

                        //download the content----------
                        ImageLoader.getInstance().displayImage(Config.SERVER_ADDRESS +  randomData.getRandomContent() + ".jpg", downLoadedImg, options, new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingStarted(String imageUri, View view) {
                            }

                            @Override
                            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            }

                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            }
                        }, new ImageLoadingProgressListener() {
                            @Override
                            public void onProgressUpdate(String imageUri, View view, int current, int total) {
                            }
                        });
                        //-----------------------------------------------------------------------------------------------------------------------------------------

                        System.out.println("randomProfile " + randomData.getRandomProfile());
                        if(randomData.getRandomProfile() != null && randomData.getRandomProfile().length() != 0) {
                            //download the profile----------------------------------
                            ImageLoader.getInstance().displayImage(Config.SERVER_PROFILE_ADDRESS + randomData.getRandomProfile() + ".jpg", randomProfileImg, options, new SimpleImageLoadingListener() {
                                @Override
                                public void onLoadingStarted(String imageUri, View view) {
                                }

                                @Override
                                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                }

                                @Override
                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                }
                            }, new ImageLoadingProgressListener() {
                                @Override
                                public void onProgressUpdate(String imageUri, View view, int current, int total) {
                                }
                            });
                        }else{
                            randomProfileImg.setImageResource(R.drawable.logo_red);
                        }

                        randomNameTxt.setText(randomData.getRandomNickName());
                    }
                }else{
                    Toast.makeText(getActivity(), "無法獲取新驚喜，請確認連線狀態", Toast.LENGTH_SHORT).show();
                }
            }
        });

        firstContentText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.shareContentContainer, new FriendLetterContentFragment());
                fragmentTransaction.commit();
            }
        });


        addRandomFriendImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Client_UserHandler.getConnection() != null) {
                    if(isFriendClick) {
                        requestFriend(randomData.getRandomId());
                        saveNewFriend(randomData.getRandomId(), randomData.getRandomUserName(), randomData.randomNickName, randomData.getRandomProfile());
                        addRandomFriendImg.setImageResource(R.drawable.logo);
                        isFriendClick = false;
                    }else{
                        Toast.makeText(getActivity(), "已是 好友", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getActivity(), "無法送出好友邀請，請確認連線狀態", Toast.LENGTH_SHORT).show();
                }
            }
        });

        collectRandomContentImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Client_UserHandler.getConnection() !=null) {
                    if(isCollectClick) {
                        saveCollectRandomContentToRemoteServe(randomData.getRandomId(), randomData.getRandomContent());//儲存收集資料到server
                        sendCollectNotification(randomData.getRandomId());
                        saveCollectRandomContentInSqlite(randomData.getRandomId(), randomData.getRandomContent());
                        collectRandomContentImg.setImageResource(R.drawable.agenda_color);
                        isCollectClick = false;
                    }else{
                        Toast.makeText(getActivity(), "你已收藏過此內容", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getActivity(), "無法收藏，請確認連線狀態", Toast.LENGTH_SHORT).show();
                }
            }
        });

        launchEffectImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Client_UserHandler.getConnection() != null){
                    launchEffectImg.setEnabled(false);
                    downLoadedImg.setEnabled(false);

                    if(randomData.getRandomId() != null){
                        switch(randomData.getRandomEffect()){
                            case "0" : bombEffect();
                                break;
                            case "1" : heartEffect();
                                break;
                            case "2" : bubbleEffect();
                                break;
                            default:
                                Toast.makeText(getActivity(), "無特效呵呵!", Toast.LENGTH_SHORT).show();
                                launchEffectImg.setEnabled(true);
                                downLoadedImg.setEnabled(true);
                                break;
                        }
                    }
                }else{
                    Toast.makeText(getActivity(), "無法觀看特效，請確認連線狀態", Toast.LENGTH_SHORT).show();
                }
            }
        });

        thinkImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Client_UserHandler.getConnection() != null) {
                    final Dialog dialog = new Dialog(getActivity(), R.style.selectorDialog);
                    dialog.setContentView(R.layout.resource_think_dialog);
                    TextView thinkText = (TextView) dialog.findViewById(R.id.thinkText);
                    if (randomData.getRandomThink().length() > 0 && randomData != null) {
                        thinkText.setText(randomData.getRandomThink());
                    }

                    // 由程式設定 Dialog 視窗外的明暗程度, 亮度從 0f 到 1f
                    WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
                    lp.dimAmount = 0.5f;
                    dialog.getWindow().setAttributes(lp);
                    dialog.show();
                }else{
                    Toast.makeText(getActivity(), "無法觀看心情，請確認連線狀態", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //觀看大頭貼
        randomProfileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Client_UserHandler.getConnection() != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("profile", randomData.getRandomProfile());
                    Intent intent = new Intent();
                    intent.putExtras(bundle);
                    intent.setClass(getActivity(), ProfilePreviewActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(getActivity(), "無法觀看大頭貼，請確認連線狀態 ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    //顯隱藏所有UI按鈕
    private void showHideUI(){
        if(uiShow){
            iconBar.setVisibility(View.GONE);
            addRandomFriendImg.setVisibility(View.GONE);
            randomProfileImg.setVisibility(View.GONE);
            randomNameTxt.setVisibility(View.GONE);
            uiShow = false;
        }else{
            iconBar.setVisibility(View.VISIBLE);
            addRandomFriendImg.setVisibility(View.VISIBLE);
            randomProfileImg.setVisibility(View.VISIBLE);
            randomNameTxt.setVisibility(View.VISIBLE);
            uiShow = true;
        }

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
                saveInviteFriend(friend);
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

    private void saveInviteFriend(Friend friend){
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
        System.out.println("shareContent " + messageText);
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

    /*Down the content and profile picutre from remote server
           Type 0:content , 1 : profile
    */
    private class DownloadImage extends AsyncTask<Void, Void, Bitmap> {
        String name;
        int type;
        public DownloadImage(String name, int type){
            this.name = name;
            this.type = type;
        }
        @Override
        protected Bitmap doInBackground(Void... params) {
            String url;
            if(type == 0) {
                url = Config.SERVER_ADDRESS + name;
            }else{
                url = Config.SERVER_PROFILE_ADDRESS + name;
            }

            try{
                URLConnection connection = new URL(url).openConnection();
                connection.setConnectTimeout(1000 * 30);
                connection.setReadTimeout(1000*30);
                return BitmapFactory.decodeStream((InputStream) connection.getContent(), null, null);

            }catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null){
                if(type ==0) {
                    downLoadedImg.setVisibility(View.VISIBLE);
                    downLoadedImg.setImageBitmap(bitmap);
                }else{
                    Bitmap thumbnailBitmap = BitmapUtils.createBitmapThumbnail(bitmap,true,100,100);//壓縮大頭貼到超小圖片
                    randomProfileImg.setImageBitmap(thumbnailBitmap);
                }
            }else{
                if(type == 0){
                    downLoadedImg.setImageResource(R.drawable.logo_red);
                }else{
                    randomProfileImg.setImageResource(R.drawable.logo_red);
                }
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    private RandomData randomDataFromMySQL(){
        RandomData randomData = new RandomData();
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());

        try {
            ArrayList<NameValuePair>params = new ArrayList<>();
            String result = DBConnector.executeQuery("", Config.LOADWORLD_CONTENT_URL, params);
            /*
                      SQL 結果有多筆資料時使用JSONArray
                      只有一筆資料時直接建立JSONObject物件
                      JSONObject jsonData = new JSONObject(result);
                      */
            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                randomData.setRandomId(jsonData.getString("ownerid"));
                randomData.setRandomContent(jsonData.getString("content"));
                randomData.setRandomUserName(jsonData.getString("username"));
                randomData.setRandomProfile(jsonData.getString("profilename"));
                randomData.setRandomNickName(jsonData.getString("nickname"));
                randomData.setRandomThink(jsonData.getString("think"));
                randomData.setRandomEffect(jsonData.getString("effect"));
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return randomData;
    }

    //用來裝遠端獲得的data
    class   RandomData{
        String randomId;
        String randomContent;
        String randomUserName;
        String randomProfile;
        String randomNickName;
        String randomThink;
        String randomEffect;

        public RandomData(){
        }

        public String getRandomId(){
            return randomId;
        }
        public void setRandomId(String randomId){
            this.randomId = randomId;
        }

        public String getRandomContent(){
            return randomContent;
        }
        public void setRandomContent(String randomContent){
            this.randomContent = randomContent;
        }
        public String getRandomUserName(){
            return randomUserName;
        }

        public void setRandomUserName(String collectUserName) {
            this.randomUserName = collectUserName;
        }

        public String getRandomProfile(){
            return randomProfile;
        }

        public void setRandomProfile(String randomProfile){
            this.randomProfile = randomProfile;
        }

        public String getRandomNickName(){
            return randomNickName;
        }
        public void setRandomNickName(String randomNickName){
            this.randomNickName = randomNickName;
        }

        public String getRandomThink(){
            return randomThink;
        }
        public void setRandomThink(String randomThink){
            this.randomThink = randomThink;
        }

        public String getRandomEffect(){
            return randomEffect;
        }
        public void setRandomEffect(String randomEffect){
            this.randomEffect = randomEffect;
        }
    }

    // 請求朋友邀請
    public void requestFriend(String friendId){
        IMConnection connection = Client_UserHandler.getConnection();
        Friend friend = new Friend();
        friend.setId(loginId);
        friend.setFriendId(friendId);

        IMResponse resp = new IMResponse();
        Header header = new Header();
        header.setHandlerId(Handlers.USER);
        header.setCommandId(Commands.FRIEND_ADD_REQUEST);
        resp.setHeader(header);
        resp.writeEntity(new FriendDTO(friend));
        connection.sendResponse(resp);
    }

    //存取新朋友進去sqlite裡
    private void saveNewFriend(String friendId, String friendUserName, String friendName, String friendAvatarUri){
        //創建MyDBHelper對象
        MyDBHelper dbHelper = new MyDBHelper(getActivity(), "Chat.db", null, 1);
        //得到一個可讀的SQLiteDatabase對象
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        ContentValues cv = new ContentValues();

        //往ContentValues​​對象存放數據，鍵-值對模式
        cv.put("id", friendId);
        cv.put("friendusername", friendUserName);
        cv.put("friendname", friendName);
        cv.put("friendAvatarUri", friendAvatarUri);
        cv.put("status", 4);//status 0 : 代表認證中
        cv.put("viewer", 0);
        cv.put("favorite", 0);

        //調用insert方法，將數據插入數據庫
        db.insert("Friend", null, cv);

        db.close();
    }

    //儲存收集的文章進入sqlite中
    private void saveCollectRandomContentInSqlite(String collectId, String collectContent){
        //創建MyDBHelper對象
        MyDBHelper dbHelper = new MyDBHelper(getActivity() , "Chat.db", null, 1);
        //得到一個可讀的SQLiteDatabase對象
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //生成ContentValues​​對象 //key:列名，value:想插入的值
        ContentValues cv = new ContentValues();

        cv.put("id", collectId);
        cv.put("content", collectContent);
        cv.put("isdelete", 0);

        db.insert("Collect", null, cv);
        db.close();
    }

    private void sendCollectNotification(String toId){
        IMConnection connection = Client_UserHandler.getConnection();
        Message_entity  message_entity = new Message_entity();
        message_entity.setId(loginId);
        message_entity.setToId(toId);

        IMResponse resp = new IMResponse();
        Header header = new Header();
        header.setHandlerId(Handlers.MESSAGE);
        header.setCommandId(Commands.COLLECT_NOTIFICATION_REQUEST);
        resp.setHeader(header);
        resp.writeEntity(new MessageDTO(message_entity));
        connection.sendResponse(resp);

    }

    //儲存collect data 進入遠端mysql
    private void saveCollectRandomContentToRemoteServe(String ownerid, String collectContent){
        ArrayList<NameValuePair>params = new ArrayList<>();
        params.add(new BasicNameValuePair("ownerid", ownerid));
        params.add(new BasicNameValuePair("collectid", loginId));
        params.add(new BasicNameValuePair("content", collectContent));
        String result = DBConnector.executeQuery("", Config.COLLECT_SAVE_URL, params);
    }

    //從sqlite載入朋友名單
    private ArrayList<String> loadFriendInSqlite(){
        //以下為從sqlite載回收集資料
        MyDBHelper dbHelper =  new  MyDBHelper(getActivity() , "Chat.db" , null , 1 );
        //得到一個可寫的數據庫
        SQLiteDatabase db =dbHelper.getReadableDatabase();

        ArrayList<String> friendArrayList = new ArrayList<>();
        Cursor cursor = db.query("Friend" ,  new  String[]{"id"}, "status=? or status=? or status=? or status =?", new String[]{"1","0", "4","2"} ,  null ,  null ,  null );
        while (cursor.moveToNext()) {
            friendArrayList.add(cursor.getString(cursor.getColumnIndex("id")));
        }

        cursor.close();
        db.close();

        return friendArrayList;
    }

    //從sqlite載入收集資料
    private ArrayList<CollectData> loadCollectInSqlite(){
        //以下為從sqlite載回收集資料
        MyDBHelper dbHelper =  new  MyDBHelper(getActivity() , "Chat.db" , null , 1 );
        //得到一個可寫的數據庫
        SQLiteDatabase db =dbHelper.getReadableDatabase();

        ArrayList<CollectData> collectDataArrayList = new ArrayList<>();

        Cursor cursor = db.query("Collect" ,  new  String[]{"id", "content"},  "isdelete=?" , new String[]{"0"}, null, null, null );
        while (cursor.moveToNext()) {
            CollectData collectData = new CollectData();
            collectData.setCollectId(cursor.getString(cursor.getColumnIndex("id")));
            collectData.setCollectContent(cursor.getString(cursor.getColumnIndex("content")));
            collectDataArrayList.add(collectData);
        }

        cursor.close();
        db.close();

        return collectDataArrayList;
    }

    private void  differentiateFriendCondition(String loginId, ArrayList<String>friendArrayList, ArrayList<CollectData>collectDataArrayList, RandomData randomData){
        if(randomData.getRandomId().equals(loginId)) {
            addRandomFriendImg.setImageResource(R.drawable.logo);
            collectRandomContentImg.setImageResource(R.drawable.agenda_color);
            isCollectClick = false;
            isFriendClick = false;
        }else {
            if (friendArrayList.contains(randomData.getRandomId())) {
                addRandomFriendImg.setImageResource(R.drawable.logo);
                isFriendClick = false;
            }

            for (int i = 0; i < collectDataArrayList.size(); i++) {
                if (collectDataArrayList.get(i).getCollectId().equals(randomData.getRandomId())) {
                    if (collectDataArrayList.get(i).getCollectContent().equals(randomData.getRandomContent())) {
                        collectRandomContentImg.setImageResource(R.drawable.agenda_color);
                        isCollectClick = false;
                    }
                }
            }
        }
    }

    //炸彈特效
    private void bombEffect(){
         randomEffectImg.setImageDrawable(null);
         Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.bombeffect);
         randomEffectImg.setImageResource(R.drawable.animation_list_boom);


        //圖片大小
        myimageviewsize(randomEffectImg, (int) (ScreenHeight / 1.7), (int) (ScreenHeight / 1.7));


        randomEffectImg.clearAnimation();
        ((AnimationDrawable)(randomEffectImg.getDrawable())).stop();

        // 重新将Frame動畫设置到第-1位，也就是重新開始
        ((AnimationDrawable)(randomEffectImg.getDrawable())).selectDrawable(0);


        ((AnimationDrawable)(randomEffectImg.getDrawable())).start();
        randomEffectImg.startAnimation(animation);
        animation.setFillAfter(true);
        animation.setAnimationListener(new effectListener());

    }

    private void heartEffect(){
        for(int i = 0 ; i < 55 ; i++){
            playheart(shareContentRelativeLayout, ScreenWidth, ScreenHeight, i);
        }
    }

    private void bubbleEffect(){
        final BubbleView bubbleView = new BubbleView(getActivity(), ScreenWidth, ScreenHeight);
        shareContentRelativeLayout.addView(bubbleView);
        CountDownTimer countDownTimer = new CountDownTimer(6*1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                shareContentRelativeLayout.removeView(bubbleView);
                downLoadedImg.setEnabled(true);
                launchEffectImg.setEnabled(true);
            }
        };
        countDownTimer.start();
    }

    //處理動畫的圖片大小
    private void myimageviewsize(ImageView imgid, int evenWidth, int evenHight) {
        // TODO 自動產生的方法 Stub
        ViewGroup.LayoutParams params = imgid.getLayoutParams();  //需import android.view.ViewGroup.LayoutParams;
        params.width = evenWidth;
        params.height = evenHight;
        imgid.setLayoutParams(params);
    }

    //處理炸彈動畫的監聽器
    private class effectListener implements Animation.AnimationListener {

        @Override
        public void onAnimationEnd(Animation arg0) {
            // TODO Auto-generated method stub
            downLoadedImg.setEnabled(true);
            launchEffectImg.setEnabled(true);
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

    //播放愛心動畫
    private void playheart(RelativeLayout effectMessageRelativeLayout, int ScreenWidth, int ScreenHeight, int count) {
        RelativeLayout.LayoutParams mDrawableLp = new RelativeLayout.LayoutParams((ScreenHeight/6), (ScreenHeight/6));
        mDrawableLp.addRule(RelativeLayout.CENTER_HORIZONTAL,
                RelativeLayout.TRUE);
        mDrawableLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                RelativeLayout.TRUE);
        Drawable[] mDrawables = new Drawable[4];
        Drawable mDrawablePink = ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.love1);
        Drawable mDrawableBlue = ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.love2);
        Drawable mDrawableGreen =ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.love3);
        Drawable mDrawableRed = ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.love4);
        mDrawables[0] = mDrawableBlue;
        mDrawables[1] = mDrawablePink;
        mDrawables[2] = mDrawableGreen;
        mDrawables[3] = mDrawableRed;

        Random random=new Random();
        ImageView heartImg =  new ImageView(getActivity());
        heartImg.setImageDrawable(mDrawables[random.nextInt(4)]);
        heartImg.setLayoutParams(mDrawableLp);
        effectMessageRelativeLayout.addView(heartImg);

        ObjectAnimator alpha = ObjectAnimator.ofFloat(heartImg, "alpha", 0.2f, 1.0f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(heartImg,View.SCALE_X, 0.0f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(heartImg,View.SCALE_Y, 0.2f, 1.0f);
        AnimatorSet set = new AnimatorSet();
        set.setDuration(800);
        set.playTogether(alpha, scaleX, scaleY);

        BezierEvaluator bezierEvaluator = new BezierEvaluator(getPointf(2, ScreenWidth, ScreenHeight), getPointf(1, ScreenWidth, ScreenHeight));
        ValueAnimator va = ValueAnimator.ofObject(bezierEvaluator, new PointF((ScreenWidth - (ScreenHeight/6.0F)) / 2,
                ScreenHeight - (ScreenHeight/4.0F)), new PointF(random.nextInt(ScreenWidth/2),0));
        va.addUpdateListener(new UpdateListener(heartImg));
        va.setTarget(heartImg);

        va.setDuration(1500+random.nextInt(2000)*2);

        AnimatorSet finalSet = new AnimatorSet();
        finalSet.playSequentially(set);
        finalSet.playSequentially(set, va);
        finalSet.addListener(new HeartAnimatorlistener(heartImg, effectMessageRelativeLayout, count));
        finalSet.start();
    }

    //處理愛心動畫得到點
    private PointF getPointf(int scale, int ScreenWidth, int ScreenHeight) {
        Random random=new Random();
        PointF pointF = new PointF();
        pointF.x = random.nextInt(ScreenWidth );
        pointF.y = random.nextInt(ScreenHeight)/ scale;
        return pointF;
    }

    //愛心特效的動畫listener
    private class HeartAnimatorlistener implements Animator.AnimatorListener {
        RelativeLayout effectMessageRelativeLayout;
        int count;

        private View target;
        public HeartAnimatorlistener(View target, RelativeLayout effectMessageRelativeLayout, int count) {
            this.target = target;
            this.effectMessageRelativeLayout = effectMessageRelativeLayout;
            this.count = count;
        }

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            effectMessageRelativeLayout.removeView((target));
            if(count == 54){
                downLoadedImg.setEnabled(true);
                launchEffectImg.setEnabled(true);
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }

    //愛心特效的上升動畫listener
    private class UpdateListener implements ValueAnimator.AnimatorUpdateListener {

        View target;

        public UpdateListener(View target) {
            this.target = target;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            PointF pointf = (PointF) animation.getAnimatedValue();
            target.setX(pointf.x);
            target.setY(pointf.y);
            target.setAlpha(1 - animation.getAnimatedFraction());
        }
    }


}
