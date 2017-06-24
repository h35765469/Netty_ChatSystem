package com.cool.user.netty_chatsystem.Chat_Fragment;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cool.user.netty_chatsystem.Chat_Fragment.ChatFragment.ChatFragment;
import com.cool.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.Utils.BitmapUtils;
import com.cool.user.netty_chatsystem.ChatListViewAdapter.FriendListAdapter;
import com.cool.user.netty_chatsystem.ChatListViewAdapter.FriendRowItem;
import com.cool.user.netty_chatsystem.Chat_Sqlite_ChatHistory.MyDBHelper;
import com.cool.user.netty_chatsystem.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by user on 2016/9/20.
 */
public class SearchFragment extends Fragment {

    // List view
    private ListView listView;

    //input text
    private EditText inputSearch;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater ,ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.activity_search_, container, false);

        final Bundle globalBundle = getArguments();

        final TextView backTxt = (TextView) rootView.findViewById(R.id.backTxt);
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(),"fonts/fontawesome-webfont.ttf");//設定back的按紐
        backTxt.setTypeface(font);
        backTxt.setText("\uf060");

        backTxt.setOnClickListener(new View.OnClickListener() {
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

        listView = (ListView)rootView.findViewById(R.id.searchFriend_listview);
        inputSearch = (EditText)rootView.findViewById(R.id.search_edittext);



        final ArrayList<FriendRowItem>friendArrayList = loadFriendListInSqlite();
        final ArrayList<FriendRowItem>searchResultArrayList = new ArrayList<FriendRowItem>(friendArrayList);
        final FriendListAdapter friendListAdapter = new FriendListAdapter(getActivity(), searchResultArrayList);
        listView.setAdapter(friendListAdapter);



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("friendUserName", searchResultArrayList.get(position).getFriendUserName());
                bundle.putString("friendId", searchResultArrayList.get(position).getFriendId());
                bundle.putString("friendName", searchResultArrayList.get(position).getFriendName());
                bundle.putInt("whichFragment", globalBundle.getInt("whichFragment"));
                ChatFragment chatFragment = new ChatFragment();
                chatFragment.setArguments(bundle);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(globalBundle.getInt("whichFragment"), chatFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        /**
         * Enabling Search Filter
         * */
        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                String searchString = inputSearch.getText().toString();
                searchResultArrayList.clear();
                if(searchString.length() == 0){
                    searchResultArrayList.addAll(friendArrayList);
                }else{
                    for(FriendRowItem friendRowItem : friendArrayList){
                        if(friendRowItem.getFriendName().toLowerCase().contains(searchString)){
                            searchResultArrayList.add(friendRowItem);
                        }
                    }
                }
                friendListAdapter.notifyDataSetChanged();
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

        return rootView;
    }

    //載入在sqlite裡的朋友列
    private ArrayList<FriendRowItem> loadFriendListInSqlite(){
        ArrayList<FriendRowItem>friendRowItemArrayList = new ArrayList<>();

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
        Cursor cursor = db.query("Friend" ,  new  String[]{"id","friendusername" , "friendname", "friendAvatarUri"}, "status=?" , new String[]{"1"} ,  null ,  null ,  null );
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex("id"));
            String friendUserName = cursor.getString(cursor.getColumnIndex("friendusername"));
            String friendName = cursor.getString(cursor.getColumnIndex("friendname"));
            String friendAvatarUri = cursor.getString(cursor.getColumnIndex("friendAvatarUri"));
            FriendRowItem friendRowItem = new FriendRowItem(id, friendUserName, friendName);
            friendRowItem.setAvatarName(friendAvatarUri);
            friendRowItemArrayList.add(friendRowItem);
        }
        cursor.close();

        db.close();

        return friendRowItemArrayList;
    }
}
