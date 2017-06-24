package com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.PersonalSettingFragment.BlockFriendFragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cool.user.netty_chatsystem.Chat_Sharepreference.SharePreferenceManager;
import com.cool.user.netty_chatsystem.Chat_Sqlite_ChatHistory.MyDBHelper;
import com.cool.user.netty_chatsystem.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user on 2016/9/23.
 */
public class BlockFriendFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater ,ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.blockfriend_fragment, container, false);
        ListView blockFriendListView = (ListView)rootView.findViewById(R.id.blockFriendListView);
        TextView backTxt = (TextView)rootView.findViewById(R.id.backTxt);

        SharePreferenceManager sharePreferenceManager = new SharePreferenceManager(getActivity().getApplicationContext());
        String loginId = sharePreferenceManager.getLoginId();

        Typeface font = Typeface.createFromAsset(getActivity().getAssets(),"fonts/fontawesome-webfont.ttf");//設定back的按紐
        backTxt.setTypeface(font);
        backTxt.setText("\uf060");
        backTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });

        ArrayList<HashMap<String,Object>>blockFriendList = new ArrayList<HashMap<String,Object>>();
        BlockFriendAdapter blockFriendAdapter = new BlockFriendAdapter(getActivity(),blockFriendList, loginId);
        loadBlockFriendInSqlite(blockFriendAdapter);
        blockFriendListView.setAdapter(blockFriendAdapter);

        return rootView;
    }

    //載入在sqlite裡新朋友名單
    private void loadBlockFriendInSqlite(BlockFriendAdapter blockFriendAdapter){
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
        Cursor cursor = db.query("Friend" ,  new  String[]{"id","friendname"}, "status=?", new String[] {"2"} ,  null ,  null ,  null );

        while (cursor.moveToNext()){
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("blockFriendId", cursor.getString(cursor.getColumnIndex("id")));
            map.put("blockName", cursor.getString(cursor.getColumnIndex("friendname")));
            blockFriendAdapter.getBlockFriendList().add(map);
        }

        cursor.close();
        //關閉數據庫
        db.close();
    }


}
