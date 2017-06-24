package com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.NewFriendFragment;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cool.user.netty_chatsystem.Chat_Client.handler.Client_UserHandler;
import com.cool.user.netty_chatsystem.Chat_Sharepreference.SharePreferenceManager;
import com.cool.user.netty_chatsystem.Chat_Sqlite_ChatHistory.MyDBHelper;
import com.cool.user.netty_chatsystem.Chat_biz.entity.Friend;
import com.cool.user.netty_chatsystem.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user on 2016/9/14.
 */
public class NewFriendFragment extends Fragment {
    NewFriendAdapter newFriendAdapter;
    TextView noNewFriendText;
    String username;
    String loginId;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater ,ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.newfriend_fragment, container, false);
        final ListView newFriendListView = (ListView)rootView.findViewById(R.id.newFriendListView);
        noNewFriendText = (TextView)rootView.findViewById(R.id.noNewFriendText);

        final ArrayList<HashMap<String,Object>>newFriendList = new ArrayList<HashMap<String,Object>>();

        SharePreferenceManager sharePreferenceManager = new SharePreferenceManager(getActivity().getApplicationContext());
        // get user data from sharePreference
        final HashMap<String, String> user = sharePreferenceManager.getUserDetails();
        // name
        username = user.get(SharePreferenceManager.KEY_NAME);
        loginId = sharePreferenceManager.getLoginId();

        newFriendAdapter = new NewFriendAdapter(getActivity(),newFriendList, username, loginId, noNewFriendText);
        loadNewFriendInSqlite();
        newFriendListView.setAdapter(newFriendAdapter);
        if(newFriendAdapter.getCount() == 0){
            noNewFriendText.setVisibility(View.VISIBLE);
        }

        Client_UserHandler client_userHandler = new Client_UserHandler();
        client_userHandler.setRequestFriendListListener(new Client_UserHandler.RequestFriendListListener() {
            @Override
            public void onRequestFriendListEvent(Friend friend) {
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("friendId", friend.getFriendId());
                bundle.putString("friendUserName", friend.getFriendUserName());
                bundle.putString("friendNickName", friend.getFriendName());
                bundle.putString("friendAvatarUri", friend.getFriendAvatarUri());
                message.setData(bundle);
                handler.sendMessage(message);
            }
        });


        TextView backTxt = (TextView)rootView.findViewById(R.id.backTxt);
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(),"fonts/fontawesome-webfont.ttf");//設定back的按紐
        backTxt.setTypeface(font);
        backTxt.setText("\uf060");
        backTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0){
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });

        return rootView;
    }

    //用來處理獲得newfriend 之後的handler
    protected Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("friendUserName", msg.getData().getString("friendUserName"));
            map.put("friendId",msg.getData().getString("friendId"));
            map.put("friendNickName", msg.getData().getString("friendNickName"));
            map.put("friendAvatarUri", msg.getData().getString("friendAvatarUri"));
            newFriendAdapter.add(map);
            newFriendAdapter.notifyDataSetChanged();
            saveNewFriendInSqlite(map);
            noNewFriendText.setVisibility(View.GONE);
        }
    };

    protected Handler newFriendHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String[] checkNewFriendArray = msg.getData().getStringArray("checkNewFriendArray");
            for(int i = 0 ; i < checkNewFriendArray.length ; i++){
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("friendUserName", checkNewFriendArray[i]);
                newFriendAdapter.add(map);
            }
            newFriendAdapter.notifyDataSetChanged();
            saveNewFriendListInSqlite();
        }
    };

    //載入在serve上的新朋友名單
    /*private void loadNewFriendFromServe(String username){
        IMConnection connection = Client_UserHandler.getConnection();
        Friend friend = new Friend();
        friend.setUserName(username);

        IMResponse resp = new IMResponse();
        Header header = new Header();
        header.setHandlerId(Handlers.USER);
        header.setCommandId(Commands.FRIEND_NEWFRIENDCHECK_REQUEST);
        resp.setHeader(header);
        resp.writeEntity(new FriendDTO(friend));
        connection.sendResponse(resp);
    }*/

    private void  loadNewFriendInSqlite(){
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
        Cursor cursor = db.query("Friend" ,  new  String[]{"id", "friendusername", "friendname", "friendAvatarUri"}, "status=?" , new String[]{"0"} ,  null ,  null ,  null );

        while (cursor.moveToNext()){
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("friendId", cursor.getString(cursor.getColumnIndex("id")));
            map.put("friendUserName", cursor.getString(cursor.getColumnIndex("friendusername")));
            map.put("friendNickName", cursor.getString(cursor.getColumnIndex("friendname")));
            map.put("friendAvatarUri", cursor.getString(cursor.getColumnIndex("friendAvatarUri")));
            newFriendAdapter.getNewFriendList().add(map);
            newFriendAdapter.notifyDataSetChanged();
        }

        cursor.close();
        //關閉數據庫
        db.close();
    }

    //存入新朋友陣列到sqlite中
    private void saveNewFriendListInSqlite(){
        //創建MyDBHelper對象
        MyDBHelper dbHelper = new MyDBHelper(getActivity(), "Chat.db", null, 1);
        //得到一個可讀的SQLiteDatabase對象
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        for(int i = 0 ; i < newFriendAdapter.getCount() ; i++) {
            //生成ContentValues​​對象 //key:列名，value:想插入的值
            ContentValues cv = new ContentValues();

            //cv.put("id", uniqueKey.toString());
            cv.put("friendusername", (String)newFriendAdapter.getItem(i).get("friendUserName"));
            cv.put("friendname","");
            cv.put("status", 0);//status 0 : 代表認證中
            cv.put("viewer", 0);
            cv.put("favorite", 0);

            //調用insert方法，將數據插入數據庫
            db.insert("Friend", null, cv);
        }
        //關閉數據庫
        db.close();
    }


    //存入新朋友
    private void saveNewFriendInSqlite(HashMap<String, Object> map){
        //創建MyDBHelper對象
        MyDBHelper dbHelper = new MyDBHelper(getActivity(), "Chat.db", null, 1);
        //得到一個可讀的SQLiteDatabase對象
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        ContentValues cv = new ContentValues();

        //往ContentValues​​對象存放數據，鍵-值對模式

        cv.put("id", (String)map.get("friendId"));
        cv.put("friendusername", (String)map.get("friendUserName"));
        cv.put("friendname",(String)map.get("friendNickName"));
        cv.put("friendAvatarUri", (String)map.get("friendAvatarUri"));
        cv.put("status", 0);//status 0 : 代表認證中
        cv.put("viewer", 0);
        cv.put("favorite", 0);

        //調用insert方法，將數據插入數據庫
        db.insert("Friend", null, cv);

        db.close();
    }
}
