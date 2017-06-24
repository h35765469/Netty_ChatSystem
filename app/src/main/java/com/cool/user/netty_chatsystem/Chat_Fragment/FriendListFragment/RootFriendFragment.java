package com.cool.user.netty_chatsystem.Chat_Fragment.FriendListFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cool.user.netty_chatsystem.R;

/**
 * Created by user on 2017/3/7.
 */
public class RootFriendFragment extends Fragment{
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater ,ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_rootfriend, container, false);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //fragmentTransaction.replace(R.id.fragmentContainer, new Friendlist_Fragment());
        fragmentTransaction.replace(R.id.friendContainer, new Friendlist_Fragment());
        fragmentTransaction.commit();

        return rootView;
    }
}
