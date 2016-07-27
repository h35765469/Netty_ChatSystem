package com.example.user.netty_chatsystem.Chat_Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.example.user.netty_chatsystem.ChangePassword_Activity;
import com.example.user.netty_chatsystem.Character_Activity;
import com.example.user.netty_chatsystem.Chat_Activity;
import com.example.user.netty_chatsystem.Chat_Client.handler.Client_UserHandler;
import com.example.user.netty_chatsystem.Chat_Listview_Friendlist.CustomBaseAdapter;
import com.example.user.netty_chatsystem.Chat_Listview_Friendlist.CustomBaseAdapter_horizontal;
import com.example.user.netty_chatsystem.Chat_Listview_Friendlist.RowItem;
import com.example.user.netty_chatsystem.Chat_Sharepreference.SharePreferenceManager;
import com.example.user.netty_chatsystem.Chat_biz.entity.Friend;
import com.example.user.netty_chatsystem.Chat_core.connetion.IMConnection;
import com.example.user.netty_chatsystem.Chat_core.protocol.Commands;
import com.example.user.netty_chatsystem.Chat_core.protocol.Handlers;
import com.example.user.netty_chatsystem.Chat_core.transport.Header;
import com.example.user.netty_chatsystem.Chat_core.transport.IMResponse;
import com.example.user.netty_chatsystem.Chat_server.dto.FriendDTO;
import com.example.user.netty_chatsystem.R;
import com.example.user.netty_chatsystem.Search_Activity;

import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by user on 2016/3/12.
 */
public class Friendlist_Fragment extends BaseFragment {
    public String[] titles;


    public static final Integer[] images = { R.drawable.bomb ,  R.drawable.bomb};

    public String [] Id_array;

    // name
    String username;

    //更改在設定中觀察者的眼睛圖示
    public int eyechange_count = 0;

    //更改最愛朋友的按鈕
    private int favorite_count = 0;

    //連上server的連接
    IMConnection connection;

    // sharePreferenceManager
    SharePreferenceManager sharePreferenceManager;

    String[] friendArray;


    ListView Friendlist_listview;
    TwoWayView favorite_listview;


    //用來處理獲得friend array之後的handler
    protected Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            // Put code here...
            friendArray = msg.getData().getStringArray("friendArray");
            int[] favoriteArray = msg.getData().getIntArray("favoriteArray");
            int[] blockArray = msg.getData().getIntArray("blockArray");
            titles = friendArray;
            Id_array = friendArray;

            List<RowItem> rowItems = new ArrayList<RowItem>();
            List<RowItem> favoriteRowItems = new ArrayList<RowItem>();

            for(int i = 0 ; i < titles.length ; i++){
                RowItem item = new RowItem(images[i], titles[i],Id_array[i]);
                if(favoriteArray[i] == 1){
                    favoriteRowItems.add(item);
                }
                rowItems.add(item);
            }
            CustomBaseAdapter adapter = new CustomBaseAdapter(getActivity(),rowItems);
            CustomBaseAdapter_horizontal adapter_horizontal = new CustomBaseAdapter_horizontal(getActivity(),favoriteRowItems);

            Friendlist_listview.setAdapter(adapter);
            favorite_listview.setAdapter(adapter_horizontal);

        }
    };


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_friendlist_titlebar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View initView(LayoutInflater inflater){
        View view = inflater.inflate(R.layout.activity_friendlist_,null);

        final ImageView Friendlist_search_imageview = (ImageView)view.findViewById(R.id.friendlist_search_imageview);
        final ImageView Friendlist_character_imageview = (ImageView)view.findViewById(R.id.friendlist_character_imageview);
        final ImageView Friendlist_addfriend_imageview = (ImageView)view.findViewById(R.id.friendlist_addfriend_imageview);

        Friendlist_listview = (ListView)view.findViewById(R.id.friendlist_listview);
        favorite_listview = (TwoWayView)view.findViewById(R.id.lvItems);

        sharePreferenceManager = new SharePreferenceManager(getActivity().getApplicationContext());
        // get user data from sharePreference
        HashMap<String, String> user = sharePreferenceManager.getUserDetails();

        // name
        username = user.get(SharePreferenceManager.KEY_NAME);

        if(Client_UserHandler.getConnection() != null){
            loadFriendList(username);
        }


        Client_UserHandler clientUserHandler = new Client_UserHandler();
        clientUserHandler.setFriendListListener(new Client_UserHandler.FriendListListener() {
            @Override
            public void onFriendListEvent(String[] friendArray, int[] favoriteArray, int[] blockArray) {
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putStringArray("friendArray", friendArray);
                bundle.putIntArray("favoriteArray", favoriteArray);
                bundle.putIntArray("blockArray" , blockArray);
                message.setData(bundle);
                handler.sendMessage(message);
            }
        });

        //註冊character的按鈕監聽
        Friendlist_character_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Character_Activity().mViewPager.setCurrentItem(1);
            }
        });


        //啟動增加好友的頁面
        Friendlist_addfriend_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent it = new Intent();
                it.setClass(getActivity(), Addfriend_Activity.class);
                startActivity(it);*/
                addFriend(username);
            }
        });

        //啟動搜尋好友的功能
        Friendlist_search_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent();
                it.putExtra("friendArray" , friendArray);
                it.setClass(getActivity(), Search_Activity.class);
                startActivity(it);
            }
        });

        //啟動Friendlist_listview的按鈕監聽器
        Friendlist_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final Dialog dialog = new Dialog(getActivity(), R.style.selectorDialog);
                dialog.setContentView(R.layout.resource_friendlist_dialog);

                // 由程式設定 Dialog 視窗外的明暗程度, 亮度從 0f 到 1f
                WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
                lp.dimAmount = 0.2f;
                dialog.getWindow().setAttributes(lp);
                dialog.show();

                //進入虛擬人物房間
                ImageView roomicon_imageview = (ImageView) dialog.findViewById(R.id.roomicon_imageview);
                roomicon_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent it = new Intent();
                        it.setClass(getActivity(), ChangePassword_Activity.class);
                        startActivity(it);
                    }
                });

                //進入好友聊天視窗
                ImageView messageicon_imageview = (ImageView) dialog.findViewById(R.id.messageicon_imageview);
                messageicon_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString("friend_id",Id_array[position]);
                        Intent it = new Intent();
                        it.putExtras(bundle);
                        it.setClass(getActivity(), Chat_Activity.class);
                        getActivity().startActivity(it);
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
                        Assign_settingdialog(setting_dialog ,Id_array[position]);

                        // 由程式設定 Dialog 視窗外的明暗程度, 亮度從 0f 到 1f
                        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
                        lp.dimAmount = 0.2f;
                        setting_dialog.getWindow().setAttributes(lp);
                        setting_dialog.show();
                    }
                });

                //添加好友為我的最愛
                final ImageView favorite_imageview = (ImageView) dialog.findViewById(R.id.favorite_imageview);
                favorite_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (favorite_count == 0) {
                            favorite_imageview.setImageResource(R.drawable.candy_red);
                            favorite_count = 1;
                            setFavoriteFriend(username , Id_array[position],1);
                        } else {
                            favorite_imageview.setImageResource(R.drawable.candy);
                            favorite_count = 0;
                            setFavoriteFriend(username , Id_array[position],0);
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

                // 由程式設定 Dialog 視窗外的明暗程度, 亮度從 0f 到 1f
                WindowManager.LayoutParams lp=dialog.getWindow().getAttributes();
                lp.dimAmount=0.2f;
                dialog.getWindow().setAttributes(lp);
                dialog.show();

                //進入虛擬人物房間
                ImageView roomicon_imageview = (ImageView)dialog.findViewById(R.id.roomicon_imageview);
                roomicon_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent it = new Intent();
                        it.setClass(getActivity(), ChangePassword_Activity.class);
                        startActivity(it);
                    }
                });

                //進入好友聊天視窗
                ImageView messageicon_imageview = (ImageView)dialog.findViewById(R.id.messageicon_imageview);
                messageicon_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString("friend_id","123");
                        Intent it = new Intent();
                        it.putExtras(bundle);
                        it.setClass(getActivity(),Chat_Activity.class);
                        getActivity().startActivity(it);
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
                        Assign_settingdialog(setting_dialog , Id_array[position]);

                        // 由程式設定 Dialog 視窗外的明暗程度, 亮度從 0f 到 1f
                        WindowManager.LayoutParams lp=dialog.getWindow().getAttributes();
                        lp.dimAmount=0.2f;
                        setting_dialog.getWindow().setAttributes(lp);
                        setting_dialog.show();
                    }
                });

                //添加好友為我的最愛
                final ImageView favorite_imageview = (ImageView)dialog.findViewById(R.id.favorite_imageview);
                favorite_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(favorite_count == 0){
                            favorite_imageview.setImageResource(R.drawable.candy_red);
                            favorite_count = 1;
                            setFavoriteFriend(username , Id_array[position],1);
                        }else{
                            favorite_imageview.setImageResource(R.drawable.candy);
                            favorite_count = 0;
                            setFavoriteFriend(username , Id_array[position],0);
                        }
                    }
                });
            }
        });

        return view;
    }

    @Override
    public void initData(Bundle savedInstanceState){

    }

    //載入朋友列
    public void loadFriendList(String username){
        connection = Client_UserHandler.getConnection();
        Friend friend = new Friend();
        friend.setUserName(username);
        friend.setFriendUserName("");
        friend.setFriendName("");
        friend.setFriendArray(new String[0]);

        IMResponse resp = new IMResponse();
        Header header = new Header();
        header.setHandlerId(Handlers.USER);
        header.setCommandId(Commands.FRIEND_REQUEST);
        resp.setHeader(header);
        resp.writeEntity(new FriendDTO(friend));
        connection.sendResponse(resp);
    }

    //新增好友
    public void addFriend(String username){
        connection = Client_UserHandler.getConnection();
        Friend friend = new Friend();
        friend.setUserName(username);
        friend.setFriendArray(new String[0]);
        friend.setFriendUserName("456");
        friend.setFriendName("");

        IMResponse resp = new IMResponse();
        Header header = new Header();
        header.setHandlerId(Handlers.USER);
        header.setCommandId(Commands.FRIEND_ADD_REQUEST);
        resp.setHeader(header);
        resp.writeEntity(new FriendDTO(friend));
        connection.sendResponse(resp);

    }

    //刪除好友
    public void removeFriend(String username , String friendName){
        connection = Client_UserHandler.getConnection();
        Friend friend = new Friend();
        friend.setUserName(username);
        friend.setFriendUserName(friendName);
        friend.setFriendName("");
        friend.setFriendArray(new String[0]);

        IMResponse resp = new IMResponse();
        Header header = new Header();
        header.setHandlerId(Handlers.USER);
        header.setCommandId(Commands.FRIEND_REMOVE_REQUEST);
        resp.setHeader(header);
        resp.writeEntity(new FriendDTO(friend));
        connection.sendResponse(resp);
    }

    //設為最愛
    public void setFavoriteFriend(String username , String friendName , int isFavorite){
        connection = Client_UserHandler.getConnection();
        Friend friend = new Friend();
        friend.setUserName(username);
        friend.setFriendUserName(friendName);
        friend.setFriendName("");
        friend.setFriendArray(new String[0]);
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
    public void setBlockFriend(String username , String friendName){
        connection = Client_UserHandler.getConnection();
        Friend friend = new Friend();
        friend.setUserName(username);
        friend.setFriendUserName(friendName);
        friend.setFriendName("");
        friend.setFriendArray(new String[0]);

        IMResponse resp = new IMResponse();
        Header header = new Header();
        header.setHandlerId(Handlers.USER);
        header.setCommandId(Commands.FRIEND_BLOCK_REQUEST);
        resp.setHeader(header);
        resp.writeEntity(new FriendDTO(friend));
        connection.sendResponse(resp);
    }

    //編輯姓名
    public void editFriendName(String username , String friendUserName , String friendName){
        connection = Client_UserHandler.getConnection();
        Friend friend = new Friend();
        friend.setUserName(username);
        friend.setFriendUserName(friendUserName);
        friend.setFriendName(friendName);
        friend.setFriendArray(new String[0]);

        IMResponse resp = new IMResponse();
        Header header = new Header();
        header.setHandlerId(Handlers.USER);
        header.setCommandId(Commands.FRIEND_EDITNAME_REQUEST);
        resp.setHeader(header);
        resp.writeEntity(new FriendDTO(friend));
        connection.sendResponse(resp);
    }


    //設定dialog_setiing上按鈕的功能
    public void Assign_settingdialog(final Dialog setting_dialog , final String friendName){
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

                ImageView  editname_yes_imageview = (ImageView)editname_dialog.findViewById(R.id.editname_yes_imageview);
                ImageView editname_no_imageview = (ImageView)editname_dialog.findViewById(R.id.editname_no_imageview);
                final BootstrapEditText friendname_edit = (BootstrapEditText)editname_dialog.findViewById(R.id.friendname_edit);

                editname_yes_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editFriendName(username,friendName,friendname_edit.getText().toString());
                        editname_dialog.dismiss();
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

                final ImageView eye_imageview = (ImageView)viewer_dialog.findViewById(R.id.eye_imageview);
                eye_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(eyechange_count == 0){
                            eye_imageview.setImageResource(R.drawable.medical_open);
                            eyechange_count = 1;
                        }
                        else{
                            eye_imageview.setImageResource(R.drawable.medical_close);
                            eyechange_count = 0;
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

                ImageView blockade_yes_imageview = (ImageView)blockade_dialog.findViewById(R.id.blockade_yes_imageview);
                ImageView blockade_no_imageview = (ImageView)blockade_dialog.findViewById(R.id.blockade_no_imageview);

                blockade_yes_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setBlockFriend(username , friendName);
                        blockade_dialog.dismiss();
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

                ImageView delete_yes_imageview = (ImageView)delete_dialog.findViewById(R.id.delete_yes_imageview);
                ImageView delete_no_imageview = (ImageView)delete_dialog.findViewById(R.id.delete_no_imageview);

                delete_yes_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeFriend(username , friendName);
                        delete_dialog.dismiss();
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
}
