package com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.AddFriendFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.cool.user.netty_chatsystem.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 2016/9/14.
 */
public class AddFriendFragment extends Fragment {

    private int[] images = {R.drawable.search_black};
    private String[] strings = {"搜尋使用者名稱增加"};
    ListView addFriendListView;
    ImageView addFriendBackImg;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater ,ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.activity_addfriend_, container, false);
        addFriendListView = (ListView)rootView.findViewById(R.id.addFriendListView);
        addFriendBackImg = (ImageView)rootView.findViewById(R.id.addFriendBackImg);

        List<Map<String,Object>> addFriendItems = new ArrayList<Map<String,Object>>();

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);

        for(int i = 0 ; i < images.length ; i++){
            Map<String,Object>addfriend_item = new HashMap<String,Object>();
            addfriend_item.put("image",images[i]);
            addfriend_item.put("string",strings[i]);
            addFriendItems.add(addfriend_item);
        }

        SimpleAdapter adapter = new SimpleAdapter(getActivity(),addFriendItems,R.layout.resource_addfriend_listview,new String[]{"image","string"},new int[]{
                R.id.addfriend_item_imageview,R.id.addfriend_item_textview
        });

        addFriendListView.setAdapter(adapter);

        //註冊搜尋添加好友的listview的監聽事件
        addFriendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: {
                        fragmentTransaction.replace(R.id.allContainer, new AddBySearchFragment());
                        break;
                    }
                    /*case 1: {
                        fragmentTransaction.replace(R.id.allContainer, new AddByFacebookFragment());
                        break;
                    }*/
                }
                fragmentTransaction.commit();
            }
        });

        //返回鈕
        addFriendBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0){
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });

        return rootView;
    }

}
