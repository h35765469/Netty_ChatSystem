package com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cool.user.netty_chatsystem.R;

/**
 * Created by user on 2016/9/14.
 */
public class RootProfileFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater ,ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.rootprofile_fragment, container, false);
        Bundle bundle = getArguments();//從MainFragment來的為了點擊上方提醒欄後可以直接進入新朋友頁面
        Profile_Fragment profileFragment = new Profile_Fragment();
        if(bundle != null){
            profileFragment.setArguments(bundle);
        }
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //fragmentTransaction.replace(R.id.fragmentContainer, profileFragment);
        fragmentTransaction.replace(R.id.profileContainer, profileFragment);
        fragmentTransaction.commit();

        return rootView;
    }
}
