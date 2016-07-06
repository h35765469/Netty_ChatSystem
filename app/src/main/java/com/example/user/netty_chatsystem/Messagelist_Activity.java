package com.example.user.netty_chatsystem;

import android.app.ActionBar;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class Messagelist_Activity extends AppCompatActivity {

    //使用ActiionBar要宣告的
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
                Toast.makeText(Messagelist_Activity.this, "你點擊了搜尋紐+f", Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                Toast.makeText(Messagelist_Activity.this,"你點擊了搜尋紐=g",Toast.LENGTH_SHORT).show();
                return true;
            }
        };
        MenuItemCompat.setOnActionExpandListener(searchItem,expandListener);
        return  super .onCreateOptionsMenu(menu);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Messages");
        setContentView(R.layout.activity_messagelist_);
        ActionBar actionBar = getActionBar();
    }
}
