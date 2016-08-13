package com.example.user.netty_chatsystem;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.example.user.netty_chatsystem.Chat_Fragment.Character_Fragment;
import com.example.user.netty_chatsystem.Chat_Fragment.Friendlist_Fragment;
import com.example.user.netty_chatsystem.Chat_Fragment.Messagelist_Fragment;
import com.example.user.netty_chatsystem.Chat_Fragment.Profile_Fragment;
import com.example.user.netty_chatsystem.Chat_Fragment.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class Character_Activity extends FragmentActivity {
    public static ViewPager mViewPager;
    private List<Fragment>mFragmentList = new ArrayList<Fragment>();
    private Messagelist_Fragment messagelist_fragment;
    private ViewPagerAdapter mViewPagerAdapter;




    @Override
    public  boolean onCreateOptionsMenu(Menu menu) {
        return  super .onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch(id){
            case android.R.id.home:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_template);
        mViewPager = (ViewPager)findViewById(R.id.container);
        init(savedInstanceState);
    }

    private void init(Bundle savedInstanceState ){
        messagelist_fragment = new Messagelist_Fragment();

        mFragmentList.add(new Profile_Fragment());
        mFragmentList.add(new Character_Fragment());
        mFragmentList.add(new Friendlist_Fragment());
        mFragmentList.add(messagelist_fragment);

        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),mFragmentList);
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setCurrentItem(1);

    }
}
