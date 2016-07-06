package com.example.user.netty_chatsystem;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.netty_chatsystem.Chat_Listview_Friendlist.CustomBaseAdapter;
import com.example.user.netty_chatsystem.Chat_Listview_Friendlist.CustomBaseAdapter_horizontal;
import com.example.user.netty_chatsystem.Chat_Listview_Friendlist.RowItem;

import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Friendlist_Activity extends AppCompatActivity {
    ListView FriendList_ListView;
    String[] array = {"123","456"};
    SimpleAdapter friend_adapter;

    public static final String[] titles = new String[] { "Strawberry fuck you asshole",
            "Banana", "Orange", "Mixed" };


    public static final Integer[] images = { R.drawable.bomb,
            R.drawable.search, R.drawable.desk, R.drawable.color_white };

    ListView listView;
    List<RowItem> rowItems;

    @Override
    public  boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_friendlist_titlebar , menu);

        //獲取MenuItem 給 action Item
        MenuItem searchItem = menu.findItem(R.id.user_p);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        //定義監聽器
         MenuItemCompat.OnActionExpandListener expandListener = new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                Toast.makeText(Friendlist_Activity.this,"你點擊了搜尋紐+f",Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                Toast.makeText(Friendlist_Activity.this,"你點擊了搜尋紐=g",Toast.LENGTH_SHORT).show();
                return true;
            }
        };
        MenuItemCompat.setOnActionExpandListener(searchItem,expandListener);
        return  super .onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.user_p:
                Toast.makeText(this,"你點擊了搜尋紐",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.home:
                finish();
                return true;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //設置標題攔title
        setTitle("Friends");
        setContentView(R.layout.activity_friendlist_);

        FriendList_ListView = (ListView)findViewById(R.id.friendlist_listview);

        final TwoWayView lvTest = (TwoWayView) findViewById(R.id.lvItems);

        //LoadFriend();

        rowItems = new ArrayList<RowItem>();
        for(int i = 0 ; i < titles.length ; i++){
            RowItem item = new RowItem(images[i], titles[i],array[i]);
            rowItems.add(item);
        }
        CustomBaseAdapter adapter = new CustomBaseAdapter(this,rowItems);
        CustomBaseAdapter_horizontal adapter_horizontal = new CustomBaseAdapter_horizontal(this,rowItems);
        FriendList_ListView.setAdapter(adapter);
        lvTest.setAdapter(adapter_horizontal);
    }

    //載入好友列表
    public void LoadFriend(){
        HashMap<String,Object>friends = null;
        List<HashMap<String , Object>>friends_list = new ArrayList<HashMap<String,Object>>();
        for(int i = 0 ; i < 2 ; i++){
            friends = new HashMap<String,Object>();
            friends.put("Id",array[i]);
            friends_list.add(friends);
        }
        friend_adapter = new SimpleAdapter(this,friends_list,R.layout.friend_profile,new String[]{"Id"},new int[]{R.id.title_text});
        FriendList_ListView.setAdapter(friend_adapter);

        FriendList_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*HashMap<String,String>friend_map = (HashMap<String,String>)FriendList_ListView.getItemAtPosition(position);
                String friend_id = friend_map.get("Id");
                Bundle bundle =new Bundle();
                bundle.putString("friend_id", friend_id);
                Intent it = new Intent();
                it.putExtras(bundle);
                it.setClass(Friendlist_Activity.this,Chat_Activity.class);
                startActivity(it);*/
            }
        });
    }
}
