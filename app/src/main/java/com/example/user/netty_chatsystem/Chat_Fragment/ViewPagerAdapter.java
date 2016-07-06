package com.example.user.netty_chatsystem.Chat_Fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2016/3/12.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments = new ArrayList<Fragment>();

    public ViewPagerAdapter(FragmentManager fm){
        super(fm);
    }

    public ViewPagerAdapter(FragmentManager fragmentManager,
                            List<Fragment>fragments){
        super(fragmentManager);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position){
        return fragments.get(position);
    }

    @Override
    public int getCount(){
        return fragments.size();
    }
}
