package com.cool.user.netty_chatsystem.Chat_Fragment.FriendListFragment;

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
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.cool.user.netty_chatsystem.Chat_Client.handler.Client_MessageHandler;
import com.cool.user.netty_chatsystem.Chat_Client.handler.Client_UserHandler;
import com.cool.user.netty_chatsystem.Chat_Fragment.BaseFragment;
import com.cool.user.netty_chatsystem.Chat_Fragment.CameraFragment.OpenCameraFragment;
import com.cool.user.netty_chatsystem.Chat_Fragment.ChatFragment.ChatFragment;
import com.cool.user.netty_chatsystem.Chat_Fragment.SearchFragment;
import com.cool.user.netty_chatsystem.Chat_Fragment.WorldShareFragment.FriendContent.FriendLetterContentFragment;
import com.cool.user.netty_chatsystem.ChatListViewAdapter.FriendListAdapter;
import com.cool.user.netty_chatsystem.ChatListViewAdapter.FriendRowItem;
import com.cool.user.netty_chatsystem.ChatListViewAdapter.RowItem;
import com.cool.user.netty_chatsystem.Chat_MySQL.Config;
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
import com.cool.user.netty_chatsystem.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by user on 2016/3/12.
 */
public class Friendlist_Fragment extends BaseFragment {

    private ArrayList<FriendRowItem> friendRowItems;
    private List<FriendRowItem> favoriteRowItems;
    private ArrayList<Integer>favoriteArray = new ArrayList<>();
    private ArrayList<Integer> viewerArray = new ArrayList<>();
    private ArrayList<String>friendIdArrayList = new ArrayList<>();
    private ArrayList<String> friendArray = new ArrayList<>();
    private ArrayList<String> friendNameArray = new ArrayList<>();
    private ArrayList<String> friendAvatarUriArray = new ArrayList<>();
    // name
    String username;
    //userId
    String loginId;

    //連上server的連接
    IMConnection connection;

    // sharePreferenceManager
    SharePreferenceManager sharePreferenceManager;



    ListView Friendlist_listview;
    ListView favorite_listview;

    //將listview的adapter作為全域變數
    private FriendListAdapter adapter;
    private FriendListAdapter favoriteFriendAdapter;

    //發送廣播事件用的Action
    public static final String BROADCAT_ACTION = "com.netty_chatsystem.ChatBroadcast";



    //用來處理獲得friend array之後的handler
    protected Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            //清空list---------
            favoriteArray.clear();
            viewerArray.clear();
            friendRowItems.clear();
            favoriteRowItems.clear();
            friendArray.clear();
            friendNameArray.clear();
            friendIdArrayList.clear();

            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), Config.IMAGE_DIRECTORY_NAME);//圖片存取路徑

            // Put code here...
            friendIdArrayList = new ArrayList<>(Arrays.asList(msg.getData().getStringArray("friendIdArray")));
            friendArray = new ArrayList<>(Arrays.asList(msg.getData().getStringArray("friendArray")));
            friendNameArray = new ArrayList<>(Arrays.asList(msg.getData().getStringArray("friendNameArray")));


            //檢查朋友有無更換大頭貼
            if(!friendAvatarUriArray.isEmpty()) {
                for (int i = 0; i < friendAvatarUriArray.size(); i++) {
                    if (!friendAvatarUriArray.get(i).equals(msg.getData().getStringArray("friendAvatarUriArray")[i])) {
                        File file = new File(mediaStorageDir.getPath() + File.separator + friendAvatarUriArray.get(i));
                        file.delete();//刪除在local的本來的大頭貼
                        friendAvatarUriArray.set(i, msg.getData().getStringArray("friendAvatarUriArray")[i]);
                    }
                }
            }else{
                friendAvatarUriArray = new ArrayList<>(Arrays.asList(msg.getData().getStringArray("friendAvatarUriArray")));
            }


            for(int i = 0 ; i < msg.getData().getIntArray("favoriteArray").length ; i++) {
                favoriteArray.add(msg.getData().getIntArray("favoriteArray")[i]);
                viewerArray.add(msg.getData().getIntArray("viewerArray")[i]);
            }

            //下載來自遠端的朋友大頭貼-----------------------------------------------------------------------
            for(int i = 0 ; i < friendAvatarUriArray.size() ; i++){
                new DownloadImage(friendAvatarUriArray.get(i) + ".jpg").execute();
            }
            //----------------------------------------------------------------------------------------------------------------

            //判別是否有好友---------------------
            setListViewCondition();
            //------------------------------------------------
        }
    };

    @Override
    public View initView(LayoutInflater inflater){
        View view = inflater.inflate(R.layout.activity_friendlist_,null);

        //初始化imageLoader-------------------------------------
        final DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.logo)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.logo_red)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        //---------------------------------------------------------------

        registerChatListener();//註冊訊息傳送過來後的各種監聽器


        friendRowItems = new ArrayList<>();
        favoriteRowItems = new ArrayList<>();

        Friendlist_listview = (ListView)view.findViewById(R.id.friendlist_listview);
        favorite_listview = (ListView)view.findViewById(R.id.favoriteFriendListView);
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(),"fonts/fontawesome-webfont.ttf");
        final TextView searchbarTxt = (TextView) view.findViewById(R.id.searchbarTxt);
        TextView newpostTxt = (TextView) view.findViewById(R.id.newpostTxt);
        TextView friendNumberTxt = (TextView)view.findViewById(R.id.friendNumberTxt);
        searchbarTxt.setTypeface(font);
        searchbarTxt.setText("\uf002");
        newpostTxt.setTypeface(font);
        newpostTxt.setText("\uf030");

        sharePreferenceManager = new SharePreferenceManager(getActivity().getApplicationContext());
        // get user data from sharePreference
        final HashMap<String, String> user = sharePreferenceManager.getUserDetails();

        // name
        username = user.get(SharePreferenceManager.KEY_NAME);
        loginId = sharePreferenceManager.getLoginId();

        loadFriendListInSqlite();
        setListViewCondition();

        //從遠端獲取好友
        /*if (Client_UserHandler.getConnection() != null) {
            loadFriendList();
        }else {
            if(!friendArray.isEmpty()) {
                setListViewCondition();
            }
        }*/

        //以下為跳到新的fragment的變數
        final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);

        friendNumberTxt.setText("(" + String.valueOf(adapter.getCount()) + ")");//載入所有朋友總數

        /*Client_UserHandler clientUserHandler = new Client_UserHandler();
        clientUserHandler.setFriendListListener(new Client_UserHandler.FriendListListener() {
            @Override
            public void onFriendListEvent(Friend friend) {
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putStringArray("friendIdArray", friend.getFriendIdArray());
                bundle.putStringArray("friendArray", friend.getFriendArray());
                bundle.putStringArray("friendNameArray", friend.getFriendNameArray());
                bundle.putIntArray("favoriteArray", friend.getFavoriteArray());
                bundle.putIntArray("viewerArray" , friend.getViewerArray());
                bundle.putStringArray("friendAvatarUriArray", friend.getFriendAvatarUriArray());
                message.setData(bundle);
                handler.sendMessage(message);
            }
        });*/



        //啟動Friendlist_listview的按鈕監聽器
        Friendlist_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final Dialog dialog = new Dialog(getActivity(), R.style.selectorDialog);
                dialog.setContentView(R.layout.resource_friendlist_dialog);

                ImageView dialogProfileImg = (de.hdodenhof.circleimageview.CircleImageView)dialog.findViewById(R.id.dialogProfileImg);
                TextView friendName_textview = (TextView)dialog.findViewById(R.id.friendname_textview);
                friendName_textview.setText(adapter.getItem(position).getFriendName());

                // 由程式設定 Dialog 視窗外的明暗程度, 亮度從 0f 到 1f
                WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
                lp.dimAmount = 0.2f;
                dialog.getWindow().setAttributes(lp);
                dialog.show();

                ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
                File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                File file = new File(directory.getAbsolutePath(), adapter.getItem(position).getAvatarName());
                if(adapter.getItem(position).getAvatarName().length() > 0) {
                    ImageLoader.getInstance()
                            .displayImage("File://" + file.getAbsolutePath(), dialogProfileImg, options, new SimpleImageLoadingListener() {
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
                    dialogProfileImg.setImageResource(R.drawable.logo_red);
                }

                //進入memory區
                ImageView roomicon_imageview = (ImageView) dialog.findViewById(R.id.roomicon_imageview);
                roomicon_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FriendLetterContentFragment friendLetterContentFragment = new FriendLetterContentFragment();
                        fragmentTransaction.replace(R.id.friendContainer, friendLetterContentFragment);
                        fragmentTransaction.commit();
                        dialog.dismiss();
                    }
                });

                //進入好友聊天視窗
                ImageView messageicon_imageview = (ImageView) dialog.findViewById(R.id.messageicon_imageview);
                messageicon_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString("friendId", adapter.getItem(position).getFriendId());
                        bundle.putInt("whichFragment", R.id.friendContainer);
                        ChatFragment chatFragment = new ChatFragment();
                        chatFragment.setArguments(bundle);
                        fragmentTransaction.replace(R.id.friendContainer, chatFragment);
                        fragmentTransaction.commit();
                        dialog.dismiss();
                    }
                });

                //進入好友設定視窗
                ImageView settingicon_image = (ImageView) dialog.findViewById(R.id.settingicon_imageview);
                settingicon_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog setting_dialog = new Dialog(getActivity(), R.style.selectorDialog);
                        setting_dialog.setContentView(R.layout.resource_friendlist_settinglist_dialog);
                        //設定dialog_setiing上按鈕的功能
                        Assign_settingdialog(adapter.getItem(position).getFriendId(), setting_dialog, position);

                        // 由程式設定 Dialog 視窗外的明暗程度, 亮度從 0f 到 1f
                        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
                        lp.dimAmount = 0.2f;
                        setting_dialog.getWindow().setAttributes(lp);
                        setting_dialog.show();
                    }
                });

                //添加好友為我的最愛
                final ImageView favorite_imageview = (ImageView) dialog.findViewById(R.id.favorite_imageview);

                //獲得目前是否為favorite;
                if(adapter.getItem(position).getFriendFavorite() == 0){
                    favorite_imageview.setImageResource(R.drawable.candy);
                }else{
                    favorite_imageview.setImageResource(R.drawable.candy_red);
                }

                favorite_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(Client_UserHandler.getConnection()!= null) {
                            if (adapter.getItem(position).getFriendFavorite() == 0) {
                                favorite_imageview.setImageResource(R.drawable.candy_red);
                                adapter.getItem(position).setFriendFavorite(1);
                                setFavoriteFriend(adapter.getItem(position).getFriendId(), 1);
                                updateFriendFavoriteInSqlite(adapter.getItem(position).getFriendId(), 1);
                                favoriteFriendAdapter.addItem(adapter.getItem(position));
                                favoriteFriendAdapter.notifyDataSetChanged();
                            } else {
                                favorite_imageview.setImageResource(R.drawable.candy);
                                adapter.getItem(position).setFriendFavorite(0);
                                setFavoriteFriend(adapter.getItem(position).getFriendId(), 0);
                                updateFriendFavoriteInSqlite(adapter.getItem(position).getFriendId(), 0);
                                favoriteFriendAdapter.removeItem(adapter.getItem(position));
                                favoriteFriendAdapter.notifyDataSetChanged();
                            }
                        }else{
                            Toast.makeText(getActivity(), "無法設定最愛，請確認連線狀態", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


        //啟動favorite_listview的按鈕監聽器
        favorite_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final Dialog dialog = new Dialog(getActivity(),R.style.selectorDialog);
                dialog.setContentView(R.layout.resource_friendlist_dialog);

                ImageView dialogProfileImg = (de.hdodenhof.circleimageview.CircleImageView)dialog.findViewById(R.id.dialogProfileImg);
                TextView friendName_textview = (TextView)dialog.findViewById(R.id.friendname_textview);
                friendName_textview.setText(favoriteFriendAdapter.getItem(position).getFriendName());

                // 由程式設定 Dialog 視窗外的明暗程度, 亮度從 0f 到 1f
                WindowManager.LayoutParams lp=dialog.getWindow().getAttributes();
                lp.dimAmount=0.2f;
                dialog.getWindow().setAttributes(lp);
                dialog.show();

                ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
                File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                File file = new File(directory.getAbsolutePath(), favoriteFriendAdapter.getItem(position).getAvatarName());
                if(favoriteFriendAdapter.getItem(position).getAvatarName().length() > 0) {
                    ImageLoader.getInstance()
                            .displayImage("File://" + file.getAbsolutePath(), dialogProfileImg, options, new SimpleImageLoadingListener() {
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
                    dialogProfileImg.setImageResource(R.drawable.logo_red);
                }


                //進入別人的信件區
                ImageView roomicon_imageview = (ImageView)dialog.findViewById(R.id.roomicon_imageview);
                roomicon_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString("friendUserName", favoriteFriendAdapter.getItem(position).getFriendUserName());
                        FriendLetterContentFragment friendLetterContentFragment = new FriendLetterContentFragment();
                        friendLetterContentFragment.setArguments(bundle);
                        fragmentTransaction.replace(R.id.friendContainer, friendLetterContentFragment);
                        fragmentTransaction.commit();
                        dialog.dismiss();
                    }
                });

                //進入好友聊天視窗
                ImageView messageicon_imageview = (ImageView)dialog.findViewById(R.id.messageicon_imageview);
                messageicon_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString("friendId", favoriteFriendAdapter.getItem(position).getFriendId());
                        bundle.putInt("whichFragment", R.id.friendContainer);
                        ChatFragment chatFragment = new ChatFragment();
                        chatFragment.setArguments(bundle);
                        fragmentTransaction.replace(R.id.friendContainer, chatFragment);
                        fragmentTransaction.commit();
                        dialog.dismiss();
                    }
                });

                //進入好友設定視窗
                ImageView settingicon_image = (ImageView)dialog.findViewById(R.id.settingicon_imageview);
                settingicon_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog setting_dialog = new Dialog(getActivity(), R.style.selectorDialog);
                        setting_dialog.setContentView(R.layout.resource_friendlist_settinglist_dialog);

                        //設定dialog_setiing上按鈕的功能
                        Assign_settingdialog(favoriteFriendAdapter.getItem(position).getFriendId(), setting_dialog, position);

                        // 由程式設定 Dialog 視窗外的明暗程度, 亮度從 0f 到 1f
                        WindowManager.LayoutParams lp=dialog.getWindow().getAttributes();
                        lp.dimAmount=0.2f;
                        setting_dialog.getWindow().setAttributes(lp);
                        setting_dialog.show();
                    }
                });

                //添加好友為我的最愛
                final ImageView favorite_imageview = (ImageView)dialog.findViewById(R.id.favorite_imageview);

                //獲得目前是否為favorite
                favorite_imageview.setImageResource(R.drawable.candy_red);

                favorite_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(Client_UserHandler.getConnection() != null) {
                            setFavoriteFriend(favoriteFriendAdapter.getItem(position).getFriendId(), 0);
                            updateFriendFavoriteInSqlite(favoriteFriendAdapter.getItem(position).getFriendId(), 0);
                            int adapterIndex = adapter.getItemIndex(favoriteFriendAdapter.getItem(position));
                            adapter.getItem(adapterIndex).setFriendFavorite(0);
                            favoriteFriendAdapter.removePosition(position);
                            favoriteFriendAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }else{
                            Toast.makeText(getActivity(), "無法設定最愛，請確認連線狀態", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });

        searchbarTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("whichFragment", R.id.friendContainer);
                SearchFragment searchFragment = new SearchFragment();
                searchFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.friendContainer, searchFragment);
                fragmentTransaction.commit();
            }
        });

        newpostTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("whichFragment", R.id.friendContainer);
                OpenCameraFragment openCameraFragment = new OpenCameraFragment();
                openCameraFragment.setArguments(bundle);
                fragmentTransaction.addToBackStack("backFragment");
                fragmentTransaction.replace(R.id.friendContainer, openCameraFragment);
                fragmentTransaction.commit();
            }
        });

        return view;
    }

    @Override
    public void initData(Bundle savedInstanceState){

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

    //載入朋友列
    public void loadFriendList(){
        connection = Client_UserHandler.getConnection();
        Friend friend = new Friend();
        friend.setId(loginId);
        friend.setUserName(username);
        IMResponse resp = new IMResponse();
        Header header = new Header();
        header.setHandlerId(Handlers.USER);
        header.setCommandId(Commands.FRIEND_REQUEST);
        resp.setHeader(header);
        resp.writeEntity(new FriendDTO(friend));
        connection.sendResponse(resp);
    }

    //載入在sqlite裡的朋友列
    private void loadFriendListInSqlite(){
        //清空list
        cleanAllList();

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
        Cursor cursor = db.query("Friend" ,  new  String[]{"id","friendusername" , "friendname", "viewer" , "favorite", "friendAvatarUri"}, "status=?" , new String[]{"1"} ,  null ,  null ,  null );
        while (cursor.moveToNext()) {
            friendIdArrayList.add(cursor.getString(cursor.getColumnIndex("id")));
            friendArray.add(cursor.getString(cursor.getColumnIndex("friendusername")));
            friendNameArray.add(cursor.getString(cursor.getColumnIndex("friendname")));
            favoriteArray.add(cursor.getInt(cursor.getColumnIndex("favorite")));
            viewerArray.add(cursor.getInt(cursor.getColumnIndex("viewer")));
            friendAvatarUriArray.add(cursor.getString(cursor.getColumnIndex("friendAvatarUri")));
        }
        cursor.close();

        db.close();
    }

    //設置listivew的狀態
    private void setListViewCondition(){
        Bitmap avatar;

        for(int i = 0 ; i < friendArray.size(); i++){

            FriendRowItem friendRowItem = new FriendRowItem(friendIdArrayList.get(i), friendArray.get(i), friendNameArray.get(i));
            friendRowItem.setAvatarName(friendAvatarUriArray.get(i));
            friendRowItem.setFriendFavorite(favoriteArray.get(i));

            if (favoriteArray.get(i) == 1) {
                favoriteRowItems.add(friendRowItem);
            }
            friendRowItems.add(friendRowItem);
        }

        adapter = new FriendListAdapter(getActivity(),friendRowItems);
        favoriteFriendAdapter = new FriendListAdapter(getActivity(),favoriteRowItems);

        Friendlist_listview.setAdapter(adapter);
        favorite_listview.setAdapter(favoriteFriendAdapter);

        ListUtils.setDynamicHeight(Friendlist_listview);
        ListUtils.setDynamicHeight(favorite_listview);
    }

    //自動增加listview
    public static class ListUtils {
        public static void setDynamicHeight(ListView mListView) {
            ListAdapter mListAdapter = mListView.getAdapter();
            if (mListAdapter == null) {
                // when adapter is null
                return;
            }
            int height = 0;
            int desiredWidth = View.MeasureSpec.makeMeasureSpec(mListView.getWidth(), View.MeasureSpec.UNSPECIFIED);
            for (int i = 0; i < mListAdapter.getCount(); i++) {
                View listItem = mListAdapter.getView(i, null, mListView);
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                height += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = mListView.getLayoutParams();
            params.height = height + (mListView.getDividerHeight() * (mListAdapter.getCount() - 1));
            mListView.setLayoutParams(params);
            mListView.requestLayout();
        }
    }



    //新增好友訊息到friend的sqlite裡
    private void saveFriendInSqlite(String friendId, String friendUserName, String friendName, int viewer, int favorite, String friendAvatarUri){
        //創建MyDBHelper對象
        MyDBHelper dbHelper = new MyDBHelper(getActivity() , "Chat.db", null, 1);
        //得到一個可讀的SQLiteDatabase對象
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //生成ContentValues​​對象 //key:列名，value:想插入的值
        ContentValues cv = new ContentValues();

        //往ContentValues​​對象存放數據，鍵-值對模式
        cv.put("id", friendId);
        cv.put("friendusername", friendUserName);
        cv.put("friendname", friendName);
        cv.put("friendAvatarUri", friendAvatarUri);

        //0代表顯示可讀 ，1代表不顯示
        cv.put("viewer", String.valueOf(viewer));

        /*File cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),"TTImages_cache");
        cacheDir = new File(cacheDir,"1944246116" );
        cv.put("friendprofile", cacheDir.getAbsolutePath());*/

        cv.put("status", 1);

        //0代表普通朋友，1代表最愛
        cv.put("favorite", String.valueOf(favorite));

        //調用insert方法，將數據插入數據庫
        db.insert("Friend", null, cv);
        //關閉數據庫
        db.close();

    }

    //刪除好友
    public void removeFriend(String friendId){
        connection = Client_UserHandler.getConnection();
        Friend friend = new Friend();
        friend.setId(loginId);
        friend.setFriendId(friendId);
        IMResponse resp = new IMResponse();
        Header header = new Header();
        header.setHandlerId(Handlers.USER);
        header.setCommandId(Commands.FRIEND_REMOVE_REQUEST);
        resp.setHeader(header);
        resp.writeEntity(new FriendDTO(friend));
        connection.sendResponse(resp);
    }

    //設為最愛
    public void setFavoriteFriend(String friendId, int isFavorite){
        connection = Client_UserHandler.getConnection();
        Friend friend = new Friend();
        friend.setId(loginId);
        friend.setFriendId(friendId);
        friend.setIsFavorite(isFavorite);

        IMResponse resp = new IMResponse();
        Header header = new Header();
        header.setHandlerId(Handlers.USER);
        header.setCommandId(Commands.FRIEND_FAVORITE_REQUEST);
        resp.setHeader(header);
        resp.writeEntity(new FriendDTO(friend));
        connection.sendResponse(resp);
    }

    //設為封鎖
    public void setBlockFriend(String friendId , int isBlock){
        connection = Client_UserHandler.getConnection();
        Friend friend = new Friend();
        friend.setId(loginId);
        friend.setFriendId(friendId);
        friend.setIsBlock(isBlock);

        IMResponse resp = new IMResponse();
        Header header = new Header();
        header.setHandlerId(Handlers.USER);
        header.setCommandId(Commands.FRIEND_BLOCK_REQUEST);
        resp.setHeader(header);
        resp.writeEntity(new FriendDTO(friend));
        connection.sendResponse(resp);
    }

    //設定觀察者
    public void setViewFriend(String friendId, int viewer){
        connection = Client_UserHandler.getConnection();
        Friend friend = new Friend();
        friend.setId(loginId);
        friend.setFriendId(friendId);
        friend.setViewer(viewer);

        IMResponse resp = new IMResponse();
        Header header = new Header();
        header.setHandlerId(Handlers.USER);
        header.setCommandId(Commands.FRIEND_VIEWER_REQUEST);
        resp.setHeader(header);
        resp.writeEntity(new FriendDTO(friend));
        connection.sendResponse(resp);
    }

    //編輯姓名
    public void editFriendName(String friendId , String friendName){
        connection = Client_UserHandler.getConnection();
        Friend friend = new Friend();
        friend.setId(loginId);
        friend.setFriendId(friendId);
        friend.setFriendName(friendName);

        IMResponse resp = new IMResponse();
        Header header = new Header();
        header.setHandlerId(Handlers.USER);
        header.setCommandId(Commands.FRIEND_EDITNAME_REQUEST);
        resp.setHeader(header);
        resp.writeEntity(new FriendDTO(friend));
        connection.sendResponse(resp);

    }


    //設定dialog_setiing上按鈕的功能
    public void Assign_settingdialog(final String friendId, final Dialog setting_dialog, final int position){

        ImageView editnameicon_imageview = (ImageView)setting_dialog.findViewById(R.id.editnameicon_imageview);
        ImageView viewericon_imageview = (ImageView)setting_dialog.findViewById(R.id.viewericon_imageview);
        ImageView blockadeicon_imageview = (ImageView)setting_dialog.findViewById(R.id.blockadeicon_imageview);
        ImageView deleteicon_imageview = (ImageView)setting_dialog.findViewById(R.id.deleteicon_imageview);

        editnameicon_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setting_dialog.dismiss();
                final Dialog editname_dialog = new Dialog(getActivity(), R.style.selectorDialog);
                editname_dialog.setContentView(R.layout.resource_friendlist_settinglist_editname_dialog);
                editname_dialog.show();

                ImageView editname_yes_imageview = (ImageView) editname_dialog.findViewById(R.id.editname_yes_imageview);
                ImageView editname_no_imageview = (ImageView) editname_dialog.findViewById(R.id.editname_no_imageview);
                final BootstrapEditText friendname_edit = (BootstrapEditText) editname_dialog.findViewById(R.id.friendname_edit);

                editname_yes_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Client_UserHandler.getConnection() != null) {
                            editFriendName(friendId, friendname_edit.getText().toString());
                            updateFriendNameInSqlite(friendId, friendname_edit.getText().toString());
                            adapter.getItem(position).setFriendName(friendname_edit.getText().toString());
                            adapter.notifyDataSetChanged();
                            editname_dialog.dismiss();
                        } else {
                            Toast.makeText(getActivity(), "無法編輯朋友姓名，請確認連線狀態", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                editname_no_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editname_dialog.dismiss();
                    }
                });
            }
        });

        viewericon_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setting_dialog.dismiss();
                final Dialog viewer_dialog = new Dialog(getActivity(), R.style.selectorDialog);
                viewer_dialog.setContentView(R.layout.resource_friendlist_settinglist_viewer_dialog);
                viewer_dialog.show();
                final ImageView eye_imageview = (ImageView) viewer_dialog.findViewById(R.id.eye_imageview);

                if (viewerArray.get(position) == 0) {
                    eye_imageview.setImageResource(R.drawable.medical_close);
                } else {
                    eye_imageview.setImageResource(R.drawable.medical_open);
                }

                eye_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Client_UserHandler.getConnection() != null) {
                            if (viewerArray.get(position) == 0) {
                                eye_imageview.setImageResource(R.drawable.medical_open);
                                viewerArray.set(position, 1);
                                setViewFriend(friendId, 1);
                                updateFriendViewerInSqlite(friendId, 1);
                            } else {
                                eye_imageview.setImageResource(R.drawable.medical_close);
                                viewerArray.set(position, 0);
                                setViewFriend(friendId, 0);
                                updateFriendViewerInSqlite(friendId, 0);
                            }
                        } else {
                            Toast.makeText(getActivity(), "無法變更觀察者狀態，請確認是否連線", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        blockadeicon_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setting_dialog.dismiss();
                final Dialog blockade_dialog = new Dialog(getActivity(), R.style.selectorDialog);
                blockade_dialog.setContentView(R.layout.resource_friendlist_settinglist_blockade_dialog);
                blockade_dialog.show();

                ImageView blockade_yes_imageview = (ImageView) blockade_dialog.findViewById(R.id.blockade_yes_imageview);
                ImageView blockade_no_imageview = (ImageView) blockade_dialog.findViewById(R.id.blockade_no_imageview);


                blockade_yes_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Client_UserHandler.getConnection() != null) {
                            setBlockFriend(friendId, 2);
                            updateFriendStatusInSqlite(friendId, 2);
                            if(adapter.getItem(position).getFriendFavorite() == 1){
                                favoriteFriendAdapter.removeItem(adapter.getItem(position));
                                favoriteFriendAdapter.notifyDataSetChanged();
                            }
                            adapter.removePosition(position);
                            adapter.notifyDataSetChanged();
                            blockade_dialog.dismiss();
                        } else {
                            Toast.makeText(getActivity(), "無法封鎖好友，請確認連線狀態", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                blockade_no_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        blockade_dialog.dismiss();
                    }
                });
            }
        });

        deleteicon_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setting_dialog.dismiss();
                final Dialog delete_dialog = new Dialog(getActivity(), R.style.selectorDialog);
                delete_dialog.setContentView(R.layout.resource_friendlist_settinglist_delete_dialog);
                delete_dialog.show();

                ImageView delete_yes_imageview = (ImageView) delete_dialog.findViewById(R.id.delete_yes_imageview);
                ImageView delete_no_imageview = (ImageView) delete_dialog.findViewById(R.id.delete_no_imageview);

                delete_yes_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Client_UserHandler.getConnection() != null) {
                            removeFriend(friendId);
                            updateFriendStatusInSqlite(friendId, 3);
                            friendRowItems.remove(position);
                            adapter.notifyDataSetChanged();
                            delete_dialog.dismiss();
                        } else {
                            Toast.makeText(getActivity(), "無法刪除好友，請確認連線狀態", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                delete_no_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        delete_dialog.dismiss();
                    }
                });
            }
        });
    }

    //更改在sqlite裡friend的status為2(封鎖中)，3(刪除中)
    private void updateFriendStatusInSqlite(String friendId, int status){
        //創建MyDBHelper對象
        MyDBHelper dbHelper = new MyDBHelper(getActivity() , "Chat.db", null, 1);
        //得到一個可讀的SQLiteDatabase對象
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //生成ContentValues​​對象 //key:列名，value:想插入的值
        ContentValues cv = new ContentValues();
        cv.put("status", 2);//status 2 : 代表封鎖中
        db.update("Friend", cv, "id" + "=\"" + friendId + "\"", null);
        db.close();
    }



    //更改在sqlite裡friend的favorite為
    private void updateFriendFavoriteInSqlite(String friendId, int favorite){
        //創建MyDBHelper對象
        MyDBHelper dbHelper = new MyDBHelper(getActivity() , "Chat.db", null, 1);
        //得到一個可讀的SQLiteDatabase對象
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //生成ContentValues​​對象 //key:列名，value:想插入的值
        ContentValues cv = new ContentValues();
        cv.put("favorite", favorite);
        db.update("Friend", cv, "id" + "=\"" + friendId + "\"", null);
        db.close();
    }

    //更改在sqlite裡friend的viewer為
    private void updateFriendViewerInSqlite(String friendId, int viewer){
        //創建MyDBHelper對象
        MyDBHelper dbHelper = new MyDBHelper(getActivity() , "Chat.db", null, 1);
        //得到一個可讀的SQLiteDatabase對象
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //生成ContentValues​​對象 //key:列名，value:想插入的值
        ContentValues cv = new ContentValues();
        cv.put("viewer", viewer);
        db.update("Friend", cv, "id" + "=\"" + friendId + "\"", null);
        db.close();
    }

    //更改在sqlite裡的friend的Name為
    private void updateFriendNameInSqlite(String friendId, String friendName){
        //創建MyDBHelper對象
        MyDBHelper dbHelper = new MyDBHelper(getActivity() , "Chat.db", null, 1);
        //得到一個可讀的SQLiteDatabase對象
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //生成ContentValues​​對象 //key:列名，value:想插入的值
        ContentValues cv = new ContentValues();
        cv.put("friendname", friendName);
        db.update("Friend", cv, "id" + "=\"" + friendId + "\"", null);
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

    //淨空所有list
    private void cleanAllList(){
        favoriteArray.clear();
        viewerArray.clear();
        friendRowItems.clear();
        favoriteRowItems.clear();
        friendArray.clear();
        friendNameArray.clear();
        friendAvatarUriArray.clear();
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


    private class DownloadImage extends AsyncTask<Void, Void, Bitmap> {
        String name;
        int type;
        public DownloadImage(String name){
            this.name = name;
        }
        @Override
        protected Bitmap doInBackground(Void... params) {
            String url = Config.SERVER_PROFILE_ADDRESS + name;

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
            if(bitmap != null){
                File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), Config.IMAGE_DIRECTORY_NAME);

                // Create the storage directory if it does not exist
                if (!mediaStorageDir.exists()) {
                    if (!mediaStorageDir.mkdirs()) {
                        Log.d("sendContentFragment", "Oops! Failed create " + Config.IMAGE_DIRECTORY_NAME + " directory");
                    }
                }

                File file;
                file = new File(mediaStorageDir.getPath() + File.separator + name);

                try{
                    FileOutputStream fos = new FileOutputStream(file);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);   //Bitmap類別的compress方法產生檔案
                    bos.flush();
                    bos.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

}
