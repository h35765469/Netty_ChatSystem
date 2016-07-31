package com.example.user.netty_chatsystem.Chat_Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.user.netty_chatsystem.Character_Activity;
import com.example.user.netty_chatsystem.Chat_Activity;
import com.example.user.netty_chatsystem.Chat_Listview_Friendlist.CustomBaseAdapter;
import com.example.user.netty_chatsystem.Chat_Listview_Friendlist.RowItem;
import com.example.user.netty_chatsystem.Chat_Service.ChatBroadcastReceiver;
import com.example.user.netty_chatsystem.R;
import com.example.user.netty_chatsystem.Search_Activity;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by user on 2016/3/11.
 */
public class Messagelist_Fragment extends BaseFragment {

    public static final String[] titles = new String[] { "Strawberry",
            "Banana", "Orange", "Mixed" };


    public static final Integer[] images = { R.drawable.bomb,
            R.drawable.search, R.drawable.bomb, R.drawable.color_white };

    public String [] Id_array = {"123" , "456","789","465"};

    ChatBroadcastReceiver chatBroadcastReceiver = new ChatBroadcastReceiver();

    CustomBaseAdapter messageAdapter;

    ListView Messagelist_listview;

    Handler messageHandler = new Handler(){
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);

            RowItem rowItem = (RowItem)msg.getData().getSerializable("updateMessage");
            updateMessage(rowItem);
        }
    };

    @Override
    public View initView(LayoutInflater inflater){
        View view = inflater.inflate(R.layout.activity_messagelist_,null);
        setHasOptionsMenu(true);

        chatBroadcastReceiver.setChangeMessageListListener(new ChatBroadcastReceiver.ChangeMessageListListener() {
            @Override
            public void onChangeMessageListEvent(String from, String content) {
                RowItem rowItem = new RowItem(images[0],from,from);

                Bundle bundle = new Bundle();
                bundle.putSerializable("updateMessage", rowItem);
                Message msg = new Message();
                msg.setData(bundle);
                messageHandler.sendMessage(msg);
            }
        });


        final ImageView Messagelist_character_imageview = (ImageView)view.findViewById(R.id.friendlist_addfriend_imageview);
        final ImageView Messagelist_search_imageview = (ImageView)view.findViewById(R.id.messagelist_search_imageview);
        Messagelist_listview = (ListView)view.findViewById(R.id.messagelist_listview);


        List<RowItem>Rowitem_list = new ArrayList<RowItem>();
        for(int i = 0 ; i < titles.length ; i++){
            RowItem Rowitem = new RowItem(images[i],titles[i],Id_array[i]);
            Rowitem_list.add(Rowitem);
        }

        messageAdapter = new CustomBaseAdapter(getActivity(),Rowitem_list);

        Messagelist_search_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent();
                it.setClass(getActivity(), Search_Activity.class);
                startActivity(it);
            }
        });

        Messagelist_character_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Character_Activity().mViewPager.setCurrentItem(1);
            }
        });



        //設置處理message的訊息列條監聽
        Messagelist_listview.setAdapter(messageAdapter);
        Messagelist_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RowItem rowItem  = (RowItem)Messagelist_listview.getItemAtPosition(position);
                Bundle bundle = new Bundle();
                bundle.putString("friend_id", "123");
                Intent it = new Intent();
                it.putExtras(bundle);
                it.setClass(getActivity(), Chat_Activity.class);
                startActivity(it);
            }
        });


        return view;
    }

    @Override
    public void initData(Bundle savedInstanceState){

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_messagelist_titlebar,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.back_character:{
                new Character_Activity().mViewPager.setCurrentItem(1);
            }
        }
        return super.onOptionsItemSelected(item);
    }

   @Override
    public void onPrepareOptionsMenu(Menu menu){
   }

   public void updateMessage(RowItem rowItem){
       if(!messageAdapter.inMessage(rowItem)) {
           messageAdapter.addMessage(rowItem);
       }
       messageAdapter.notifyDataSetChanged();
       scroll();
   }
    private void scroll() {
        Messagelist_listview.setSelection(Messagelist_listview.getCount() - 1);
    }
}
