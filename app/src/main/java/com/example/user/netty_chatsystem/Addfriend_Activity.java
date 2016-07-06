package com.example.user.netty_chatsystem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Addfriend_Activity extends AppCompatActivity {

    private int[] images = {R.drawable.search , R.drawable.phonebook,R.drawable.facebook , R.drawable.line};
    private String[] strings = {"搜尋使用者名稱增加" , "電話簿增加", "fackbook好友增加" , "line好友連接"};

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfriend_);
        getSupportActionBar().hide();
        final ListView addfriend_listview = (ListView)findViewById(R.id.addfriend_listview);
        final ImageView addfriend_back_imageview = (ImageView)findViewById(R.id.addfriend_back_imageview);
        List<Map<String,Object>>addfriend_items = new ArrayList<Map<String,Object>>();

        for(int i = 0 ; i < images.length ; i++){
            Map<String,Object>addfriend_item = new HashMap<String,Object>();
            addfriend_item.put("image",images[i]);
            addfriend_item.put("string",strings[i]);
            addfriend_items.add(addfriend_item);
        }

        SimpleAdapter adapter = new SimpleAdapter(this,addfriend_items,R.layout.resource_addfriend_listview,new String[]{"image","string"},new int[]{
                R.id.addfriend_item_imageview,R.id.addfriend_item_textview
        });

        addfriend_listview.setAdapter(adapter);

        //註冊搜尋添加好友的listview的監聽事件
        addfriend_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent it = new Intent();
                switch (position) {
                    case 0: {
                        it.setClass(Addfriend_Activity.this, Search_Activity.class);
                        startActivity(it);
                        break;
                    }
                    case 1: {
                        it.setClass(Addfriend_Activity.this,Phonebook_Activity.class);
                        startActivity(it);
                        break;
                    }
                    case 2: {

                    }
                    case 3: {

                    }


                }
            }
        });

        //返回鈕
        addfriend_back_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
