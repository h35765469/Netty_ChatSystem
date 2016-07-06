package com.example.user.netty_chatsystem;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Search_Activity extends AppCompatActivity {
    // List view
    private ListView listView;

    //input text
    private EditText inputSearch;

    // Listview Adapter
    SimpleAdapter adapter;

    private int[] images = {R.mipmap.ic_launcher , R.drawable.arrows,R.drawable.attach , R.drawable.arrows_white};
    private String[] strings = {"HTC One X", "HTC Wildfire S", "HTC Sense" , "Dell Inspiron"};
    List<Map<String,Object>> addfriend_items;
    List<Map<String,Object>>searchResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_);
        getSupportActionBar().hide();

        final ImageView search_back_imageview = (ImageView)findViewById(R.id.search_back_imageview);
        search_back_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        listView = (ListView)findViewById(R.id.searchFriend_listview);
        inputSearch = (EditText)findViewById(R.id.search_edittext);

        addfriend_items = new ArrayList<Map<String,Object>>();


        for(int i = 0 ; i < images.length ; i++){
            Map<String,Object>addfriend_item = new HashMap<String,Object>();
            addfriend_item.put("image",images[i]);
            addfriend_item.put("string",strings[i]);
            addfriend_items.add(addfriend_item);
        }

        searchResults = new ArrayList<Map<String,Object>>(addfriend_items);


        adapter = new SimpleAdapter(this,searchResults,R.layout.resource_addfriend_listview ,new String[]{"image","string"},new int[]{
                R.id.addfriend_item_imageview,R.id.addfriend_item_textview
        });

        listView.setAdapter(adapter);

        /**
         * Enabling Search Filter
         * */
        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                String searchString = inputSearch.getText().toString();
                int textLength=searchString.length();
                //adapter.getFilter().filter(cs.toString());
                searchResults.clear();
                for(int i=0;i<addfriend_items.size();i++)
                {
                    String playerName=addfriend_items.get(i).get("string").toString();
                    if(textLength<=playerName.length()){
                        //compare the String in EditText with Names in the ArrayList
                        if(searchString.equalsIgnoreCase(playerName.substring(0,textLength)))
                            searchResults.add(addfriend_items.get(i));
                    }
                }

                adapter.notifyDataSetChanged();
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
    }
}
