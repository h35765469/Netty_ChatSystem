package com.example.user.netty_chatsystem;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Phonebook_Activity extends AppCompatActivity {

    private String[] phonenames = { "哥哥" , "老媽" , "老爸"
    };

    private String[] phonenumbers = {"0922960566","0927063858","0938665799"};

    private int phoneadd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phonebook_);
        getSupportActionBar().hide();
        ImageView phonebook_back_imageview = (ImageView)findViewById(R.id.phonebook_back_imageview);
        EditText phonebook_search_edittext = (EditText)findViewById(R.id.phonebook_search_edittext);
        ListView phonebook_listview = (ListView)findViewById(R.id.phonebook_listview);

        List<Map<String,Object>> phonebook_items = new ArrayList<Map<String,Object>>();
        for(int i = 0 ; i < phonenames.length ;i++){
            Map<String,Object>phonebook_item = new HashMap<String,Object>();
            phonebook_item.put("phonename",phonenames[i]);
            phonebook_item.put("phonenumber",phonenumbers[i]);
            phonebook_items.add(phonebook_item);
        }

        SimpleAdapter adapter = new SimpleAdapter(this,phonebook_items,R.layout.resource_phonebook_listview,new String[]{"phonename","phonenumber"},new int[]{
           R.id.phonename_textview,R.id.phonenumber_textview
        });

        phonebook_listview.setAdapter(adapter);

        phonebook_back_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}
