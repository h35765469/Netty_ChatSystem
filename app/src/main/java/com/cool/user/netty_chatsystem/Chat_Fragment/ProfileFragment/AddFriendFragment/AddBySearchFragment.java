package com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.AddFriendFragment;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.cool.user.netty_chatsystem.Chat_Client.handler.Client_UserHandler;
import com.cool.user.netty_chatsystem.ChatListViewAdapter.FriendRowItem;
import com.cool.user.netty_chatsystem.Chat_Sharepreference.SharePreferenceManager;
import com.cool.user.netty_chatsystem.Chat_Sqlite_ChatHistory.MyDBHelper;
import com.cool.user.netty_chatsystem.Chat_biz.entity.Friend;
import com.cool.user.netty_chatsystem.Chat_core.connetion.IMConnection;
import com.cool.user.netty_chatsystem.Chat_core.protocol.Commands;
import com.cool.user.netty_chatsystem.Chat_core.protocol.Handlers;
import com.cool.user.netty_chatsystem.Chat_core.transport.Header;
import com.cool.user.netty_chatsystem.Chat_core.transport.IMResponse;
import com.cool.user.netty_chatsystem.Chat_server.dto.FriendDTO;
import com.cool.user.netty_chatsystem.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user on 2016/10/8.
 */
public class AddBySearchFragment extends Fragment {
    ListView searchListView;
    String loginId;
    String username;
    String nickName;
    SearchFriendAdapter searchFriendAdapter;//放入listview的好友adapter
    ArrayList<String>loadFriendNameArrayList;
    ArrayList<FriendRowItem>friendRowItemArrayList;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater ,ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.addbysearch_fragment, container, false);
        ImageView backImg = (ImageView)rootView.findViewById(R.id.backImg);
        final EditText searchFriendText = (EditText)rootView.findViewById(R.id.searchFriendText);
        ImageView searchFriendImg = (ImageView)rootView.findViewById(R.id.searchFriendImg);
        searchListView = (ListView)rootView.findViewById(R.id.searchListView);

        // Session class instance
        SharePreferenceManager sharePreferenceManager = new SharePreferenceManager(getActivity().getApplicationContext());
        HashMap<String,String>userDetails = sharePreferenceManager.getUserDetails();
        loginId = sharePreferenceManager.getLoginId();
        username = userDetails.get(SharePreferenceManager.KEY_NAME);
        nickName = sharePreferenceManager.getNickName();


        Client_UserHandler client_userHandler = new Client_UserHandler();
        client_userHandler.setSearchFriendListListener(new Client_UserHandler.SearchFriendListListener() {
            @Override
            public void onSearchFriendListEvent(Friend friend) {
                Bundle bundle = new Bundle();
                bundle.putStringArray("searchFriendIdArray", friend.getFriendIdArray());
                bundle.putStringArray("searchFriendArray" , friend.getFriendArray());
                bundle.putStringArray("searchFriendNameArray", friend.getFriendNameArray());
                bundle.putStringArray("searchFriendAvatarArray", friend.getFriendAvatarUriArray());
                Message msg = new Message();
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        });

        loadFriendNameArrayList = loadFriendNameInSqlite();//獲取sqlite全部好友的名字
        friendRowItemArrayList = new ArrayList<>();
        searchFriendAdapter = new SearchFriendAdapter(getActivity(),friendRowItemArrayList, username, loginId, nickName, loadFriendNameArrayList);


        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //自動關閉鍵盤(如果有開啟的話)----------------------------------------------------------------------------------------------------------------------------------------
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

                if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0){
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });

        searchFriendImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IMConnection connection = Client_UserHandler.getConnection();
                if(connection != null) {
                    searchFriendAdapter.cleanAll();
                    searchFriendAdapter.notifyDataSetChanged();
                    String inputFriendName = searchFriendText.getText().toString();
                    if(!inputFriendName.isEmpty()) {
                        Friend friend = new Friend();
                        friend.setFriendName(inputFriendName);
                        friend.setId(loginId);
                        IMResponse resp = new IMResponse();
                        Header header = new Header();
                        header.setHandlerId(Handlers.USER);
                        header.setCommandId(Commands.FRIEND_SEARCH_REQUEST);
                        resp.setHeader(header);
                        resp.writeEntity(new FriendDTO(friend));
                        connection.sendResponse(resp);
                    }
                }else{
                    Toast.makeText(getActivity(), "無法搜尋好友，請確認連線狀態", Toast.LENGTH_SHORT).show();
                }
            }
        });

        searchFriendText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                IMConnection connection = Client_UserHandler.getConnection();
                if(connection != null) {
                    searchFriendAdapter.cleanAll();
                    searchFriendAdapter.notifyDataSetChanged();
                    String inputFriendName = searchFriendText.getText().toString();
                    if(!inputFriendName.isEmpty()) {
                        Friend friend = new Friend();
                        friend.setFriendName(inputFriendName);
                        friend.setId(loginId);
                        IMResponse resp = new IMResponse();
                        Header header = new Header();
                        header.setHandlerId(Handlers.USER);
                        header.setCommandId(Commands.FRIEND_SEARCH_REQUEST);
                        resp.setHeader(header);
                        resp.writeEntity(new FriendDTO(friend));
                        connection.sendResponse(resp);
                    }
                }else{
                    Toast.makeText(getActivity(), "無法搜尋好友，請確認連線狀態", Toast.LENGTH_SHORT).show();
                }
            }
        });


        return rootView;
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            String[] searchFriendIdArray = msg.getData().getStringArray("searchFriendIdArray");
            String[] searchFriendArray = msg.getData().getStringArray("searchFriendArray");
            String[] searchFriendNameArray = msg.getData().getStringArray("searchFriendNameArray");
            String[] searchFriendAvatarArray = msg.getData().getStringArray("searchFriendAvatarArray");

            if(searchFriendArray.length < 1){
                searchFriendAdapter.cleanAll();
                searchFriendAdapter.notifyDataSetChanged();
            }else {
                ArrayList<String>loadFriendNameArrayList = loadFriendNameInSqlite();//獲取sqlite全部好友的名字
                ArrayList<FriendRowItem>friendRowItemArrayList = new ArrayList<>();

                for(int i = 0 ; i < searchFriendIdArray.length ; i++){
                    FriendRowItem friendRowItem = new FriendRowItem(searchFriendIdArray[i], searchFriendArray[i], searchFriendNameArray[i]);
                    friendRowItem.setAvatarName(searchFriendAvatarArray[i]);//大頭貼檔案名
                    friendRowItemArrayList.add(friendRowItem);
                }
                searchFriendAdapter = new SearchFriendAdapter(getActivity(),friendRowItemArrayList, username, loginId, nickName, loadFriendNameArrayList);
                searchListView.setAdapter(searchFriendAdapter);

            }
        }
    };

    //從sqlite載入朋友名單
    private ArrayList<String> loadFriendNameInSqlite(){
        //以下為從sqlite載回收集資料
        MyDBHelper dbHelper =  new  MyDBHelper(getActivity() , "Chat.db" , null , 1 );
        //得到一個可寫的數據庫
        SQLiteDatabase db =dbHelper.getReadableDatabase();

        ArrayList<String> friendNameArrayList = new ArrayList<>();
        Cursor cursor = db.query("Friend" ,  new  String[]{"friendname"}, "status=? or status = ?  or  status = ? or status = ?", new String[]{"1","0","4","2"} ,  null ,  null ,  null );
        while (cursor.moveToNext()) {
            friendNameArrayList.add(cursor.getString(cursor.getColumnIndex("friendname")));
        }

        cursor.close();
        db.close();

        return friendNameArrayList;
    }
}
